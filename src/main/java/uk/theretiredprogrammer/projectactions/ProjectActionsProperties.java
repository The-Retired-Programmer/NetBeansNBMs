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
package uk.theretiredprogrammer.projectactions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.projectactions.FileChangeManager.FileChangeType;

public class ProjectActionsProperties {

    private final static String PROPERTIESFILENAME = "projectactions";

    private Properties prop;
    private boolean disabled;
    private int propertycount;
    private final int requiredactions;
    private final ProjectAction[] projectactions;
    private final FileObject projectdir;
    
    public ProjectActionsProperties(FileObject projectdir, int requiredactions) {
        this.requiredactions = requiredactions;
        this.projectdir = projectdir;
        projectactions = new ProjectAction[requiredactions];
        int j = 0;
        while (j < requiredactions) {
            projectactions[j++] = new ProjectAction(projectdir);
        }
        loadPropertiesAndConfigureActions();
    }
    
    public void registerFiles(FileChangeManager fcm) {
        fcm.registerForCallback(PROPERTIESFILENAME, "properties", fct-> propertiesfilechangecallback(fct));
    }
    
    private void propertiesfilechangecallback(FileChangeType filechangetype) {
        // clear all the commands
        int j = 0;
        while (j < propertycount) {
            projectactions[j].clearCommand();
            j++;
        }
        loadPropertiesAndConfigureActions();
    }
    
    private void loadPropertiesAndConfigureActions() {
        disabled = true;
        propertycount = 0;
        prop = null;
        FileObject propertiesFO = projectdir.getFileObject(PROPERTIESFILENAME, "properties");
        if (propertiesFO == null) {
            return;
        }
        prop = new Properties();
        try {
            try ( InputStream propsin = propertiesFO.getInputStream()) {
                prop.load(propsin);
            }
        } catch (IOException ex) {
            return;
        }
        String pcount = prop.getProperty("COMMANDCOUNT");
        if (pcount == null) {
            return;
        }
        propertycount = Integer.parseInt(pcount);
        disabled = false;
        // set up the commands
        int j = 0;
        while (j < propertycount) {
            insertActionParameters(projectactions[j], Integer.toString(j));
            j++;
        }
    }

    private void insertActionParameters(ProjectAction action, String prefix) {
        //<prefix>.label and <prefix>.command 
        String label = prop.getProperty(prefix + ".label");
        if (label != null) {
            String command = prop.getProperty(prefix + ".command");
            if (command != null) {
                action.setCommand(label, command);
            }
        }
    }

    public Action[] getActions(Action[] coreactions) {
        if (!disabled && requiredactions > 0) {
            Action[] allactions = new Action[requiredactions + coreactions.length + 1];
            int i = 0;
            for (Action a : coreactions) {
                allactions[i++] = a;
            }
            allactions[i++] = null;
            int j = 0;
            while (j < requiredactions) {
                allactions[i++] = projectactions[j++];
            }
            return allactions;
        } else {
            return coreactions;
        }
    }
}
