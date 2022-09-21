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
package uk.theretiredprogrammer.actionssupportimplementation;

import uk.theretiredprogrammer.actionssupport.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.openide.filesystems.FileObject;

public class ActionsPropertyFile {

    private final List<DynamicCLIAction> dynamicactions = new ArrayList<>();

    public ActionsPropertyFile(FileObject filefolder, String actionpropertiesfilename, FileChangeManager filechangemanager) {
        parsePropertiesFile(filefolder, actionpropertiesfilename);
        filechangemanager.register(actionpropertiesfilename, "properties", fct -> parsePropertiesFile(filefolder, actionpropertiesfilename));
    }

    public List<DynamicCLIAction> getPropertyActions() {
        return dynamicactions;
    }

    private void parsePropertiesFile(FileObject filefolder, String actionpropertiesfilename) {
        dynamicactions.clear();
        FileObject propertiesFO = filefolder.getFileObject(actionpropertiesfilename, "properties");
        if (propertiesFO == null) {
            return;
        }
        Properties properties = new Properties();
        try {
            try ( InputStream propsin = propertiesFO.getInputStream()) {
                properties.load(propsin);
            }
        } catch (IOException ex) {
            return;
        }
        String pcount = properties.getProperty("COMMANDCOUNT");
        if (pcount == null) {
            return;
        }
        int propertycount = Integer.parseInt(pcount);
        // set up the commands
        for (int j = 1; j <= propertycount; j++) {
            DynamicCLIAction action = actionFromProperties(filefolder, properties, j);
            if (action != null) {
                dynamicactions.add(action);
            }
        }
    }

    private DynamicCLIAction actionFromProperties(FileObject dir, Properties properties, int iPrefix) {
        String prefix = Integer.toString(iPrefix);
        String label = properties.getProperty(prefix + ".label");
        String cmdline = properties.getProperty(prefix + ".command");
        if (cmdline == null || label == null) {
            return null;
        }
        CLIExecUsingOutput cliexec = new CLIExecUsingOutput(dir, dir.getName() + " - " + label, cmdline);
        String inputfrom = properties.getProperty(prefix + ".inputfrom");
        if (inputfrom != null) {
            switch (inputfrom.toLowerCase()) {
                case "file":
                    String inputfilename = properties.getProperty(prefix + ".inputfile");
                    if (inputfilename != null) {
                        cliexec.stdin(dir.getFileObject(inputfilename, null));
                    }
                    break;
                case "ui":
                    cliexec.stdinFromUI();
                    break;
            }
        }
        String sbactions = properties.getProperty(prefix + ".sidebaractions");
        if (sbactions != null) {
            cliexec.sidebarActions(new SideBarActions().add(sbactions.split(","), cliexec).get());
        }
        return new DynamicCLIAction(label, cliexec);
    }
}
