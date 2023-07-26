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
import uk.theretiredprogrammer.util.ActivitiesAndActionsFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

public class PicoCBuildWorkers {

    private final FileObject buildfolder;
    private final String iotabname;

    public PicoCBuildWorkers(String iotabname, FileObject buildfolder) {
        this.iotabname = iotabname;
        this.buildfolder = buildfolder;
    }

    public final void showSerialTerminal(String iotabname, PicoCPropertyFile picocproperties) {
        Activity activity;
        try {
            activity = ActivitiesAndActionsFactory.createActivity()
                    .setDevice(new SerialDeviceDescriptor(picocproperties.getDevicename(),
                            picocproperties.getBaudrate(), picocproperties.getEncoding()))
                    .needsIOTab(iotabname)
                    .stdinFromIOSTDIN()
                    .stdoutToIOSTDOUT();
        } catch (ApplicationException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error initialising the serial terminal activity", ex);
            return;
        }
        activity.run();
    }

    public final void cleanBuildFolder() {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        Activity activity;
        try {
            activity = ActivitiesAndActionsFactory.createActivity()
                    .setMethod(() -> cleanbuildfolder())
                    .needsIOTab(iotabname);
        } catch (ApplicationException ex) {
            UserReporting.exception(iotabname, ex);
            return;
        }
        activity.run("Cleaning Build Folder");
    }

    private void cleanbuildfolder() {
        try {
            for (FileObject content : buildfolder.getChildren()) {
                content.delete();
            }
        } catch (IOException ex) {
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
                activity = ActivitiesAndActionsFactory.createActivity()
                        .setExternalProcess("cmake", "..", buildfolder)
                        .needsIOTab(iotabname)
                        .stderrToIOSTDERR()
                        .stdoutToIOSTDOUT();
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
                activity = ActivitiesAndActionsFactory.createActivity()
                        .setExternalProcess("make", "", buildfolder)
                        .needsIOTab(iotabname)
                        .stderrToIOSTDERR()
                        .stdoutToIOSTDOUT();
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
        String userhome = System.getProperty("user.home", "?");
        String executablepath = getExecutablePath(buildname, "elf");
        Activity activity;
        try {
            activity = ActivitiesAndActionsFactory.createActivity()
                    .setExternalProcess("openocd",
                            "-f " + userhome + "/pico/openocd/tcl/interface/raspberrypi-swd.cfg "
                            + "-f " + userhome + "/pico/openocd/tcl/target/rp2040.cfg "
                            + "-c \"program " + executablepath + " verify reset exit\"",
                            buildfolder)
                    .needsIOTab(iotabname)
                    .stderrToIOSTDERR()
                    .stdoutToIOSTDOUT();
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
        Activity activity;
        try {
            activity = ActivitiesAndActionsFactory.createActivity()
                    .setMethod(() -> loadUsingBootLoader(buildname))
                    .needsIOTab(iotabname);
        } catch (ApplicationException ex) {
            UserReporting.exception(iotabname, ex);
            return;
        }
        activity.run("Downloading " + buildname + ".uf2 via Boot Loader");
    }

    private String getExecutablePath(String buildname, String ext) {
        return FileUtil.toFile(getExecutable(buildname, ext)).getAbsolutePath();
    }

    private FileObject getExecutable(String buildname, String ext) {
        return buildfolder.getFileObject(buildname, ext);
    }

    public void loadUsingBootLoader(String buildname) {
        String username = System.getProperty("user.name", "?");
        FileObject uf2file = getExecutable(buildname, "uf2");
        if (uf2file != null && uf2file.isData()) {
            File picobootloaderfs = new File("/media/" + username + "/RPI-RP2");
            FileObject picobootloader = FileUtil.toFileObject(picobootloaderfs);
            if (picobootloader != null && picobootloader.isFolder()) {
                try {
                    FileUtil.copyFile(uf2file, picobootloader, uf2file.getName());
                } catch (IOException ex) {
                    UserReporting.exceptionWithMessage(iotabname, "Boot Loader - failure during file copy", ex);
                }
            } else {
                UserReporting.error(iotabname, "Bootloader is not enabled - operate BOOTSEL with Reset to mount");
            }
        } else {
            UserReporting.error(iotabname, "Cannot download via bootloader - .uf2 is missing");
        }
    }
}
