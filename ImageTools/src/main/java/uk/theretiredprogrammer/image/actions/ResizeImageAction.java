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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import static javax.swing.SwingConstants.LEFT;
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
import uk.theretiredprogrammer.actionssupport.UserReporting;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.activity.ActivityIO;
import static uk.theretiredprogrammer.activity.ActivityIO.STDERR;

@ActionID(
        category = "Tools",
        id = "uk.theretiredprogrammer.image.actions.ResizeImageAction"
)
@ActionRegistration(
        iconBase = "uk/theretiredprogrammer/image/actions/arrow_out.png",
        displayName = "#CTL_ResizeImageAction"
)
@ActionReference(path = "Loaders/image/png-gif-jpeg-bmp/Actions", position = 160)
@Messages("CTL_ResizeImageAction=Resize Image")
public final class ResizeImageAction implements ActionListener {

    private final List<DataObject> context;
    private int image_w = 100;
    private int image_h = 100;

    public ResizeImageAction(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (DataObject dataObject : context) {
            FileObject input = dataObject.getPrimaryFile();
            //Project project = FileOwnerQuery.getOwner(input);
            //if (project != null && project instanceof AsciiDocProject) {
            getImageSize(input);
            resizeImage(input);
        }
    }

    private void resizeImage(FileObject input) {
        Object[] options = createOptions();
        DialogDescriptor dd = new DialogDescriptor(null, "Image Resize", false,
                options, options[options.length - 1], RIGHT_ALIGN,
                null, new ButtonListener(input));
        dd.setClosingOptions(new Object[]{resizebutton});
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
        Object[] options = new Object[4];
        options[0] = sizelabel;
        options[1] = getConfiguredTextField(size);
        options[2] = getConfiguredCheckBox(vorh);
        options[3] = resizebutton;
        return options;
    }

    private JTextField getConfiguredTextField(JTextField field) {
        field.setText(Integer.toString(vorh.isSelected()?image_w:image_h));
        return field;
    }

    private JCheckBox getConfiguredCheckBox(JCheckBox field) {
        field.setHorizontalTextPosition(LEFT);
        return field;
    }

    private static final JButton resizebutton = new JButton("Resize image");
    private static final JTextField size = new JTextField(10);
    private static final JLabel sizelabel = new JLabel("Required Size");
    private static final JCheckBox vorh = new JCheckBox("Use required Size horizontally", true);

    private class ButtonListener implements ActionListener {

        private final FileObject input;

        public ButtonListener(FileObject input) {
            this.input = input;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == resizebutton) {
                int dim = Integer.parseInt(size.getText());
                boolean ishval = vorh.isSelected();
                doResizeActivity(input, dim, ishval);
            } else if (source == vorh) {
                size.setText(Integer.toString(vorh.isSelected()?image_w:image_h));
            }
        }
    }

    private void doResizeActivity(FileObject input, int dim, boolean ishval) {
        RequestProcessor rp = new RequestProcessor("imageconversion");
        rp.post(new RunResizeActivity(input, dim, ishval));
    }

    private class RunResizeActivity implements Runnable {

        private final FileObject input;
        private final int dim;
        private final boolean ishval;

        public RunResizeActivity(FileObject input, int dim, boolean ishval) {
            this.input = input;
            this.dim = dim;
            this.ishval = ishval;
        }

        @Override
        public void run() {
            String resize = ishval
                    ? Integer.toString(dim) + "x1000000"
                    : "1000000x" + Integer.toString(dim);
            String rescalefactor = ishval
                    ? "from "+ Integer.toString(image_w)+ " to "+Integer.toString(dim) + " (horizontally)"
                    : "from "+ Integer.toString(image_h)+ " to "+Integer.toString(dim) + " (vertically)";
            String outputfilename = FileUtil.findFreeFileName(input.getParent(), input.getName(), input.getExt())
                    + "." + input.getExt();
            Activity.runExternalProcessWithIOTab("convert-im6",
                    "\""+input.getNameExt() + "\" -resize " + resize + " \"" + outputfilename+ "\"",
                    input.getParent(),
                    new ActivityIO("Image Manipulation")
                            .outputToIOSTDERR(STDERR),
                    "Resize " + input.getNameExt() + " to " + outputfilename +" - "+rescalefactor);
        }
    }
}
