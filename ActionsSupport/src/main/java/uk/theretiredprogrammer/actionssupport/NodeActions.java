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

/**
 * NodeActions creates DynamicAsyncActions for a node, using a properties file
 * to define these actions.
 *
 * In many cases the node will be the project node.
 *
 * NodeActions observes the node folder containing the properties file, ensuring
 * the actions are updated whenever changes to the properties file occur.
 *
 * Additional files within the node folder can be observed, so that changes can
 * trigger updates to any associated objects.
 *
 * It provides a method for assembling node actions, combining various
 * sources of actions to create the Actions array required by a node definition.
 *
 * Structure of the Properties file.
 *
 * The properties file must include a property _COMMANDCOUNT_ which indicates
 * the number of actions being defined in this file.
 *
 * Each action definition must have two or more property lines defined:
 *
 * n.label - defines the label displayed in the popup list (required),
 *
 * n.command - defines the CLI command to be executed when the action is selected
 * (required),
 * 
 * n.tabname - defines the tab name to be used in the Output Window. Optional,
 * if not defined then the label property will be used.
 *
 * n.inputfrom - defines the source for the STDIN stream (optional). Value is
 * one of:
 *
 * "file" a file is to be used as the STDIN stream (see inputfile),
 * 
 * "ui" the Output window is to be the STDIN stream, providing keyboard input or
 *
 * "noinput" no input is provided for STDIN (default).
 *
 * n.inputfile - defines the input file for STDIN (required if inputfrom is set
 * to "file"),
 *
 * n.needscancel = yes - adds a cancel button to the Output Window sidebar. This
 * allows a user to cancel the Process created by the DynamicAsyncAction.
 *
 * Note that n must be an integer between 1 and _COMMANDCOUNT_.

 * @author richard linsdale
 */
public class NodeActions {

    /**
     * The types of file changes which are observed by NodeActions.
     */
    public static enum FileChangeType {

        /**
         * The observed file has been created
         */
        CREATED,
        /**
         * The observed file has been edited
         */
        CHANGED,
        /**
         * A file has been renamed as the observed file
         */
        RENAMEDTO,
        /**
         * The observed file has been renamed
         */
        RENAMEDFROM,
        /**
         * The observed f has been deleted
         */
        DELETED
    }

    private final FileChangeManager filechangemanager;
    private final ActionsPropertyFile actionspropertyfile;
    private List<Action> basicactions = new ArrayList<>();
    private final List<DynamicAsyncAction> nodeactions = new ArrayList<>();

    /**
     * Constructor
     * 
     * Defines the actions property file and its location.
     * 
     * @param filefolder the node folder (which contains the actions property
     * file)
     * @param actionpropertiesfilename the filename (no extension) of the
     * actions property file.
     */
    public NodeActions(FileObject filefolder, String actionpropertiesfilename) {
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
     * @param callback The method to be called when a change is observed on this file
     */
    public final void registerFile(String filename, String fileext, Consumer<FileChangeType> callback) {
        filechangemanager.register(filename, fileext, callback);
    }

    /**
     * Define the set of basic actions which are to be included in the Node's
     * actions popup.
     *
     * @param actions set of Actions.
     */
    public void setNodeBasicActions(Action... actions) {
        basicactions = Arrays.asList(actions);
    }

    /**
     * Define the set of actions which are particular to all projects of this
     * Node's project type.
     *
     * @param actions set of Actions.
     */
    public void setNodeActions(DynamicAsyncAction... actions) {
        nodeactions.clear();
        for (DynamicAsyncAction action : actions) {
            if (action != null) {
                nodeactions.add(action);
            }
        }
    }

    /**
     * Get the array of enabled actions for this node.
     *
     * This includes the basicNodeActions, the NodeActions and any
     * DynamicAsyncActions that were defined by the properties file. Separators
     * are included to split the three groups.
     *
     * @return Array of actions;
     */
    public Action[] getAllNodeActions() {
        return combine(basicactions, combine(selectOnlyEnabled(nodeactions),
                selectOnlyEnabled(actionspropertyfile.getActions()))).toArray(Action[]::new);
    }

    private List<Action> selectOnlyEnabled(List<DynamicAsyncAction> actions) {
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
