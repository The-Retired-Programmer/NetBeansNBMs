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
package uk.theretiredprogrammer.actionssupport;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actionssupportimplementation.FileChangeManager;

public class NodeDynamicActions {

    public static enum FileChangeType {
        CREATED, CHANGED, RENAMEDTO, RENAMEDFROM, DELETED
    }

    private Properties properties;
    private boolean disabled;
    private int propertycount;
    private final int maxactions;
    private DynamicCLIAction[] dynamicactions;
    private final FileObject filefolder;
    private final String actionpropertiesfilename;
    private final FileChangeManager filechangemanager;

    public NodeDynamicActions(FileObject filefolder, String actionpropertiesfilename, int maxactions) {
        this.maxactions = maxactions;
        this.filefolder = filefolder;
        this.actionpropertiesfilename = actionpropertiesfilename;
        this.filechangemanager = new FileChangeManager(filefolder);
        processCommandsFile();
        registerFile(actionpropertiesfilename, "properties", fct -> processCommandsFile());
    }

    public final void registerFile(String filename, String fileext, Consumer<FileChangeType> callback) {
        filechangemanager.register(filename, fileext, callback);
    }

    private void processCommandsFile() {
        disabled = true;
        propertycount = 0;
        properties = null;
        dynamicactions = new DynamicCLIAction[maxactions];
        FileObject propertiesFO = filefolder.getFileObject(actionpropertiesfilename, "properties");
        if (propertiesFO == null) {
            return;
        }
        properties = new Properties();
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
        propertycount = Integer.parseInt(pcount);
        disabled = false;
        // set up the commands
        int j = 0;
        while (j < propertycount) {
            dynamicactions[j] = new DynamicCLIAction(new CLICommand(filefolder, properties, j + 1));
            j++;
        }
    }

    public Action[] getActions(Action[] coreactions) {
        if (!disabled && maxactions > 0) {
            Action[] allactions = new Action[maxactions + coreactions.length + 1];
            int i = 0;
            for (Action a : coreactions) {
                allactions[i++] = a;
            }
            allactions[i++] = null;
            int j = 0;
            while (j < maxactions) {
                allactions[i++] = dynamicactions[j++];
            }
            return allactions;
        } else {
            return coreactions;
        }
    }
}
