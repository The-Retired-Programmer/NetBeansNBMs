/*
 * Copyright 2023 richard linsdale.
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
import java.util.List;
import javax.swing.JButton;
import org.openide.DialogDescriptor;
import static org.openide.DialogDescriptor.RIGHT_ALIGN;
import org.openide.DialogDisplayer;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.actionssupport.UserReporting;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.activity.ActivityIO;
import static uk.theretiredprogrammer.activity.ActivityIO.STDERR;

@ActionID(
        category = "Tools",
        id = "uk.theretiredprogrammer.image.actions.ConvertImageAction"
)
@ActionRegistration(
        iconBase = "uk/theretiredprogrammer/image/actions/arrow_divide.png",
        displayName = "#CTL_ConvertImageAction"
)
@ActionReference(path = "Loaders/image/png-gif-jpeg-bmp/Actions", position = 150)
@Messages("CTL_ConvertImageAction=Convert Image")
public final class ConvertImageAction implements ActionListener {

    private final List<DataObject> context;

    public ConvertImageAction(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (DataObject dataObject : context) {
            FileObject input = dataObject.getPrimaryFile();
            //Project project = FileOwnerQuery.getOwner(input);
            //if (project != null && project instanceof AsciiDocProject) {
            switch (input.getExt().toLowerCase()) {
                case "gif":
                    convertImage(input, "jpg", "png");
                    break;
                case "jpeg":
                    convertImage(input, "gif", "png");
                    break;
                case "jpg":
                    convertImage(input, "gif", "png");
                    break;
                case "png":
                    convertImage(input, "gif", "jpg");
                    break;
                default:
                    UserReporting.error("Image Manipulation", "Unknown image type: " + input.getExt());
            }
        }
    }

    private void convertImage(FileObject input, String... outputoptions) {
        Object[] options = createOptions(outputoptions);
        DialogDescriptor dd = new DialogDescriptor("Select Action Required", "Image Conversion", false,
                options, options[0], RIGHT_ALIGN,
                null, new ButtonListener(input));
        dd.setClosingOptions(null);
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
    }

    private Object[] createOptions(String[] outputoptions) {
        Object[] options = new Object[outputoptions.length];
        for (int i = 0; i < outputoptions.length; i++) {
            options[i] = getOptionButton(outputoptions[i]);
        }
        return options;
    }

    private Object getOptionButton(String imagetype) {
        switch (imagetype) {
            case "gif":
                return toGIFbutton;
            case "png":
                return toPNGbutton;
            case "jpg":
                return toJPGbutton;
        }
        return null;
    }

    private static final JButton toPNGbutton = new JButton("Convert to PNG");
    private static final JButton toGIFbutton = new JButton("Convert to GIF");
    private static final JButton toJPGbutton = new JButton("Convert to JPG");

    private class ButtonListener implements ActionListener {

        private final FileObject input;

        public ButtonListener(FileObject input) {
            this.input = input;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == toPNGbutton) {
                doConvertActivity(input, ".png");
            } else if (source == toGIFbutton) {
                doConvertActivity(input, ".gif");
            } else if (source == toJPGbutton) {
                doConvertActivity(input, ".jpg");
            }
        }

    }

    private void doConvertActivity(FileObject input, String outputext) {
        RequestProcessor rp = new RequestProcessor("imageconversion");
        rp.post(new RunConvertActivity(input, outputext));
    }

    private class RunConvertActivity implements Runnable {

        private final FileObject input;
        private final String outputext;

        public RunConvertActivity(FileObject input, String outputext) {
            this.input = input;
            this.outputext = outputext;
        }

        @Override
        public void run() {
            Activity.runExternalProcessWithIOTab("convert-im6",
                    input.getNameExt() + " " + input.getName() + outputext,
                    input.getParent(),
                    new ActivityIO("Image Manipulation")
                            .outputToIOSTDERR(STDERR),
                    "Convert " + input.getNameExt() + " to " + input.getName() + outputext);
        }
    }
}
