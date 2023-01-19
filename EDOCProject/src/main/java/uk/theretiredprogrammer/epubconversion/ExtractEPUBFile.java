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
package uk.theretiredprogrammer.epubconversion;

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
import static uk.theretiredprogrammer.activity.ActivityIO.STDERR;
import static uk.theretiredprogrammer.activity.ActivityIO.STDOUT;
import uk.theretiredprogrammer.epub.EPUBProject;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.epubconversion.ExtractEPUBFile"
)
@ActionRegistration(
        displayName = "#CTL_EXTRACT_EPUB"
)
@ActionReference(path = "Loaders/text/x-epub/Actions", position = 150)
@Messages("CTL_EXTRACT_EPUB=Extract EPUB")
public final class ExtractEPUBFile implements ActionListener, Runnable {

    private final List<DataObject> context;

    public ExtractEPUBFile(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("text-x-epub_extract");
        rp.post(this);
    }

    @Override
    public void run() {
        for (DataObject dataObject : context) {
            FileObject input = dataObject.getPrimaryFile();
            Project project = FileOwnerQuery.getOwner(input);
            if (project != null && project instanceof EPUBProject) {
                EPUBProject aproject = (EPUBProject) project;
                aproject.getSaveBeforeAction().saveIfModifiedByMode(dataObject);
                Activity.runWithIOTab(new EPUBExtractionActivity(input,
                        new ActivityIO("EPUB")
                                .outputToIOSTDERR(STDERR)
                                .outputToIOSTDOUT(STDOUT)),
                        "Extracting EPUB " + input.getNameExt()
                );
            }
        }
    }
}
