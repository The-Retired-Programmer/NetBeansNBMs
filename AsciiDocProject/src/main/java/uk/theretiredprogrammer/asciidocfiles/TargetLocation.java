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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileObject;

public class TargetLocation {
    
    public static FileObject openorcreatefolder(FileObject parentFO, String foldername) throws IOException {
        FileObject folderFO = parentFO.getFileObject(foldername);
        if (folderFO == null) {
            folderFO = parentFO.createFolder(foldername);
        }
        return folderFO;
    }

    private FileObject targetFO;
    private FileObject projectFO;
    private final String targetname;

    public TargetLocation(FileObject inputFO) throws IOException {
        targetname = inputFO.getName();
        List<String> foldernames = new ArrayList<>();
        while (true) {
            inputFO = inputFO.getParent();
            if (inputFO == null) {
                throw new IOException("missing src folder");
            }
            String foldername = inputFO.getNameExt();
            if (foldername.equals("src")) {
                projectFO = inputFO.getParent();
                targetFO = createTargetFolders(openorcreatefolder(projectFO, "generated_documents"), foldernames);
                return;
            }
            foldernames.add(0, foldername);
        }
    }
    
    private FileObject createTargetFolders(FileObject fo, List<String> foldernames) throws IOException {
        for (String foldername : foldernames) {
            fo = openorcreatefolder(fo, foldername);
        }
        return fo;
    }
    
    public FileObject get(String ext) throws IOException {
        return targetFO.createData(targetname, ext);
    }
    
    public FileObject getProjectRoot() {
        return projectFO;
    }
}
