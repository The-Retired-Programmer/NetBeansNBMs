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
package uk.theretiredprogrammer.image.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.actionssupport.NbCliDescriptor;
import uk.theretiredprogrammer.image.ImageManagerImpl;
import uk.theretiredprogrammer.image.api.ScreenCaptureDescriptor;

@ActionID(
        category = "Tools",
        id = "uk.theretiredprogrammer.image.ScreenCaptureAction"
)
@ActionRegistration(
        iconBase = "uk/theretiredprogrammer/image/camera.png",
        displayName = "#CTL_ScreenCaptureAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 0, separatorAfter = 50),
    @ActionReference(path = "Toolbars/ScreenCapture", position = 0)
})
@Messages("CTL_ScreenCaptureAction=Screen Capture")
public final class ScreenCaptureAction implements ActionListener, Runnable {

    @Override
    public void actionPerformed(ActionEvent e) {
        RequestProcessor rp = new RequestProcessor("screen capture");
        rp.post(this);
    }

    @Override
    public void run() {
        ScreenCaptureDescriptor screencapturedescriptor = ImageManagerImpl.getCurrentScreenCaptureDescriptor();
        String capturefilepath = screencapturedescriptor.getCaptureFilePath();
        new NbCliDescriptor(screencapturedescriptor.getCaptureFolder(), "scrot", "-s " + capturefilepath)
                .stderrToIO()
                .ioTabName(screencapturedescriptor.getIoTabname())
                .exec("Screen Capture:  " + capturefilepath);
    }
}
