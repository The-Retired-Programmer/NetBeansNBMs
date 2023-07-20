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
package uk.theretiredprogrammer.actions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.activity.ActivityImp;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

public class ActionsPropertyFile {

    private final List<DynamicAction> dynamicactions = new ArrayList<>();

    public ActionsPropertyFile(FileObject filefolder, String actionpropertiesfilename, FileChangeManager filechangemanager) {
        parsePropertiesFile(filefolder, actionpropertiesfilename);
        filechangemanager.register(actionpropertiesfilename, "properties", fct -> parsePropertiesFile(filefolder, actionpropertiesfilename));
    }

    public List<DynamicAction> getActions() {
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
            String prefix = Integer.toString(j);
            String command = properties.getProperty(prefix + ".command");
            if (command != null) {
                String label = properties.getProperty(prefix + ".label");
                Activity activity;
                try {
                    activity = getActivity(command, filefolder, label, properties, prefix);
                    dynamicactions.add(new DynamicActionImp(label).onActionAsync(() -> activity.run(label)));
                } catch (ApplicationException ex) {
                    UserReporting.exceptionWithMessage("Error when parsing the node actions properties file", ex);
                }
            }
        }
    }
    
    private Activity getActivity(String command, FileObject dir, String label, Properties properties, String prefix) throws ApplicationException {
        String args = properties.getProperty(prefix + ".commandargs", "");
        Activity activity = new ActivityImp()
                .setExternalProcess(command, args, dir)
                .stdoutToIOSTDOUT()
                .stderrToIOSTDERR();
        if ("enable".equalsIgnoreCase(properties.getProperty(prefix + ".IOSTDIN", "disable"))) {
            activity.stdinFromIOSTDIN();
        }
        String tabname = properties.getProperty(prefix + ".tabname", label);
        if (!tabname.isBlank()) {
            activity.needsIOTab(tabname);
        }
        if ("every execution".equalsIgnoreCase(properties.getProperty(prefix + ".cleartab", ""))) {
            activity.ioTabClear();
        }
        return activity;
    }
}
