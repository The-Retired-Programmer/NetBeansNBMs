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

import java.io.File;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;
import uk.theretiredprogrammer.image.api.ImageManager;
import uk.theretiredprogrammer.image.api.ScreenCaptureDescriptor;

@ServiceProvider(service=ImageManager.class)
public class ImageManagerImpl implements ImageManager {

    private static boolean reserved = false;
    private static ScreenCaptureDescriptor currentscreencapturedescriptor;
    private static final ScreenCaptureDescriptorImpl UNRESERVEDSCREENCAPTUREDESCRIPTOR;

    static {
        UNRESERVEDSCREENCAPTUREDESCRIPTOR = new ScreenCaptureDescriptorImpl(
                FileUtil.toFileObject(new File("/home/richard/SCREENCAPTURE/")),
                "screen_capture", "png", "Screen Capture");
        currentscreencapturedescriptor = UNRESERVEDSCREENCAPTUREDESCRIPTOR;
    }
    
    public static ScreenCaptureDescriptor getCurrentScreenCaptureDescriptor() {
        return currentscreencapturedescriptor;
    }

    @Override
    public ScreenCaptureDescriptor createScreenCaptureDescriptor(FileObject capturefolder, String imagefilenameroot, String imageext, String iotabname) {
        return new ScreenCaptureDescriptorImpl(capturefolder, imagefilenameroot, imageext, iotabname);
    }

    @Override
    public boolean gainDedicatedUse(ScreenCaptureDescriptor descriptor) {
        if (reserved) {
            return false;
        }
        reserved = true;
        currentscreencapturedescriptor = descriptor;
        return true;
    }

    @Override
    public boolean dropDedicatedUse(ScreenCaptureDescriptor descriptor) {
        if (reserved && descriptor == currentscreencapturedescriptor) {
            reserved = false;
            currentscreencapturedescriptor = UNRESERVEDSCREENCAPTUREDESCRIPTOR;
            return true;
        }
        return false;
    }
}
