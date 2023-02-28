/*
 * Copyright 2022-2023 Richard Linsdale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.picoc;

import java.io.File;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import uk.theretiredprogrammer.activity.Activity;
import static uk.theretiredprogrammer.activity.Activity.RX;
import static uk.theretiredprogrammer.activity.Activity.STDERR;
import static uk.theretiredprogrammer.activity.Activity.STDOUT;
import static uk.theretiredprogrammer.activity.Activity.TX;
import uk.theretiredprogrammer.util.ActionsAndActivitiesFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

public class PicoCBuildWorkers {

    private final FileObject buildfolder;
    private final String iotabname;

    public PicoCBuildWorkers(String iotabname, FileObject buildfolder) {
        this.iotabname = iotabname;
        this.buildfolder = buildfolder;
    }

    public final void showSerialTerminal(String iotabname) {
        SerialActivity serial= new SerialActivity(iotabname,"/dev/serial0", 115200);
        serial.open();
        Activity activity;
            try {
                activity = ActionsAndActivitiesFactory.createActivity()
                        .setDuplexTransfer(serial.getOutputStreamSupplier(), serial.getInputStreamSupplier(), ()->serial.close())
                        .needsIOTab(iotabname)
                        .inputFromIOSTDIN(TX)
                        .outputToIOSTDOUT(RX);
            } catch (ApplicationException ex) {
                UserReporting.exceptionWithMessage(iotabname, "Error initialising the serial terminal", ex);
                return;
            }
            activity.run();
    }

    public final void cleanBuildFolder() {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        try {
            ActionsAndActivitiesFactory.getActivityIOTab(iotabname).println("Cleaning Build Folder");
            for (FileObject content : buildfolder.getChildren()) {
                content.delete();
            }
            ActionsAndActivitiesFactory.getActivityIOTab(iotabname).printdone();
        } catch (IOException | ApplicationException ex) {
            UserReporting.exception(iotabname, ex);
        }
    }

    public final void buildMakeFile() {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        FileObject cmaketxt = buildfolder.getParent().getFileObject("CMakeLists.txt");
        if (cmaketxt != null && cmaketxt.isData()) {
            Activity activity;
            try {
                activity = ActionsAndActivitiesFactory.createActivity()
                        .setExternalProcess("cmake", "..", buildfolder)
                        .needsIOTab(iotabname)
                        .outputToIOSTDERR(STDERR)
                        .outputToIOSTDOUT(STDOUT);
            } catch (ApplicationException ex) {
                UserReporting.exceptionWithMessage(iotabname, "Error initialising the cmake activity", ex);
                return;
            }
            activity.run("Creating Make file");
        } else {
            UserReporting.error(iotabname, "Cannot create Make File - CMakeLists.txt file is missing");
        }
    }

    public final void buildExecutables() {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        FileObject make = buildfolder.getFileObject("Makefile");
        if (make != null && make.isData()) {
            Activity activity;
            try {
                activity = ActionsAndActivitiesFactory.createActivity()
                        .setExternalProcess("make", "", buildfolder)
                        .needsIOTab(iotabname)
                        .outputToIOSTDERR(STDERR)
                        .outputToIOSTDOUT(STDOUT);
            } catch (ApplicationException ex) {
                UserReporting.exceptionWithMessage(iotabname, "Error initialising the make activity", ex);
                return;
            }
            activity.run("Building executables");
        } else {
            UserReporting.error(iotabname, "Cannot build executables - Makefile is missing");
        }
    }

    public final void downloadViaDebug(String buildname) {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        String executablepath = getExecutablePath(buildname, "elf");
        Activity activity;
        try {
            activity = ActionsAndActivitiesFactory.createActivity()
                    .setExternalProcess("openocd",
                            "-f /home/richard/pico/openocd/tcl/interface/raspberrypi-swd.cfg "
                            + "-f /home/richard/pico/openocd/tcl/target/rp2040.cfg "
                            + "-c \"program " + executablepath + " verify reset exit\"",
                            buildfolder)
                    .needsIOTab(iotabname)
                    .outputToIOSTDERR(STDERR)
                    .outputToIOSTDOUT(STDOUT);
        } catch (ApplicationException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error initialising the downloading via debug port activity", ex);
            return;
        }
        activity.run("Downloading " + buildname + ".elf via debug port");
    }

    public final void downloadViaBootLoader(String buildname) {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        try {
            ActionsAndActivitiesFactory.getActivityIOTab(iotabname).println("Downloading " + buildname + ".uf2 via Boot Loader");
            loadUsingBootLoader(buildname);
            ActionsAndActivitiesFactory.getActivityIOTab(iotabname).printdone();
        } catch (ApplicationException ex) {
            UserReporting.exception(iotabname, ex);
        }
    }

    private String getExecutablePath(String buildname, String ext) {
        return FileUtil.toFile(getExecutable(buildname, ext)).getAbsolutePath();
    }

    private FileObject getExecutable(String buildname, String ext) {
        return buildfolder.getFileObject(buildname, ext);
    }

    public void loadUsingBootLoader(String buildname) throws ApplicationException {
        FileObject uf2file = getExecutable(buildname, "uf2");
        if (uf2file != null && uf2file.isData()) {
            File picobootloaderfs = new File("/media/richard/RPI-RP2");
            FileObject picobootloader = FileUtil.toFileObject(picobootloaderfs);
            if (picobootloader != null && picobootloader.isFolder()) {
                try {
                    FileUtil.copyFile(uf2file, picobootloader, uf2file.getName());
                } catch (IOException ex) {
                    throw new ApplicationException("Boot Loader - failure during file copy", ex);
                }
            } else {
                throw new ApplicationException("Bootloader is not enabled - operate BOOTSEL with Reset to mount");
            }
        } else {
            throw new ApplicationException("Cannot download via bootloader - .uf2 is missing");
        }
    }
}
