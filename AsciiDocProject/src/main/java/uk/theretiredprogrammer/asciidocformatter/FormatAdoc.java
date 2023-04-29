/*
 * Copyright 2023 Richard Linsdale.
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
package uk.theretiredprogrammer.asciidocformatter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.Caret;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.asciidoc.AsciiDocProject;
import uk.theretiredprogrammer.util.ActionsAndActivitiesFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.asciidocfiles.FormatAdoc"
)
@ActionRegistration(
        displayName = "#CTL_FormatAdoc"
)
@Messages("CTL_FormatAdoc=Format Lines")
public final class FormatAdoc implements ActionListener, Runnable {

    private final List<DataObject> context;

    public FormatAdoc(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("text-x-asciidoc_format");
        rp.post(this);
    }

    @Override
    public void run() {
        for (DataObject dataObject : context) {
            FileObject input = dataObject.getPrimaryFile();
            Project project = FileOwnerQuery.getOwner(input);
            if (project != null && project instanceof AsciiDocProject) {
                AsciiDocProject aproject = (AsciiDocProject) project;
                String iotabname = aproject.getTabname();
                try {
                    ActionsAndActivitiesFactory.getActivityIOTab(iotabname).println("Formatting Lines");
                    reformat(dataObject);
                    ActionsAndActivitiesFactory.getActivityIOTab(iotabname).printdone();
                } catch (ApplicationException ex) {
                    UserReporting.exceptionWithMessage(iotabname, "Error Formatting Lines", ex);
                }
            } else {
                String iotabname = "Publish AsciiDocs";
                try {
                    ActionsAndActivitiesFactory.getActivityIOTab(iotabname).println("Formatting Lines");
                    reformat(dataObject);
                    ActionsAndActivitiesFactory.getActivityIOTab(iotabname).printdone();
                } catch (ApplicationException ex) {
                    UserReporting.exceptionWithMessage(iotabname, "Error Formatting Lines", ex);
                }
            }
        }
    }

    private void reformat(DataObject dataobject) throws ApplicationException {
        final EditorCookie edit = (EditorCookie) dataobject.getLookup().lookup(EditorCookie.class);
        if (edit == null) {
            throw new ApplicationException("Fatal: Editor Cookie missing");
        }
        JEditorPane[] panes = edit.getOpenedPanes();
        if (panes == null) {
            throw new ApplicationException("Fatal: File is not open in editor");
        }
        JEditorPane pane = panes[0];
        Caret caret = pane.getCaret();
        int mark = caret.getMark();
        int dot = caret.getDot();
        if (mark == dot) {
            new AdocDocument().reformatSelectedBlock(edit.getDocument(), mark);
        } else if (mark > dot) {
            new AdocDocument().reformatSelectedBlocks(edit.getDocument(), dot, mark);
        } else {
            new AdocDocument().reformatSelectedBlocks(edit.getDocument(), mark, dot);
        }
    }
}
