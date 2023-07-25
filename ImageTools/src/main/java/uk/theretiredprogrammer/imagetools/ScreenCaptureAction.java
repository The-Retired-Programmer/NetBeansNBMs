/*
 * Copyright 2022-23 Richard Linsdale.
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
package uk.theretiredprogrammer.imagetools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.windows.TopComponent;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.screencapture.ScreenCapture;
import static uk.theretiredprogrammer.screencapture.ScreenCapture.Priority.FILE;
import static uk.theretiredprogrammer.screencapture.ScreenCapture.Priority.PROJECT;
import uk.theretiredprogrammer.util.ActionsAndActivitiesFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

@ActionID(
        category = "Tools",
        id = "uk.theretiredprogrammer.imagetools.ScreenCaptureAction"
)
@ActionRegistration(
        iconBase = "uk/theretiredprogrammer/imagetools/camera.png",
        displayName = "#CTL_ScreenCaptureAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 0, separatorAfter = 50),
    @ActionReference(path = "Toolbars/ScreenCapture", position = 0)
})
@Messages("CTL_ScreenCaptureAction=Screen Capture")
public final class ScreenCaptureAction implements ActionListener, Runnable {

    private static final RequestProcessor RP = new RequestProcessor("screen capture");

    @Override
    public void actionPerformed(ActionEvent e) {
        RP.post(this);
    }

    @Override
    public void run() {

        ScreenCapture projectscreencapture = null;
        ScreenCapture filescreencapture = null;

        try {
            TopComponent activeTC = TopComponent.getRegistry().getActivated();
            if (activeTC != null) {
                DataObject dataObject = activeTC.getLookup().lookup(DataObject.class);
                if (dataObject != null) {
                    FileObject input = dataObject.getPrimaryFile();
                    Project parent = FileOwnerQuery.getOwner(input);
                    if (parent != null) {
                        projectscreencapture = getProjectScreenCapture(parent);
                    }
                    filescreencapture = getFileScreenCapture(input);
                }
            }

            if (filescreencapture != null) {
                if (projectscreencapture != null) {
                    if (projectscreencapture.getPriority() == PROJECT) {
                        captureScreen(projectscreencapture);
                    } else {
                        captureScreen(filescreencapture);
                    }
                } else {
                    captureScreen(filescreencapture);
                }
            } else {
                if (projectscreencapture != null) {
                    captureScreen(projectscreencapture);
                } else {
                    captureScreen(getBasicScreenCapture());
                }
            }
        } catch (ClassNotFoundException | IOException ex) {
            UserReporting.exception("Screen Capture", ex);
        }
    }

    private void captureScreen(ScreenCapture screencapture) {
        FileObject capturefolder = screencapture.getCaptureFolder();
        String capturefilepath = screencapture.getCaptureFilePath();
        Activity activity;
        try {
            activity = ActionsAndActivitiesFactory.createActivity()
                    .setExternalProcess("scrot", "-s " + capturefilepath, capturefolder)
                    .needsIOTab(screencapture.getIoTabname())
                    .stderrToIOSTDERR();
        } catch (ApplicationException ex) {
            UserReporting.exceptionWithMessage(screencapture.getIoTabname(), "Error when configuring Screen Capture Activity", ex);
            return;
        }
        activity.run("Screen Capture:  " + capturefilepath);
    }

    private ScreenCapture getProjectScreenCapture(Project project) throws ClassNotFoundException, IOException {
        if (project != null) {
            String projecttype = project.getClass().getTypeName().replace(".", "-");
            FileObject instance = FileUtil.getConfigRoot().getFileObject("ScreenCapture/Projects/" + projecttype + "/ScreenCapture.instance");
            if (instance != null) {
                DataObject ob;
                ob = DataObject.find(instance);
                InstanceCookie ck = ob.getLookup().lookup(InstanceCookie.class);
                if (ck != null) {
                    Object instanceobject = ck.instanceCreate();
                    if (instanceobject instanceof ScreenCapture) {
                        ScreenCapture screencapture = (ScreenCapture) instanceobject;
                        screencapture.setCaptureBaseFolder(project.getProjectDirectory());
                        return screencapture;
                    }
                }
            }
        }
        return null;
    }

    private ScreenCapture getFileScreenCapture(FileObject fileobj) throws ClassNotFoundException, IOException {
        String mimetype = fileobj.getMIMEType();
        FileObject instance = FileUtil.getConfigRoot().getFileObject("ScreenCapture/Files/" + mimetype + "/ScreenCapture.instance");
        if (instance != null) {
            DataObject ob;
            ob = DataObject.find(instance);
            InstanceCookie ck = ob.getLookup().lookup(InstanceCookie.class);
            if (ck != null) {
                Object instanceobject = ck.instanceCreate();
                if (instanceobject instanceof ScreenCapture) {
                    ScreenCapture screencapture = (ScreenCapture) instanceobject;
                    screencapture.setCaptureBaseFolder(fileobj.getParent());
                    return screencapture;
                }
            }
        }
        return null;
    }

    private ScreenCapture getBasicScreenCapture() throws IOException {
        ScreenCapture basicscreencapture = new BasicScreenCapture();
        basicscreencapture.setCaptureBaseFolder(FileUtil.toFileObject(new File(System.getProperty("user.home", "?"))));
        return basicscreencapture;
    }

    private class BasicScreenCapture extends ScreenCapture {

        public BasicScreenCapture() {
            super("screenshots", "img", "png", "Screen Capture", FILE);
        }
    }
}
