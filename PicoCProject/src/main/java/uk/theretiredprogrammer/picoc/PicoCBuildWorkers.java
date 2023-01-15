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
import uk.theretiredprogrammer.activity.ActivityIO;
import uk.theretiredprogrammer.actionssupport.UserReporting;
import static uk.theretiredprogrammer.activity.ActivityIO.STDERR;
import static uk.theretiredprogrammer.activity.ActivityIO.STDOUT;

public class PicoCBuildWorkers {

    private final FileObject buildfolder;
    private final String iotabname;

    public PicoCBuildWorkers(String iotabname, FileObject buildfolder) {
        this.iotabname = iotabname;
        this.buildfolder = buildfolder;
    }

    public final void showSerialTerminal(String iotabname) {
        Activity.runWithIOTab(new SerialActivity(
                "/dev/serial0", 115200,
                new ActivityIO()
                        .inputs("Tx")
                        .outputs("Rx")
                        .inputFromIOSTDIN("Tx")
                        .outputToIOSTDOUT("Rx")
                        .ioTabName(iotabname)));
    }

    public final void cleanBuildFolder() {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        Activity.runWithIOTab(
                new CleanBuildFolderActivity(new ActivityIO().ioTabName(iotabname)),
                "Cleaning Build Folder");
    }

    public final void buildMakeFile() {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        FileObject cmaketxt = buildfolder.getParent().getFileObject("CMakeLists.txt");
        if (cmaketxt != null && cmaketxt.isData()) {
            Activity.runExternalProcessWithIOTab("cmake", "..", buildfolder,
                    new ActivityIO()
                            .outputToIOSTDERR(STDERR)
                            .outputToIOSTDOUT(STDOUT)
                            .ioTabName(iotabname),
                    "Creating Make file"
            );
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
            Activity.runExternalProcessWithIOTab("make", "", buildfolder,
                    new ActivityIO()
                            .outputToIOSTDERR(STDERR)
                            .outputToIOSTDOUT(STDOUT)
                            .ioTabName(iotabname),
                    "Building executables"
            );
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
        Activity.runExternalProcessWithIOTab("openocd",
                "-f /home/richard/pico/openocd/tcl/interface/raspberrypi-swd.cfg "
                + "-f /home/richard/pico/openocd/tcl/target/rp2040.cfg "
                + "-c \"program " + executablepath + " verify reset exit\"",
                buildfolder,
                new ActivityIO()
                        .outputToIOSTDERR(STDERR)
                        .outputToIOSTDOUT(STDOUT)
                        .ioTabName(iotabname),
                "Downloading " + buildname + ".elf via debug port"
        );
    }

    public final void downloadViaBootLoader(String buildname) {
        if (buildfolder == null) {
            UserReporting.error(iotabname, "Build folder is not present");
            return;
        }
        Activity.runWithIOTab(
                new DownloadViaBootLoaderActivity(buildname, new ActivityIO().ioTabName(iotabname)),
                "Downloading " + buildname + ".uf2 via Boot Loader");
    }

    private String getExecutablePath(String buildname, String ext) {
        return FileUtil.toFile(getExecutable(buildname, ext)).getAbsolutePath();
    }

    private FileObject getExecutable(String buildname, String ext) {
        return buildfolder.getFileObject(buildname, ext);
    }

    private class CleanBuildFolderActivity extends Activity {

        public CleanBuildFolderActivity(ActivityIO activityio) {
            super(activityio);
        }

        @Override
        public void onActivity() {
            try {
                for (FileObject content : buildfolder.getChildren()) {
                    content.delete();
                }
            } catch (IOException ex) {
                UserReporting.exception(iotabname, ex);
            }
        }
    }

    private class DownloadViaBootLoaderActivity extends Activity {

        private final String buildname;

        public DownloadViaBootLoaderActivity(String buildname, ActivityIO activityio) {
            super(activityio);
            this.buildname = buildname;
        }

        @Override
        public void onActivity() {
            FileObject uf2file = getExecutable(buildname, "uf2");
            if (uf2file != null && uf2file.isData()) {
                File picobootloaderfs = new File("/media/richard/RPI-RP2");
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
}
