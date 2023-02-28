/*
 * Copyright 2022-2023 Richard Linsdale.
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
package uk.theretiredprogrammer.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import uk.theretiredprogrammer.actions.NodeActions.FileChangeType;

public class FileChangeManager {

    private final List<Registration> registrations = new ArrayList<>();
    private final FileChangeListener directorylistener;
    private final FileObject directory;

    public FileChangeManager(FileObject directory) {
        this.directory = directory;
        directorylistener = new DirectoryListener();
        directory.addFileChangeListener(directorylistener);
    }

    public void register(String name, String ext, Consumer<FileChangeType> callback) {
        registrations.add(new Registration(name, ext, callback));
    }

    private void handleFileChange(FileObject file, FileChangeType changetype) {
        handleFileChange(file.getParent(), file.getName(), file.getExt(), changetype);
    }

    private void handleFileChange(FileObject directory, String name, String ext, FileChangeType changetype) {
        registrations.stream()
                .filter(r -> r.name.equals(name) && r.ext.equals(ext) && directory.equals(this.directory))
                .forEach(r -> r.callback.accept(changetype));
    }

    private class Registration {

        public final String name;
        public final String ext;
        public final Consumer<FileChangeType> callback;

        public Registration(String name, String ext, Consumer<FileChangeType> callback) {
            this.name = name;
            this.ext = ext;
            this.callback = callback;
        }
    }

    private class DirectoryListener implements FileChangeListener {

        @Override
        public void fileChanged(FileEvent fe) {
            handleFileChange(fe.getFile(), FileChangeType.CHANGED);
        }

        @Override
        public void fileFolderCreated(FileEvent fe) {
        }

        @Override
        public void fileDataCreated(FileEvent fe) {
            handleFileChange(fe.getFile(), FileChangeType.CREATED);
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            handleFileChange(fe.getFile(), FileChangeType.DELETED);
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
            handleFileChange(fe.getFile().getParent(), fe.getName(), fe.getExt(), FileChangeType.RENAMEDFROM);
            handleFileChange(fe.getFile(), FileChangeType.RENAMEDTO);
        }

        @Override
        public void fileAttributeChanged(FileAttributeEvent fe) {
        }
    }
}
