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
package uk.theretiredprogrammer.asciidocfiles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.actionssupport.SaveBeforeAction;
import uk.theretiredprogrammer.actionssupport.UserReporting;
import uk.theretiredprogrammer.asciidoc.AsciiDocProject;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.asciidocfiles.htmltoadoc"
)
@ActionRegistration(
        displayName = "#CTL_HtmltoAdoc"
)
@ActionReference(path = "Loaders/text/x-htmlfragment/Actions", position = 150)
@Messages("CTL_HtmltoAdoc=Convert to AsciiDoc")
public final class ConvertHtmltoAdoc implements ActionListener, Runnable {

    private final List<DataObject> context;

    public ConvertHtmltoAdoc(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("text-x-htmlfragment_import");
        rp.post(this);
    }

    @Override
    public void run() {
        for (DataObject dataObject : context) {
            FileObject input = dataObject.getPrimaryFile();
            Project project = FileOwnerQuery.getOwner(input);
            if (project != null && project instanceof AsciiDocProject) {
                AsciiDocProject aproject = (AsciiDocProject) project;
                aproject.getSaveBeforeAction().saveIfModifiedByMode(dataObject);
                try {
                    new HtmlFragment2AsciiDoc().convert(input, aproject.isParagraphLayout());
                } catch (IOException ex) {
                    UserReporting.exception(ex);
                }
            } else {
                SaveBeforeAction.saveIfModified(dataObject);
                try {
                    new HtmlFragment2AsciiDoc().convert(input,true);
                } catch (IOException ex) {
                    UserReporting.exception(ex);
                }
            }
        }
    }
}
