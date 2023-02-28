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

import java.util.function.Consumer;
import javax.swing.Action;

/**
 * NodeActions creates DynamicActions for a node, using a properties file to
 * define these actions.
 *
 * In many cases the node will be the project node.
 *
 * NodeActions observes the node folder containing the properties file, ensuring
 * the actions are updated whenever changes to the properties file occur.
 *
 * Additional files within the node folder can be observed, so that changes can
 * trigger updates to any associated objects.
 *
 * It provides a method for assembling node actions, combining various sources
 * of actions to create the Actions array required by a node definition.
 *
 */
public interface NodeActions {

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
         * The observed file has been deleted
         */
        DELETED
    }

    /**
     * RegisterFile
     *
     * Register an addition file to be observed.This file is expected to be
     * present (if exists) in the same folder as the actions properties file.
     *
     * @param filename The filename of the file to be observed
     * @param fileext The file extension of the file to be observed
     * @param callback The method to be called when a change is observed on this
     * file
     * @return this instance
     */
    public NodeActions registerFile(String filename, String fileext, Consumer<FileChangeType> callback);

    /**
     * Define the set of basic actions which are to be included in the Node's
     * actions popup.
     *
     * @param actions set of Actions.
     * @return this instance
     */
    public NodeActions setNodeBasicActions(Action... actions);

    /**
     * Define the set of basic actions which are to be included in the Project
     * Node's actions popup using the Standard Project NodeActions set
     *
     * @return this instance
     */
    public NodeActions setNodeBasicProjectActions();

    /**
     * Define the set of actions which are particular to all projects of this
     * Node's project type.
     *
     * @param actions set of Actions.
     * @return this instance
     */
    public NodeActions setNodeActions(DynamicAction... actions);

    /**
     * Get the array of enabled actions for this node.
     *
     * This includes the basicNodeActions, the NodeActions and any
     * DynamicActions that were defined by the properties file. Separators are
     * included to split the three groups.
     *
     * @return Array of actions;
     */
    public Action[] getAllNodeActions();
}
