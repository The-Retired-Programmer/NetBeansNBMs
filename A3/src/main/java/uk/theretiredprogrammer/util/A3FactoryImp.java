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
import org.openide.util.lookup.ServiceProvider;
import uk.theretiredprogrammer.actions.DynamicAction;
import uk.theretiredprogrammer.actions.DynamicActionImp;
import uk.theretiredprogrammer.actions.NodeActions;
import uk.theretiredprogrammer.actions.NodeActionsImp;
import uk.theretiredprogrammer.actions.SaveBeforeAction;
import uk.theretiredprogrammer.actions.SaveBeforeActionImp;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.activity.ActivityImp;

@ServiceProvider(service = A3Factory.class)
public class A3FactoryImp implements A3Factory {

    @Override
    public DynamicAction createDynamicAction(String label) {
        return new DynamicActionImp(label);
    }

    @Override
    public Activity createActivity() {
        return new ActivityImp();
    }

    @Override
    public NodeActions createNodeActions(FileObject filefolder, String actionpropertiesfilename) {
        return new NodeActionsImp(filefolder, actionpropertiesfilename);
    }

    @Override
    public SaveBeforeAction createSaveBeforeAction(Properties properties, String propertyname, SaveBeforeAction.SaveBeforeActionMode defaultmode) {
        return new SaveBeforeActionImp(properties, propertyname, defaultmode);
    }
}
