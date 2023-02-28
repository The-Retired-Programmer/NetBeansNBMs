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

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import uk.theretiredprogrammer.util.ApplicationException;

/**
 * Support for saving modified files prior to running an external process which
 * uses the file.
 *
 * The project properties file is used to obtain a save_before_action setting:
 *
 * NO - don't save
 *
 * YES - save the file which is the subject of the action (either file node or
 * editor)
 *
 * ALL - save all project source files which are currently in a modified state.
 *
 * NOTE:
 *
 * There is a static method which can use to SaveBeforeAction on any file, but
 * without the Any need for a project's property file. It implements
 * functionality which is the same as the YES property option.
 *
 * SaveSelfBeforeAction.saveIfModified(DataObject dataobject);
 *
 */
public interface SaveBeforeAction {

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

        /**
         * Obtain the PropertyString associated with this enum value
         * 
         * @return the PropertyString
         */
        public String getPropertyString() {
            return propertystring;
        }
    };

    /**
     * Set the root of the Source files (used if mode is ALL)
     *
     * @param srcroot the source root folder
     * @return this instance
     */
    public SaveBeforeAction setSourceRoot(FileObject srcroot);

    /**
     * Save a file(s) if modified (depending on the Mode set)
     *
     * it is assumed that the file exists with the projects source folder(s)
     *
     * @param dataobject the file
     * @throws ApplicationException a failure Exception
     */
    public void saveIfModified(DataObject dataobject) throws ApplicationException;
}
