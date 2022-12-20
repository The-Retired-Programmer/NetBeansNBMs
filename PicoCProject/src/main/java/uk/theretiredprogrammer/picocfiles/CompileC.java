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
package uk.theretiredprogrammer.picocfiles;

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
import uk.theretiredprogrammer.actionssupport.NbCliDescriptor;
import uk.theretiredprogrammer.picoc.PicoCProject;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.picocfiles.compilec"
)
@ActionRegistration(
        displayName = "#CTL_CompileC"
)
@ActionReference(path = "Loaders/text/x-picoc/Actions", position = 150)
@Messages("CTL_CompileC=Compile")
public final class CompileC implements ActionListener, Runnable {

    private final List<DataObject> context;

    public CompileC(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("text-x-picoc_compile");
        rp.post(this);
    }

    @Override
    public void run() {
        for (DataObject dataObject : context) {
            FileObject input = dataObject.getPrimaryFile();
            Project project = FileOwnerQuery.getOwner(input);
            if (project != null && project instanceof PicoCProject) {
                PicoCProject aproject = (PicoCProject) project;
                aproject.getSaveBeforeAction().saveIfModifiedByMode(dataObject);
                new NbCliDescriptor(aproject.getProjectDirectory(),
//                        "asciidoctor", "-r asciidoctor-pdf " + aproject.getAsciiDoctorParameters() + input.getPath())
//   need a command line here
                        "dummy","dummy")
                        .stderrToIO()
                        .ioTabName(aproject.getTabname())
                        .exec("Compile " + input.getNameExt());
            }
        }
    }
}
