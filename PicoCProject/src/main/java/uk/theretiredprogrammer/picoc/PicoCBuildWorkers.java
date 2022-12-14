/*
 * Copyright 2022 Richard Linsdale.
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
import uk.theretiredprogrammer.actionssupport.NbCliDescriptor;
import uk.theretiredprogrammer.actionssupport.UserReporting;

public class PicoCBuildWorkers {

    private final FileObject buildfolder;
    private final String iotabname;

    public PicoCBuildWorkers(String iotabname, FileObject buildfolder) {
        this.iotabname = iotabname;
        this.buildfolder = buildfolder;
    }

    public final void cleanBuildFolder() {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        UserReporting.startmessage(iotabname, "Cleaning Build Folder");
        try {
            for (FileObject content : buildfolder.getChildren()) {
                content.delete();
            }
        } catch (IOException ex) {
            UserReporting.exception(iotabname, ex);
            return;
        }
        UserReporting.completedmessage(iotabname);
    }

    public final void buildMakeFile() {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        FileObject cmaketxt = buildfolder.getParent().getFileObject("CMakeLists.txt");
        if (cmaketxt != null && cmaketxt.isData()) {
            new NbCliDescriptor(buildfolder, "cmake", "..")
                    .stderrToIO()
                    .stdoutToIO()
                    .ioTabName(iotabname)
                    .exec("Creating Make file");
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
            new NbCliDescriptor(buildfolder, "make", "")
                    .stderrToIO()
                    .stdoutToIO()
                    .ioTabName(iotabname)
                    .exec("Building executables");
        } else {
            UserReporting.error(iotabname, "Cannot build executables - Makefile is missing");
        }
    }

    public final void downloadViaDebug() {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        String executablepath = getExecutablePath("elf");
        new NbCliDescriptor(buildfolder, "openocd",
                "-f /home/richard/pico/openocd/tcl/interface/raspberrypi-swd.cfg "
                + "-f /home/richard/pico/openocd/tcl/target/rp2040.cfg "
                + "-c \"program " + executablepath + " verify reset exit\"")
                .stderrToIO()
                .stdoutToIO()
                .ioTabName(iotabname)
                .exec("Downloading via debug port");
    }

    public final void downloadViaBootLoader() {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        UserReporting.startmessage(iotabname, "Download via Boot Loader");
        FileObject uf2file = getExecutable("uf2");
        if (uf2file != null && uf2file.isData()) {
            File picobootloaderfs = new File("/media/richard/RPI-RP2");
            FileObject picobootloader = FileUtil.toFileObject(picobootloaderfs);
            if (picobootloader != null && picobootloader.isFolder()) {
                try {
                    FileUtil.copyFile(uf2file, picobootloader, uf2file.getName());
                } catch (IOException ex) {
                    UserReporting.exceptionWithMessage(iotabname, "Boot Loader - failure during file copy", ex);
                    return;
                }
                UserReporting.completedmessage(iotabname);
            } else {
                UserReporting.error(iotabname, "Bootloader is not enabled - operate BOOTSEL with Reset to mount");
            }
        } else {
            UserReporting.error(iotabname, "Cannot download via bootloader - .uf2 is missing");
        }
    }

    private String getExecutablePath(String ext) {
        return FileUtil.toFile(getExecutable(ext)).getAbsolutePath();
    }

    private FileObject getExecutable(String ext) {
        return buildfolder.getFileObject("app", ext);  // needs extension in future - only does app.uf2/app.elf
    }

}
