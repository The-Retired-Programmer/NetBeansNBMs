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

public class AsciiDocPropertyFile {

    private boolean assemblydefined;
    private AssemblyRules bookrules;
    private AssemblyRules webpagerules;
    private AssemblyRules articlerules;
    private String asciidocparameters = "";

    private void clearPropertyValues() {
        assemblydefined = false;
        bookrules = null;
        webpagerules = null;
        articlerules = null;
        asciidocparameters = "";
    }

    public String asciidocUserParameters() {
        return asciidocparameters;
    }

    public boolean isAssembly() {
        return assemblydefined;
    }

    public boolean isBookAssembly() {
        return assemblydefined && bookrules != null;
    }

    public String bookfrom() {
        return assemblydefined && bookrules != null ? bookrules.from : "UNKNOWN";
    }

    public String bookto() {
        return assemblydefined && bookrules != null ? bookrules.to : "UNKNOWN";
    }

    public boolean isArticleAssembly() {
        return assemblydefined && articlerules != null;
    }

    public String articlefrom() {
        return assemblydefined && articlerules != null ? articlerules.from : "UNKNOWN";
    }

    public String articleto() {
        return assemblydefined && articlerules != null ? articlerules.to : "UNKNOWN";
    }

    public boolean isWebpageAssembly() {
        return assemblydefined && webpagerules != null;
    }

    public String webpagefrom() {
        return assemblydefined && webpagerules != null ? webpagerules.from : "UNKNOWN";
    }

    public String webpageto() {
        return assemblydefined && webpagerules != null ? webpagerules.to : "UNKNOWN";
    }

    public AsciiDocPropertyFile(FileObject projectdir, NodeActions nodedynamicactionsmanager) {
        updateProperties(projectdir);
        nodedynamicactionsmanager.registerFile("asciidoc", "properties", fct -> updateProperties(projectdir));
    }

    private void updateProperties(FileObject projectdir) {
        try {
            clearPropertyValues();
            FileObject propertyfile = projectdir.getFileObject("asciidoc", "properties");
            if (propertyfile == null) {
                throw new IOException("asciidoc.properties missing");
            }
            Properties properties = new Properties();
            try ( InputStream propsin = propertyfile.getInputStream()) {
                properties.load(propsin);
            }
            parseAssemblyProperties(properties);
        } catch (IOException ex) {
            assemblydefined = false;
        }
    }

    private void parseAssemblyProperties(Properties properties) throws IOException {
        String assemblein = properties.getProperty("assemble");
        if (assemblein == null) {
            return;
        }
        assemblydefined = true;
        for (String assembleitem : assemblein.split(",")) {
            switch (assembleitem.trim().toLowerCase()) {
                case "book":
                    bookrules = new AssemblyRules("book", properties);
                    break;
                case "webpage":
                    webpagerules = new AssemblyRules("webpage", properties);
                    break;
                case "article":
                    articlerules = new AssemblyRules("article", properties);
                    break;
                default:
                    throw new IOException("Illegal assembly value");
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String propertyname : properties.stringPropertyNames()) {
            if (propertyname.startsWith("-")) {
                int ubarpos = propertyname.indexOf('_');
                if (ubarpos == -1) {
                    sb.append(propertyname);
                } else {
                    sb.append(propertyname.substring(0, ubarpos));
                    sb.append(" ");
                    String pname = propertyname.substring(ubarpos + 1);
                    if (pname != null && (!pname.isBlank())) {
                        sb.append(propertyname.substring(ubarpos + 1));
                        String propertyvalue = properties.getProperty(propertyname);
                        if (propertyvalue != null && (!propertyvalue.isBlank())) {
                            sb.append("=");
                            sb.append(propertyvalue);
                        }
                    }
                }
                sb.append(" ");
            }
        }
        asciidocparameters = sb.toString();
    }
}
