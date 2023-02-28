/*
 * Copyright 2022-2023 richard linsdale.
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

import java.util.Properties;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.SaveSelfBeforeAction;

public class SaveBeforeActionImp implements SaveBeforeAction {

    private FileObject srcroot;
    private final SaveBeforeActionMode savebeforeactionmode;

    
    public SaveBeforeActionImp(Properties properties, String propertyname, SaveBeforeActionMode defaultmode) {
        savebeforeactionmode = parseSaveBeforeActionProperty(properties, propertyname, defaultmode);
    }
    
    private SaveBeforeActionMode parseSaveBeforeActionProperty(Properties properties, String propertyname, SaveBeforeActionMode defaultmode) {
    String prop = properties.getProperty(propertyname, defaultmode.getPropertyString());
        String proplc = prop.toLowerCase();
        for (var mode : SaveBeforeActionMode.values()) {
            if (mode.getPropertyString().equals(proplc)) {
                return mode;
            }
        }
        return defaultmode;
    }

    @Override
    public SaveBeforeAction setSourceRoot(FileObject srcroot) {
        this.srcroot = srcroot;
        return this;
    }
    
    @Override
    public void saveIfModified(DataObject dataobject) throws ApplicationException {
        switch (savebeforeactionmode) {
            case NO:
                break;
            case YES:
                SaveSelfBeforeAction.saveIfModified(dataobject);
                break;
            case ALL:
                saveAllDataObjectsIfModified(srcroot);
        }
    }

    private void saveAllDataObjectsIfModified(FileObject file) throws ApplicationException {
        try {
            if (file.isFolder()) {
                for (FileObject child : file.getChildren()) {
                    saveAllDataObjectsIfModified(child);
                }
            } else {
                SaveSelfBeforeAction.saveIfModified(DataObject.find(file));
            }
        } catch (DataObjectNotFoundException ex) {
            throw new ApplicationException("Unable to find a dataobject prior to execution: ", ex);
        }
    }
}
