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
package uk.theretiredprogrammer.postgresql;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actionssupport.NodeActions;

public class PostgreSQLPropertyFile {
    
    private String database;
    private boolean defined;
    
    public String getDatabase() throws IOException {
        if (!defined) {
            throw new IOException("Database name not defined (in postgresql.properties)");
        }
        return database;
    }
    
    private void clearPropertyValues() {
        database = "";
        defined = false;
    }

    public PostgreSQLPropertyFile(FileObject projectdir, NodeActions nodedynamicactionsmanager) {
        updateProperties(projectdir);
        nodedynamicactionsmanager.registerFile("postgresql", "properties", fct -> updateProperties(projectdir));
    }

    private void updateProperties(FileObject projectdir) {
        try {
            clearPropertyValues();
            FileObject propertyfile = projectdir.getFileObject("postgresql", "properties");
            if (propertyfile == null) {
                throw new IOException("postgresql.properties missing");
            }
            Properties properties = new Properties();
            try ( InputStream propsin = propertyfile.getInputStream()) {
                properties.load(propsin);
            }
            parseProperties(properties);
        } catch (IOException ex) {
            defined = false;
        }
    }

    private void parseProperties(Properties properties) throws IOException {
        database = properties.getProperty("database");
        if (database == null) {
            return;
        }
        defined = true;
    }
}
