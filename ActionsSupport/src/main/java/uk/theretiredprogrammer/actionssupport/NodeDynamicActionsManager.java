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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actionssupportimplementation.ActionsPropertyFile;
import uk.theretiredprogrammer.actionssupportimplementation.FileChangeManager;

public class NodeDynamicActionsManager {

    public static enum FileChangeType {
        CREATED, CHANGED, RENAMEDTO, RENAMEDFROM, DELETED
    }

    private final FileChangeManager filechangemanager;
    private final ActionsPropertyFile actionspropertyfile;
    private List<Action> basicactions = new ArrayList<>();
    private List<DynamicCLIAction> nodeactions = new ArrayList<>();

    public NodeDynamicActionsManager(FileObject filefolder, String actionpropertiesfilename) {
        this.filechangemanager = new FileChangeManager(filefolder);
        this.actionspropertyfile = new ActionsPropertyFile(filefolder, actionpropertiesfilename, filechangemanager);
    }

    public final void registerFile(String filename, String fileext, Consumer<FileChangeType> callback) {
        filechangemanager.register(filename, fileext, callback);
    }

    public void setNodeBasicActions(Action... actions) {
        basicactions = Arrays.asList(actions);
    }

    public void setNodeActions(DynamicCLIAction... actions) {
        nodeactions = Arrays.asList(actions);
    }

    public Action[] getAllNodeActions() {
        return combine(basicactions, combine(selectOnlyEnabled(nodeactions),
                selectOnlyEnabled(actionspropertyfile.getPropertyActions()))).toArray(Action[]::new);
    }

    private List<Action> selectOnlyEnabled(List<DynamicCLIAction> actions) {
        return actions.stream().filter((a) -> a.isEnabled()).collect(Collectors.toList());
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
