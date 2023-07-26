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

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Screen Capture is a descriptor that a project can include in a layer file for
 * both Project Type or File Type usages, to describe the actions of the screen
 * capture Action of the ImageTools Module.
 *
 * The project or file descriptors extend this class, having a constructor
 * without parameters, but configure this class via a call to this class's
 * constructor.
 *
 */
public abstract class ScreenCapture {

    /**
     * Describes the priority when both project and file type descriptors could
     * be applied. The default situation is that File type will have priority,
     * but a Project type may be configured to to have priority.
     */
    public enum Priority {

        /**
         * Project Type has PRIORITY
         */
        PROJECT,
        /**
         * File Type has PRIORITY
         */
        FILE
    };

    private FileObject capturefolder;
    private final String capturefolderpath;
    private final String filenameroot;
    private final String ext;
    private final String iotabname;
    private final Priority priority;

    /**
     * Configures the Descriptor.
     *
     * The descriptor is used to locate the folder where the images will be
     * saved (the capture folder). It also describes the components that make up
     * the image file name.
     *
     * This name will be of the form name_nnnn.iii, where name is the base name
     * of the file, nnn is an original number for each file in the folder and
     * iii is the image type.
     *
     * The location of the capture folder is defined as a path relative to root
     * folder. (If this is a project type descriptor, then the root folder will
     * be the project folder. If this is a file type descriptor, then the root
     * folder will be the parent folder of the file.)
     *
     * @param capturefolderpath The relative path from root folder to the actual
     * capture folder
     * @param filenameroot the base name of the capture file
     * @param ext the file extension of the capture file
     * @param iotabname the IO tab name to be used for this reporting - normally
     * "Screen Capture"
     * @param priority the priority associated with this descriptor
     */
    public ScreenCapture(String capturefolderpath, String filenameroot, String ext, String iotabname, Priority priority) {
        this.capturefolderpath = capturefolderpath;
        this.priority = priority;
        this.filenameroot = filenameroot;
        this.ext = ext;
        this.iotabname = iotabname;
    }

    /**
     * Used by the Screen Capture Action to define the root folder for each
     * capture.
     *
     * It creates any missing folders in the relative path to the capture
     * folder.
     *
     * @param capturebasefolder the root capture folder
     * @throws IOException if problems
     */
    public void setCaptureBaseFolder(FileObject capturebasefolder) throws IOException {
        capturefolder = capturebasefolder.getFileObject(capturefolderpath);
        if (capturefolder == null) {
            capturefolder = capturebasefolder;
            for (var part : capturefolderpath.split("/")) {
                FileObject fonext = capturefolder.getFileObject(part);
                if (fonext == null) {
                    fonext = capturefolder.createFolder(part);
                }
                capturefolder = fonext;
            }
        }
    }

    /**
     * Get the Capture Folder.
     *
     * @return the capture folder
     */
    public FileObject getCaptureFolder() {
        return capturefolder;
    }

    /**
     * Get the Capture FilePath.
     *
     * Gets the absolute file path for the image file to be use in the next
     * Screen Capture.
     *
     * @return the file path
     */
    public String getCaptureFilePath() {
        capturefolder.refresh();
        return FileUtil.toFile(capturefolder).getAbsoluteFile() + "/"
                + FileUtil.findFreeFileName(capturefolder, filenameroot, ext)
                + "." + ext;
    }

    /**
     * Get the IO tab name for any reporting associated with Screen Capture.
     *
     * @return the Io Tab name
     */
    public String getIoTabname() {
        return iotabname;
    }

    /**
     * Get the Priority to be applied when possibly using this description.
     *
     * @return the priority (PROJECT or FILE)
     */
    public Priority getPriority() {
        return priority;
    }
}
