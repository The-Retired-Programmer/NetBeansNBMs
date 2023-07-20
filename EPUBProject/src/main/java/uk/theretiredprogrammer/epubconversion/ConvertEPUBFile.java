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
import java.util.ArrayList;
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
import uk.theretiredprogrammer.util.ActionsAndActivitiesFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.epubconversion.ConvertEPUBFile"
)
@ActionRegistration(
        displayName = "#CTL_CONVERT_EPUB"
)
@ActionReference(path = "Loaders/application/epub+zip/Actions", position = 170)
@Messages("CTL_CONVERT_EPUB=Convert EPUB")
public final class ConvertEPUBFile implements ActionListener, Runnable {
    
    private final List<DataObject> context;
    
    public ConvertEPUBFile(List<DataObject> context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("epub_convert");
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
                try {
                    EPUBProject aproject = (EPUBProject) project;
                    FileObject outputfolder = getOutputFolder(aproject.getProjectDirectory(), epubname);
                    activity = ActionsAndActivitiesFactory.createActivity()
                            .setMethod(() -> convertAllHTMLSections(aproject.getProjectDirectory(), epubname, epub, outputfolder, "EPUB"))
                            .needsIOTab("EPUB");
                    
                } catch (IOException | ApplicationException ex) {
                    UserReporting.exception("EPUB", ex);
                    return;
                }
                activity.run("Converting EPUB " + epub.getNameExt());
            }
        }
    }
    
    private void convertAllHTMLSections(FileObject dir, String name, FileObject file, FileObject outputfolder, String iotabname) {
        try {
            for (var sectionfile : getEPUBSections(dir, name)) {
                EPUBConvertor.convertHTML(dir, file, sectionfile, outputfolder, iotabname);
            }
        } catch (IOException ex) {
            UserReporting.exception(iotabname, ex);
        }
    }
    
    private List<FileObject> getEPUBSections(FileObject projectdir, String epubname) throws IOException {
        List<FileObject> xhtmlfiles = new ArrayList<>();
        FileObject sections = projectdir.getFileObject("extracted/" + epubname + "/OEBPS/sections");
        if (sections == null) {
            throw new IOException("No extracted content exists for " + epubname + ".epub");
        }
        FileObject[] sectionchildren = sections.getChildren();
        for (var fo : sectionchildren) {
            if (fo.isData() && "xhtml".equals(fo.getExt())) {
                xhtmlfiles.add(fo);
            }
        }
        return xhtmlfiles;
    }
    
    private FileObject getOutputFolder(FileObject projectdir, String epubname) throws IOException {
        FileObject folder = getFolder(projectdir, "converted");
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
