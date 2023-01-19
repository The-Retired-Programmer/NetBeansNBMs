/*
 * Copyright 2023 richard linsdale.
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

import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.activity.ActivityIO;

public class EPUBWorkers {

    private final FileObject projectfolder;
    private final FileObject epubdoc;
    
    public EPUBWorkers(FileObject projectfolder) {
        this.projectfolder = projectfolder;
        this.epubdoc = getEPUB(projectfolder);
    }
    
    public boolean isEPUBAvailable() {
        return epubdoc != null;
    }
    
    public String getEPUBName() {
        return epubdoc != null ? epubdoc.getNameExt() : "Does not Exist";
    }
    
    public Activity getExtractionActivity(ActivityIO activityio) {
         return new EPUBExtractionActivity(epubdoc, activityio);
    }
    
    public Activity getConversionActivity(ActivityIO activityio) {
         return new EPUBConversionActivity(projectfolder, activityio);
    }
    
    private FileObject getEPUB(FileObject projectfolder) {
        FileObject[] files = projectfolder.getChildren();
        for (FileObject file : files) {
            if (file.hasExt("epub")) {
                return file;
            }
        }
        return null;
    }
}
