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
package uk.theretiredprogrammer.image;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import uk.theretiredprogrammer.image.api.ScreenCaptureDescriptor;

public class ScreenCaptureDescriptorImpl implements ScreenCaptureDescriptor {

    private final FileObject capturefolder;
    private final String imagefilenameroot;
    private final String imageext;
    private final String iotabname;

    public ScreenCaptureDescriptorImpl(FileObject capturefolder, String imagefilenameroot, String imageext, String iotabname) {
        this.capturefolder = capturefolder;
        this.imagefilenameroot = imagefilenameroot;
        this.imageext = imageext;
        this.iotabname = iotabname;
    }

    public FileObject getCaptureFolder() {
        return capturefolder;
    }

    public String getCaptureFilePath() {
        return FileUtil.toFile(capturefolder).getAbsoluteFile() + "/"
                + FileUtil.findFreeFileName(capturefolder, imagefilenameroot, imageext)
                + "." + imageext;
    }

    public String getIoTabname() {
        return iotabname;
    }
}
