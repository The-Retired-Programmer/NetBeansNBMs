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
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.epub.EPUBProject;
import uk.theretiredprogrammer.util.ActivitiesAndActionsFactory;
import uk.theretiredprogrammer.util.UserReporting;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.epubconversion.ExtractEPUBFile"
)
@ActionRegistration(
        displayName = "#CTL_EXTRACT_EPUB"
)
@ActionReference(path = "Loaders/application/epub+zip/Actions", position = 150)
@Messages("CTL_EXTRACT_EPUB=Extract EPUB")
public final class ExtractEPUBFile implements ActionListener, Runnable {

    private final List<DataObject> context;

    public ExtractEPUBFile(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("epub_extract");
        rp.post(this);
    }

    @Override
    public void run() {
        for (DataObject dataObject : context) {
            FileObject epub = dataObject.getPrimaryFile();
            String epubname = epub.getName();
            Project project = FileOwnerQuery.getOwner(epub);
            if (project != null && project instanceof EPUBProject) {
                Activity activity;
                EPUBProject aproject = (EPUBProject) project;
                try {
                    FileObject extractionfolder = getExtractionFolder(aproject.getProjectDirectory(), epubname);
                    activity = ActivitiesAndActionsFactory.createActivity()
                            .setMethod(() -> EPUBExtractor.extract(epub, extractionfolder, "EPUB"))
                            .needsIOTab("EPUB");
                } catch (Exception ex) {
                    UserReporting.exceptionWithMessage("EPUB", "Error extracting EPUB", ex);
                    return;
                }
                activity.run("Extracting EPUB " + epub.getNameExt());
            }
        }
    }

    private FileObject getExtractionFolder(FileObject projectdir, String epubname) throws IOException {
        FileObject folder = getFolder(projectdir, "extracted");
        return getFolder(folder, epubname);
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
