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
package uk.theretiredprogrammer.screencapture;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public abstract class ScreenCapture {

    public enum Priority {
        PROJECT, FILE
    };

    private FileObject capturefolder;
    private final String capturefolderpath;
    private final String filenameroot;
    private final String ext;
    private final String iotabname;
    private final Priority priority;

    public ScreenCapture(String capturefolderpath, String filenameroot, String ext, String iotabname, Priority priority) {
        this.capturefolderpath = capturefolderpath;
        this.priority = priority;
        this.filenameroot = filenameroot;
        this.ext = ext;
        this.iotabname = iotabname;
    }
    
    public void setCaptureBaseFolder(FileObject capturebasefolder) {
        capturefolder = capturebasefolder.getFileObject(capturefolderpath);
    }

    public FileObject getCaptureFolder() {
        return capturefolder;
    }

    public String getCaptureFilePath() {
        capturefolder.refresh();
        return FileUtil.toFile(capturefolder).getAbsoluteFile() + "/"
                + FileUtil.findFreeFileName(capturefolder, filenameroot, ext)
                + "." + ext;
    }

    public String getIoTabname() {
        return iotabname;
    }

    public Priority getPriority() {
        return priority;
    }
}
