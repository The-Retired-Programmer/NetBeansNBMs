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
import org.openide.util.Lookup;
import uk.theretiredprogrammer.actions.DynamicAction;
import uk.theretiredprogrammer.actions.NodeActions;
import uk.theretiredprogrammer.actions.SaveBeforeAction;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.activity.ActivityIOTab;

/**
 * The ActionsandActivity Factory
 * 
 * methods to create objects from the ActionsAndActivities Library.
 *
 */
public class ActionsAndActivitiesFactory {

    /**
     * Test if the ActionsAndActivities Services NBM is installed.
     *
     * @return true if available
     */
    public static boolean IsActionsAndActivitiesAvailable() {
        A3Factory factory = Lookup.getDefault().lookup(A3Factory.class);
        return factory != null;
    }

    /**
     * Create an Activity
     *
     * @return an Activity instance
     * @throws ApplicationException a failure Exception
     */
    public static Activity createActivity() throws ApplicationException {
        A3Factory factory = Lookup.getDefault().lookup(A3Factory.class);
        if (factory == null) {
            throw new ApplicationException("Activity and Actions NBM is not loaded");
        }
        return factory.createActivity();
    }

    /**
     * Get an ActivityIOTab
     *
     * @param iotabname the IO Tab name - used to lookup the IO Tab
     * @return an ActivityIOTab instance
     * @throws ApplicationException a failure Exception
     */
    public static ActivityIOTab getActivityIOTab(String iotabname) throws ApplicationException {
        A3Factory factory = Lookup.getDefault().lookup(A3Factory.class);
        if (factory == null) {
            throw new ApplicationException("Activity and Actions NBM is not loaded");
        }
        return factory.getActivityIOTab(iotabname);
    }

    /**
     * create a DynamicAction
     *
     * @param label the label that will be used in a popup menu
     * @return a DynamicAction
     * @throws ApplicationException a failure Exception
     */
    public static DynamicAction createDynamicAction(String label) throws ApplicationException {
        A3Factory factory = Lookup.getDefault().lookup(A3Factory.class);
        if (factory == null) {
            throw new ApplicationException("Activity and Actions NBM is not loaded");
        }
        return factory.createDynamicAction(label);
    }

    /**
     * Create a NodeActions
     *
     * @param filefolder the node folder
     * @param actionpropertiesfilename the filename (not to include the
     * extension) of the node properties file
     * @return a Nodections instance
     * @throws ApplicationException a failure Exception
     */
    public static NodeActions createNodeActions(FileObject filefolder, String actionpropertiesfilename) throws ApplicationException {
        A3Factory factory = Lookup.getDefault().lookup(A3Factory.class);
        if (factory == null) {
            throw new ApplicationException("Activity and Actions NBM is not loaded");
        }
        return factory.createNodeActions(filefolder, actionpropertiesfilename);
    }

    /**
     * Create a SaveBeforeAction Instance
     *
     * Process the properties to select the required mode.
     *
     * @param properties the properties
     * @param propertyname the property key - used to find the Mode value
     * @param defaultmode the mode value to use if the property is not found in
     * properties
     * @return A SaveBeforeAction instance
     * @throws uk.theretiredprogrammer.util.ApplicationException if illegal
     * property value is used
     */
    public static SaveBeforeAction createSaveBeforeAction(Properties properties, String propertyname, SaveBeforeAction.SaveBeforeActionMode defaultmode) throws ApplicationException {
        A3Factory factory = Lookup.getDefault().lookup(A3Factory.class);
        if (factory == null) {
            throw new ApplicationException("Activity and Actions NBM is not loaded");
        }
        return factory.createSaveBeforeAction(properties, propertyname, defaultmode);
    }
}
