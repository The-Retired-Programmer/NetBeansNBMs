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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.openide.DialogDescriptor;
import static org.openide.DialogDescriptor.RIGHT_ALIGN;
import org.openide.DialogDisplayer;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.util.ActionsAndActivitiesFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

@ActionID(
        category = "Tools",
        id = "uk.theretiredprogrammer.image.actions.CropImageAction"
)
@ActionRegistration(
        iconBase = "uk/theretiredprogrammer/image/actions/cut.png",
        displayName = "#CTL_CropImageAction"
)
@ActionReference(path = "Loaders/image/png-gif-jpeg-bmp/Actions", position = 170)
@Messages("CTL_CropImageAction=Crop Image")
public final class CropImageAction implements ActionListener {

    private final List<DataObject> context;
    private int image_w = 100;
    private int image_h = 100;

    public CropImageAction(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (DataObject dataObject : context) {
            FileObject input = dataObject.getPrimaryFile();
            //Project project = FileOwnerQuery.getOwner(input);
            //if (project != null && project instanceof AsciiDocProject) {
            getImageSize(input);
            cropImage(input);
        }
    }

    private void cropImage(FileObject input) {
        Object[] options = createOptions();
        DialogDescriptor dd = new DialogDescriptor(null, "Image Crop", false,
                options, options[options.length - 1], RIGHT_ALIGN,
                null, new ButtonListener(input));
        dd.setClosingOptions(null);
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
    }

    private void getImageSize(FileObject input) {
        try {
            image_h = 100;
            image_w = 100;
            BufferedImage image = ImageIO.read(FileUtil.toFile(input));
            image_w = image.getWidth();
            image_h = image.getHeight();
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage("Image Manipulation", "Error when finding image size", ex);
        }
    }

    private Object[] createOptions() {
        Object[] options = new Object[9];
        options[0] = widthlabel;
        options[1] = getConfiguredTextField(width, image_w);
        options[2] = heightlabel;
        options[3] = getConfiguredTextField(height, image_h);
        options[4] = offsetleftlabel;
        options[5] = getConfiguredTextField(offsetleft, 0);
        options[6] = offsettoplabel;
        options[7] = getConfiguredTextField(offsettop, 0);
        options[8] = cropbutton;
        return options;
    }

    private JTextField getConfiguredTextField(JTextField field, int defaultvalue) {
        field.setText(Integer.toString(defaultvalue));
        return field;
    }

    private static final JButton cropbutton = new JButton("Crop image");
    private static final JTextField offsetleft = new JTextField(10);
    private static final JLabel offsetleftlabel = new JLabel("Left offset");
    private static final JTextField offsettop = new JTextField(10);
    private static final JLabel offsettoplabel = new JLabel("Top offset");
    private static final JTextField width = new JTextField(10);
    private static final JLabel widthlabel = new JLabel("Width");
    private static final JTextField height = new JTextField(10);
    private static final JLabel heightlabel = new JLabel("Height");

    private class ButtonListener implements ActionListener {

        private final FileObject input;

        public ButtonListener(FileObject input) {
            this.input = input;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == cropbutton) {
                int w = Integer.parseInt(width.getText());
                int h = Integer.parseInt(height.getText());
                int left = Integer.parseInt(offsetleft.getText());
                int top = Integer.parseInt(offsettop.getText());
                if (w + left > image_w) {
                    UserReporting.warning("Image Manipulation", "attempting to crop image beyond rhs of image");
                    return;
                }
                if (h + top > image_h) {
                    UserReporting.warning("Image Manipulation", "attempting to crop image beyond bottom of image");
                    return;
                }
                doCropActivity(input, w, h, left, top);
            }
        }
    }

    private void doCropActivity(FileObject input, int w, int h, int left, int top) {
        RequestProcessor rp = new RequestProcessor("imageconversion");
        rp.post(new RunCropActivity(input, w, h, left, top));
    }

    private class RunCropActivity implements Runnable {

        private final FileObject input;
        private final int w;
        private final int h;
        private final int left;
        private final int top;

        public RunCropActivity(FileObject input, int w, int h, int left, int top) {
            this.input = input;
            this.w = w;
            this.h = h;
            this.left = left;
            this.top = top;
        }

        @Override
        public void run() {
            String crop = Integer.toString(w) + "x" + Integer.toString(h) + "+" + Integer.toString(left) + "+" + Integer.toString(top);
            String cropfactor = "from " + Integer.toString(image_w) + "x" + Integer.toString(image_h)
                    + " to " + Integer.toString(w) + "x" + Integer.toString(h)
                    + " offset " + Integer.toString(left) + "x" + Integer.toString(top);
            String outputfilename = FileUtil.findFreeFileName(input.getParent(), input.getName(), input.getExt())
                    + "." + input.getExt();
            Activity activity;
            try {
                activity = ActionsAndActivitiesFactory.createActivity()
                        .setExternalProcess("convert-im6",
                                "\"" + input.getNameExt() + "\" -crop '" + crop + "' \"" + outputfilename + "\"",
                                input.getParent())
                        .needsIOTab("Image Manipulation")
                        .stderrToIOSTDERR();
            } catch (ApplicationException ex) {
                UserReporting.exceptionWithMessage("Image Manipulation", "Error when configuring Crop Image Activity", ex);
                return;
            }
            activity.run("Crop " + input.getNameExt() + " to " + outputfilename + " - " + cropfactor);
        }
    }
}
