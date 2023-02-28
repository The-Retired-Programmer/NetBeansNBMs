/*
 * Copyright 2022-2023 Richard Linsdale.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.Action;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.actions.PasteAction;
import org.openide.filesystems.FileObject;
import org.openide.util.actions.SystemAction;

public class NodeActionsImp implements NodeActions {

    private FileChangeManager filechangemanager;
    private ActionsPropertyFile actionspropertyfile;
    private List<Action> basicactions = new ArrayList<>();
    private final List<DynamicAction> nodeactions = new ArrayList<>();

    public NodeActionsImp(FileObject filefolder, String actionpropertiesfilename) {
        this.filechangemanager = new FileChangeManager(filefolder);
        this.actionspropertyfile = new ActionsPropertyFile(filefolder, actionpropertiesfilename, filechangemanager);
    }

    /**
     * Register an addition file to be observed.
     *
     * This file is expected to be present (when existing) in the same folder as
     * the actions properties file.
     *
     * @param filename The filename of the file to be observed
     * @param fileext The file extension of the file to be observed
     * @param callback The method to be called when a change is observed on this
     * file
     */
    public NodeActions registerFile(String filename, String fileext, Consumer<FileChangeType> callback) {
        filechangemanager.register(filename, fileext, callback);
        return this;
    }

    public NodeActions setNodeBasicActions(Action... actions) {
        basicactions = Arrays.asList(actions);
        return this;
    }

    public NodeActions setNodeBasicProjectActions() {
        basicactions = Arrays.asList(
                CommonProjectActions.renameProjectAction(),
                CommonProjectActions.copyProjectAction(),
                SystemAction.get(PasteAction.class),
                CommonProjectActions.closeProjectAction()
        );
        return this;
    }

    public NodeActionsImp setNodeActions(DynamicAction... actions) {
        nodeactions.clear();
        for (DynamicAction action : actions) {
            if (action != null) {
                nodeactions.add(action);
            }
        }
        return this;
    }

    public Action[] getAllNodeActions() {
        return combine(basicactions, combine(selectOnlyEnabled(nodeactions),
                selectOnlyEnabled(actionspropertyfile.getActions()))).toArray(Action[]::new);
    }

    private List<Action> selectOnlyEnabled(List<DynamicAction> actions) {
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
