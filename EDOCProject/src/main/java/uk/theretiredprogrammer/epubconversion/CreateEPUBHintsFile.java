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
import uk.theretiredprogrammer.actionssupport.UserReporting;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.activity.ActivityIO;
import static uk.theretiredprogrammer.activity.ActivityIO.STDERR;
import static uk.theretiredprogrammer.activity.ActivityIO.STDOUT;
import uk.theretiredprogrammer.epub.EPUBProject;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.epubconversion.CreateEPUBHintsFile"
)
@ActionRegistration(
        displayName = "#CTL_CREATE_EPUB_HINTS"
)
@ActionReference(path = "Loaders/application/epub+zip/Actions", position = 160)
@Messages("CTL_CREATE_EPUB_HINTS=Create EPUB Hints")
public final class CreateEPUBHintsFile implements ActionListener, Runnable {

    private final List<DataObject> context;

    public CreateEPUBHintsFile(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("epub_hints");
        rp.post(this);
    }

    @Override
    public void run() {
        for (DataObject dataObject : context) {
            FileObject epub = dataObject.getPrimaryFile();
            String epubname = epub.getName();
            Project project = FileOwnerQuery.getOwner(epub);
            if (project != null && project instanceof EPUBProject) {
                try {
                    EPUBProject aproject = (EPUBProject) project;
                    FileObject stylesheet = getEPUBStyleSheet(aproject.getProjectDirectory(), epubname);
                    Activity.runWithIOTab(new EPUBHintsActivity(epub, stylesheet,
                            new ActivityIO("EPUB")
                                    .outputToIOSTDERR(STDERR)
                                    .outputToIOSTDOUT(STDOUT)),
                            "Creating EPUB Hints for " + epubname
                    );
                } catch (IOException ex) {
                    UserReporting.warning("EPUB", ex.getLocalizedMessage());
                }
            }
        }
    }

    private FileObject getEPUBStyleSheet(FileObject projectdir, String epubname) throws IOException {
        FileObject styles = projectdir.getFileObject("extracted/" + epubname + "/OEBPS/styles");
        if (styles == null) {
            throw new IOException("No extracted content exists for " + epubname + ".epub");
        }
        return styles.getFileObject("stylesheet.css");
    }

    private FileObject getFolder(FileObject parent, String foldername) throws IOException {
        FileObject folder = parent.getFileObject(foldername);
        if (folder == null) {
            folder = parent.createFolder(foldername);
        }
        if (folder == null || !folder.isFolder()) {
            throw new IOException("Folder \"" + foldername + "\": does not exist and cannot be created; or exists and is not a folder");
        }
        return folder;
    }
}
