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
package uk.theretiredprogrammer.epub;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actions.NodeActions;
import uk.theretiredprogrammer.actions.NodeActions.FileChangeType;
import uk.theretiredprogrammer.actions.SaveBeforeAction;
import static uk.theretiredprogrammer.actions.SaveBeforeAction.SaveBeforeActionMode.YES;
import uk.theretiredprogrammer.util.ActivitiesAndActionsFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

public class EPUBPropertyFile {

    private SaveBeforeAction savebeforeaction;

    public EPUBPropertyFile(FileObject projectdir, NodeActions nodeactions, ProjectState state) throws IOException, ApplicationException {
        loadProperties(projectdir);
        nodeactions.registerFile("epub", "properties", fct -> loadProperties(fct, projectdir, state));
    }

    public SaveBeforeAction getSaveBeforeAction() {
        return savebeforeaction;
    }

    private void clearPropertyValues() {
        savebeforeaction = null;
    }

    private void loadProperties(FileChangeType ftc, FileObject projectdir, ProjectState state) {
        switch (ftc) {
            case RENAMEDFROM:
            case DELETED:
                state.notifyDeleted();
                break;
            default:
                try {
                loadProperties(projectdir);
            } catch (ApplicationException | IOException ex) {
                UserReporting.exceptionWithMessage("Unable to read properties from epub.properties: ", ex);
            }
        }
    }

    private void loadProperties(FileObject projectdir) throws IOException, ApplicationException {
        clearPropertyValues();
        FileObject propertyfile = projectdir.getFileObject("epub", "properties");
        if (propertyfile == null) {
            throw new IOException("epub.properties missing in " + projectdir.getPath());
        }
        Properties properties = new Properties();
        try ( InputStream propsin = propertyfile.getInputStream()) {
            properties.load(propsin);
        }
        parseProperties(projectdir, properties);
    }

    private void parseProperties(FileObject projectdir, Properties properties) throws IOException, ApplicationException {
        savebeforeaction = ActivitiesAndActionsFactory.createSaveBeforeAction(properties, "save_before_execution", YES);
        savebeforeaction.setSourceRoot(projectdir);
    }
}
