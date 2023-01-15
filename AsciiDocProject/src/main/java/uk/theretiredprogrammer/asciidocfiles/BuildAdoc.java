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
package uk.theretiredprogrammer.asciidocfiles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.activity.ActivityIO;
import uk.theretiredprogrammer.actionssupport.SaveBeforeAction;
import static uk.theretiredprogrammer.activity.ActivityIO.STDERR;
import uk.theretiredprogrammer.asciidoc.AsciiDocProject;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.asciidocfiles.BuildAdoc"
)
@ActionRegistration(
        displayName = "#CTL_BuildAdoc"
)
@ActionReference(path = "Loaders/text/x-asciidoc/Actions", position = 150)
@Messages("CTL_BuildAdoc=Publish")
public final class BuildAdoc implements ActionListener, Runnable {

    private final List<DataObject> context;

    public BuildAdoc(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("text-x-asciidoc_publish");
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
                Activity.runExternalProcessWithIOTab("asciidoctor",
                        "-r asciidoctor-pdf " + aproject.getAsciiDoctorParameters() + input.getPath(),
                        aproject.getProjectDirectory(),
                        new ActivityIO()
                                .outputToIO(STDERR)
                                .ioTabName(aproject.getTabname()),
                        "Publishing " + input.getNameExt());
            } else {
                SaveBeforeAction.saveIfModified(dataObject);
                Activity.runExternalProcessWithIOTab("asciidoctor",
                        "-r asciidoctor-pdf " + input.getPath(),
                        input.getParent(),
                        new ActivityIO()
                                .outputToIO(STDERR)
                                .ioTabName("Publish AsciiDocs"),
                        "Publishing " + input.getNameExt());
            }
        }
    }
}
