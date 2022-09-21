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
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.SMALL_ICON;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.actionssupport.CLIExecUsingOutput;

public class SideBarActions {

    private final List<Action> actions = new ArrayList<>();

    @SuppressWarnings("CollectionsToArray")
    public final Action[] get() {
        return actions.toArray(Action[]::new);
    }

    public final SideBarActions add(String[] keys, CLIExecUsingOutput cliexec) {
        for (String key : keys) {
            add(key, cliexec);
        }
        return this;
    }

    public SideBarActions add(String key, CLIExecUsingOutput cliexec) {
        switch (key) {
            case "close":
                actions.add(new SideBarAction_Close(() -> cliexec.stdinClose()));
                break;
            case "cancel":
                actions.add(new SideBarAction_Cancel(() -> cliexec.processCancel()));
                break;
            default:
                actions.add(new SideBarAction_BAD());
        }
        return this;
    }

    private class SideBarAction_Close extends AbstractAction {

        private final Runnable onClick;

        SideBarAction_Close(Runnable onClick) {
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("uk/theretiredprogrammer/actionssupport/accept.png", false));
            this.onClick = onClick;
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            onClick.run();
        }
    }

    private class SideBarAction_Cancel extends AbstractAction {

        private final Runnable onClick;

        SideBarAction_Cancel(Runnable onClick) {
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("uk/theretiredprogrammer/actionssupport/cancel.png", false));
            this.onClick = onClick;
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            onClick.run();
        }
    }

    private class SideBarAction_BAD extends AbstractAction {

        SideBarAction_BAD() {
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("uk/theretiredprogrammer/actionssupport/thumb_down.png", false));
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // null action - as this toolbar entry is just created to show an error in configuration
        }
    }
}
