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
package uk.theretiredprogrammer.actionssupportimplementation;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.SMALL_ICON;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.ImageUtilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public class ProjectOutputTabs {
    
    private static ProjectOutputTabs projectoutputtabs = null;
    
    public static ProjectOutputTabs getDefault() {
        if (projectoutputtabs == null) {
            projectoutputtabs = new ProjectOutputTabs();
        }
        return projectoutputtabs;
    }
    
    private static final Map<String, String> datafortabs = new HashMap<>();
    

    public void create(String key, String tabtext) {
        if (datafortabs.containsKey(key)) {
            // need a reporting process - error log?
        } else {
            datafortabs.put(key, tabtext);
        }
    }

    public InputOutput get(String key, Runnable closeaction) {
        String tabtext = datafortabs.get(key);
        if (tabtext == null) {
            // need a reporting process - error log?
            return null;
        } else {
            SideBarAction_Close close = new SideBarAction_Close();
            close.onClick(closeaction);
            close.setEnabled(true);
            InputOutput io = IOProvider.get("output2").getIO(tabtext, false,
                    new Action[] {close}, null);
            io.select();
            return io;
        }
    }
    
    public InputOutput get(String key) {
        String tabtext = datafortabs.get(key);
        if (tabtext == null) {
            // need a reporting process - error log?
            return null;
        } else {
            SideBarAction_Close close = new SideBarAction_Close();
            InputOutput io = IOProvider.get("output2").getIO(tabtext, false,new Action[] {close}, null);
            io.select();
            return io;
        }
    }
    
    public InputOutput getCancellable(String tabtext, Runnable cancelaction) {
        SideBarAction_Cancel cancel = new SideBarAction_Cancel();
        cancel.onClick(cancelaction);
        cancel.setEnabled(true);
        InputOutput io = IOProvider.get("output2").getIO(tabtext, true, new Action[] {cancel}, null);
            io.select();
            return io;
    }

    
    private class SideBarAction_Close extends AbstractAction {

        private Runnable onClick;

        SideBarAction_Close() {
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("uk/theretiredprogrammer/actionssupport/accept.png", false));
            setEnabled(false);
        }

        public void onClick(Runnable onClick) {
            this.onClick = onClick;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (onClick != null) {
                onClick.run();
            }
        }
    }

    private class SideBarAction_Cancel extends AbstractAction {

        private Runnable onClick;

        SideBarAction_Cancel() {
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("uk/theretiredprogrammer/actionssupport/cancel.png", false));
            setEnabled(false);
        }

        public void onClick(Runnable onClick) {
            this.onClick = onClick;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (onClick != null) {
                onClick.run();
            }
        }
    }

    // temp
//    public static Project getCurrentProject() {
//        Lookup lkup = Utilities.actionsGlobalContext();
//        return lkup.lookup(Project.class);
//    }
}
