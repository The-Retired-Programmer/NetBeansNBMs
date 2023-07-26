/*
 * Copyright 2023 richard linsdale.
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
package uk.theretiredprogrammer.util;

import java.util.Properties;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actions.DynamicAction;
import uk.theretiredprogrammer.actions.NodeActions;
import uk.theretiredprogrammer.actions.SaveBeforeAction;
import uk.theretiredprogrammer.activity.Activity;

/**
 * The Interface for the ActivitiesAndActions Factory.
 *
 * Methods to create objects from the ActivitiesAndActions Module.
 *
 */
public interface A3Factory {

    /**
     * Create an Activity
     *
     * @return an Activity instance
     */
    public Activity createActivity();

    /**
     * Create a DynamicAction.
     *
     * @param label the label to be used in the popup menu
     * @return a dynamicAction instance
     */
    public DynamicAction createDynamicAction(String label);

    /**
     * Create a NodeAction
     *
     * @param filefolder the node folder
     * @param actionpropertiesfilename the name (not extension) of the property
     * file which defines the node properties
     *
     * @return a NodeActions instance
     */
    public NodeActions createNodeActions(FileObject filefolder, String actionpropertiesfilename);

    /**
     * Create a SaveBeforeAction
     *
     * @param properties the properties from which the mode is extracted
     * @param propertyname the property name of the mode
     * @param defaultmode the default mode if the property is not defined
     * @return a SaveBeforeAction instance
     */
    public SaveBeforeAction createSaveBeforeAction(Properties properties, String propertyname, SaveBeforeAction.SaveBeforeActionMode defaultmode);
}
