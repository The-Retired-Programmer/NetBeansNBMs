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
package uk.theretiredprogrammer.asciidocwordcount;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.StyledDocument;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.activity.Activity;
import static uk.theretiredprogrammer.activity.Activity.NEWLINE;
import uk.theretiredprogrammer.asciidoc.AsciiDocProject;
import uk.theretiredprogrammer.util.ActivitiesAndActionsFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.asciidocwordcount.WordCountAdoc"
)
@ActionRegistration(
        displayName = "#CTL_WordCountAdoc"
)
@Messages("CTL_WordCountAdoc=Count Words")
public final class WordCountAdoc implements ActionListener, Runnable {

    private final List<DataObject> context;

    public WordCountAdoc(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("text-x-asciidoc_wordcount");
        rp.post(this);
    }

    @Override
    public void run() {
        for (DataObject dataObject : context) {
            FileObject input = dataObject.getPrimaryFile();
            Project project = FileOwnerQuery.getOwner(input);
            String iotabname;
            Activity activity;
            if (project != null && project instanceof AsciiDocProject) {
                AsciiDocProject aproject = (AsciiDocProject) project;
                iotabname = aproject.getTabname();
            } else {
                iotabname = "Publish AsciiDocs";
            }
            try {
                activity = ActivitiesAndActionsFactory.createActivity()
                        .setMethod((stdoutwriter) -> wordCount(dataObject, iotabname, stdoutwriter))
                        .needsIOTab(iotabname)
                        .stdoutToIOSTDOUT();
            } catch (ApplicationException ex) {
                UserReporting.exceptionWithMessage(iotabname, "Error Word Count", ex);
                return;
            }
            activity.run("Counting Words");
        }
    }

    private void wordCount(DataObject dataobject, String iotabname, Writer stdoutwriter) {
        try {
            stdoutwriter.write(wordCounter(dataobject) + " words");
            stdoutwriter.write(NEWLINE);
        } catch (IOException | ApplicationException ex) {
            UserReporting.exception(iotabname, ex);
        }
    }

    private int wordCounter(DataObject dataobject) throws ApplicationException {
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
            return wcAll(edit.getDocument());
        } else if (mark > dot) {
            return wc(edit.getDocument(), dot, mark);
        } else {
            return wc(edit.getDocument(), mark, dot);
        }
    }

    private int wcAll(StyledDocument document) throws ApplicationException {
        return wc(document, 0, document.getLength());
    }

    private int wc(StyledDocument document, int frompos, int topos) throws ApplicationException {
        String linetext;
        try {
            linetext = document.getText(frompos, topos - frompos);
        } catch (BadLocationException ex) {
            throw new ApplicationException("Fail: can't extract text by location", ex);
        }
        String[] words = linetext.split("\\s+");
        return words.length;
    }
}
