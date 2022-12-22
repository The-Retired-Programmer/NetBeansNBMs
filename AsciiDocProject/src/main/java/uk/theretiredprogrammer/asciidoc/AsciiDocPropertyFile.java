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
package uk.theretiredprogrammer.asciidoc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actionssupport.NodeActions;
import uk.theretiredprogrammer.actionssupport.SaveBeforeAction;
import static uk.theretiredprogrammer.actionssupport.SaveBeforeAction.SaveBeforeActionMode.ALL;
import uk.theretiredprogrammer.actionssupport.UserReporting;

public class AsciiDocPropertyFile {

    private SaveBeforeAction savebeforeaction;
    private String srcroot;
    private String generatedroot;
    private boolean paragraphLayout;

    public AsciiDocPropertyFile(FileObject projectdir, NodeActions nodedynamicactionsmanager) {
        updateProperties(projectdir);
        nodedynamicactionsmanager.registerFile("asciidoc", "properties", fct -> updateProperties(projectdir));
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
    
    public boolean isParagraphLayout(){
        return paragraphLayout;
    }

    private void updateProperties(FileObject projectdir) {
        try {
            FileObject propertyfile = projectdir.getFileObject("asciidoc", "properties");
            if (propertyfile == null) {
                throw new IOException("asciidoc.properties missing");
            }
            Properties properties = new Properties();
            try ( InputStream propsin = propertyfile.getInputStream()) {
                properties.load(propsin);
            }
            parseProperties(projectdir, properties);
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage("Unable to read properties from asciidoc.properties: ", ex);
        }
    }

    private void parseProperties(FileObject projectdir, Properties properties) throws IOException {
        srcroot = properties.getProperty("source_root_folder", "src");
        generatedroot = properties.getProperty("generated_root_folder", "generated_documents");
        savebeforeaction = new SaveBeforeAction(properties, "save_before_publishing", ALL);
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
    }
}