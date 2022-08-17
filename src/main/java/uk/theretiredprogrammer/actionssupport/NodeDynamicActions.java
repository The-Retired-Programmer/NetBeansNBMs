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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actionssupportimplementation.FileChangeManager;

public class NodeDynamicActions {

    public static enum FileChangeType {
        CREATED, CHANGED, RENAMEDTO, RENAMEDFROM, DELETED
    }

    private final FileObject filefolder;
    private final String actionpropertiesfilename;
    private final FileChangeManager filechangemanager;

    public NodeDynamicActions(FileObject filefolder, String actionpropertiesfilename) {
        this.filefolder = filefolder;
        this.actionpropertiesfilename = actionpropertiesfilename;
        this.filechangemanager = new FileChangeManager(filefolder);
        processPropertiesFile();
        registerFile(actionpropertiesfilename, "properties", fct -> processPropertiesFile());
    }

    public final void registerFile(String filename, String fileext, Consumer<FileChangeType> callback) {
        filechangemanager.register(filename, fileext, callback);
    }

    private void processPropertiesFile() {
        dynamicactions = new ArrayList<>();
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
            dynamicactions.add(new DynamicCLIAction(new CLICommand(filefolder, properties, j)));
        }
    }

    private List<Action> basicactions;
    private List<DynamicCLIAction> nodeactions;
    private List<DynamicCLIAction> dynamicactions;

    public void setNodeBasicActions(Action... actions) {
        basicactions = Arrays.asList(actions);
    }

    public void setNodeActions(DynamicCLIAction... actions) {
        nodeactions = Arrays.asList(actions);
    }

    public Action[] getAllNodeActions() {
        return combine(basicactions, combine(selectOnlyEnabled(nodeactions), selectOnlyEnabled(dynamicactions))).toArray(Action[]::new);
    }
    
    private List<Action> selectOnlyEnabled(List<DynamicCLIAction> actions) {
        return actions.stream().filter((a)-> a.enableIf().isEnabled()).collect(Collectors.toList());
    }

    private List<Action> combine(List<Action> first, List<Action> second) {
        List<Action> combined = new ArrayList<>(first);
        if (!first.isEmpty() && !second.isEmpty()) {
            combined.add(null); // insert a separator in list
        }
        combined.addAll(second);
        return combined;
    }
}
