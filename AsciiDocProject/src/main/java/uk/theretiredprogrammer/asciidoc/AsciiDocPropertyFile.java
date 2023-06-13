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
package uk.theretiredprogrammer.asciidoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actions.NodeActions;
import uk.theretiredprogrammer.actions.NodeActions.FileChangeType;
import uk.theretiredprogrammer.actions.SaveBeforeAction;
import static uk.theretiredprogrammer.actions.SaveBeforeAction.SaveBeforeActionMode.ALL;
import uk.theretiredprogrammer.util.ActionsAndActivitiesFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

public class AsciiDocPropertyFile {

    private SaveBeforeAction savebeforeaction;
    private String srcroot;
    private String generatedroot;
    private boolean paragraphLayout;
    private String theme;
    private boolean trace;

    public AsciiDocPropertyFile(FileObject projectdir, NodeActions nodeactions, ProjectState state) throws IOException, ApplicationException {
        loadProperties(projectdir);
        nodeactions.registerFile("asciidoc", "properties", fct -> loadProperties(fct, projectdir, state));
    }

    public SaveBeforeAction getSaveBeforeAction() {
        return savebeforeaction;
    }

    public String getSourceRootFolder() {
        return srcroot;
    }

    public String getGeneratedRootFolder() {
        return generatedroot;
    }

    public boolean isParagraphLayout() {
        return paragraphLayout;
    }

    public String getTheme() {
        return theme;
    }

    public boolean isTrace() {
        return trace;
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
                UserReporting.exceptionWithMessage("Unable to read properties from asciidoc.properties: ", ex);
            }
        }
    }

    private void loadProperties(FileObject projectdir) throws IOException, ApplicationException {
        FileObject propertyfile = projectdir.getFileObject("asciidoc", "properties");
        if (propertyfile == null) {
            throw new IOException("asciidoc.properties missing in " + projectdir.getPath());
        }
        Properties properties = new Properties();
        try ( InputStream propsin = propertyfile.getInputStream()) {
            properties.load(propsin);
        }
        parseProperties(projectdir, properties);
    }

    private void parseProperties(FileObject projectdir, Properties properties) throws IOException, ApplicationException {
        srcroot = properties.getProperty("source_root_folder", "src");
        generatedroot = properties.getProperty("generated_root_folder", "generated_documents");
        savebeforeaction = ActionsAndActivitiesFactory.createSaveBeforeAction(properties, "save_before_publishing", ALL);
        savebeforeaction.setSourceRoot(projectdir.getFileObject(srcroot));
        String layout = properties.getProperty("conversion_layout", "paragraph");
        switch (layout) {
            case "paragraph":
                paragraphLayout = true;
                break;
            case "sentence":
                paragraphLayout = false;
                break;
            default:
                paragraphLayout = true;
                UserReporting.warning("Unknown value for conversion_layout property - default selected");
        }
        theme = properties.getProperty("theme", null);
        String tr = properties.getProperty("trace", "no");
        switch (tr) {
            case "yes":
                trace = true;
                break;
            case "no":
                trace = false;
                break;
            default:
                trace = false;
                UserReporting.warning("Unknown value for trace property - default selected");
        }
    }
}
