/*
 * Copyright 2022-23 Richard Linsdale.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.actionssupport.DynamicAsyncAction;
import uk.theretiredprogrammer.activity.ActivityIO;

public class ActionsPropertyFile {

    private final List<DynamicAsyncAction> dynamicactions = new ArrayList<>();

    public ActionsPropertyFile(FileObject filefolder, String actionpropertiesfilename, FileChangeManager filechangemanager) {
        parsePropertiesFile(filefolder, actionpropertiesfilename);
        filechangemanager.register(actionpropertiesfilename, "properties", fct -> parsePropertiesFile(filefolder, actionpropertiesfilename));
    }

    public List<DynamicAsyncAction> getActions() {
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
            ActivityIO activitydescriptor = getActivityDescriptorFromProperties(properties, j);
            String command = properties.getProperty(Integer.toString(j) + ".command");
            if (activitydescriptor != null && command != null) {
                String label = properties.getProperty(Integer.toString(j) + ".label");
                String args = properties.getProperty(Integer.toString(j) + ".commandargs", "");
                dynamicactions.add(new DynamicAsyncAction(label)
                        .onAction(() -> Activity.runExternalProcessWithIOTab(
                                command,
                                args,
                                filefolder,
                                activitydescriptor)));
            }
        }
    }

    private ActivityIO getActivityDescriptorFromProperties(Properties properties, int iPrefix) {
        String prefix = Integer.toString(iPrefix);
        String label = properties.getProperty(prefix + ".label");
        if (label == null) {
            return null;
        }
        String tabname = properties.getProperty(prefix + ".tabname", label);
        ActivityIO activitydescriptor = new ActivityIO()
                .stdoutToIO()
                .stderrToIO()
                .ioTabName(tabname);
        String iotabclear = properties.getProperty(prefix + ".cleartab");
        if (iotabclear != null && iotabclear.equals("every execution")) {
            activitydescriptor.ioTabClear();
        }
        return activitydescriptor;
    }
}
