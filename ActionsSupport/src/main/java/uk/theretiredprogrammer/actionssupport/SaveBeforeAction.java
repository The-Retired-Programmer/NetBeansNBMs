/*
 * Copyright 2022 richard linsdale.
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
package uk.theretiredprogrammer.actionssupport;

import java.io.IOException;
import java.util.Properties;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * Support for saving modified files prior to running an external process which
 * uses the file.
 *
 * Two Use Cases are supported:
 *
 * File is not within a project. In that case the file is saved.
 *
 * File is within a project. In that case, the project properties file can be
 * used to obtain a save_before_action setting:
 *
 * NO - don't save
 *
 * YES - save the file which is the subject of the action (either file node or
 * editor)
 *
 * ALL - save all project source files which are currently in a modified state.
 *
 * The class supports:
 *
 * Extracting the required property from the project property file (including
 * replacement of the instance whenever an update of the property file is made).
 *
 * Definition of the Source file root folder.
 *
 * SaveIfModified action Methods: for project and non-project use cases.
 */
public class SaveBeforeAction {

    private FileObject srcroot;

    /**
     * The Modes of the Save as set in the project properties file
     */
    public static enum SaveBeforeActionMode {

        /**
         * Don't Save
         */
        NO("no"),
        /**
         * Save the selected file if modified
         */
        YES("yes"),
        /**
         * Save all modified file within the projects source root
         */
        ALL("all");
        private final String propertystring;

        SaveBeforeActionMode(String propertystring) {
            this.propertystring = propertystring;
        }

        String getPropertyString() {
            return propertystring;
        }
    };

    private final SaveBeforeActionMode savebeforeactionmode;

    /**
     * Create the instance - defining the basic parameters.
     *
     * Will process the properties to extract the SaveBeforeAction parameter.
     *
     * @param properties the properties
     * @param propertyname the property key - used to find the Mode value
     * @param defaultmode the mode value to use if the property is not found in
     * properties
     * @throws IOException if properties is malformed
     */
    public SaveBeforeAction(Properties properties, String propertyname, SaveBeforeActionMode defaultmode) throws IOException {
        savebeforeactionmode = parseSaveBeforeActionProperty(properties, propertyname, defaultmode);
    }

    private SaveBeforeActionMode parseSaveBeforeActionProperty(Properties properties, String propertyname, SaveBeforeActionMode defaultmode) throws IOException {
        String prop = properties.getProperty(propertyname, defaultmode.getPropertyString());
        String proplc = prop.toLowerCase();
        for (var mode : SaveBeforeActionMode.values()) {
            if (mode.propertystring.equals(proplc)) {
                return mode;
            }
        }
        UserReporting.warning("Unknown "+propertyname+" option: "+prop);
        return defaultmode;
    }

    /**
     * Set the root of the Source files (used if mode is ALL)
     *
     * @param srcroot the source root folder
     */
    public void setSourceRoot(FileObject srcroot) {
        this.srcroot = srcroot;
    }

    /**
     * Simple method to save a file if it is modified.
     *
     * Use in cases of not project files.
     *
     * @param dataobject the dataobject representation of the file
     */
    public static void saveIfModified(DataObject dataobject) {
        try {
            if (dataobject.isModified()) {
                SaveCookie cookie = dataobject.getLookup().lookup(SaveCookie.class);
                if (cookie != null) {
                    cookie.save();
                }
            }
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage("Unable to close a modified file prior to execution: ", ex);
        }
    }

    /**
     * Save a project file if it is modified (depending on the Mode set)
     *
     * Use in cases of project files, it follows the Mode instruction and the
     * Source Root where required.
     
     * @param dataobject the dataobject representation of the file (Used when
     * Mode is YES).
     */
    public void saveIfModifiedByMode(DataObject dataobject) {
        switch (savebeforeactionmode) {
            case NO:
                break;
            case YES:
                saveIfModified(dataobject);
                break;
            case ALL:
                saveAllDataObjectsIfModified(srcroot);
        }
    }

    private void saveAllDataObjectsIfModified(FileObject file) {
        try {
            if (file.isFolder()) {
                for (FileObject child : file.getChildren()) {
                    saveAllDataObjectsIfModified(child);
                }
            } else {
                saveIfModified(DataObject.find(file));
            }
        } catch (DataObjectNotFoundException ex) {
            UserReporting.exceptionWithMessage("Unable to find a dataobject prior to execution: ", ex);
        }
    }
}
