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
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.SMALL_ICON;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.ImageUtilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

public class InputOutputTab {

    public static InputOutput getClosable(String tabname, Runnable closeaction) {
        InputOutput io = IOProvider.get("output2").getIO(tabname, false,
                new Action[]{new SideBarAction_Close(closeaction)}, null);
        io.select();
        return io;
    }

    public static InputOutput get(String tabname) {
        InputOutput io = IOProvider.get("output2").getIO(tabname, false, new Action[]{}, null);
        io.select();
        return io;
    }

    public static InputOutput getCancellable(String tabname, Runnable cancelaction) {
        InputOutput io = IOProvider.get("output2").getIO(tabname, true,
                new Action[]{new SideBarAction_Cancel(cancelaction)}, null);
        io.select();
        return io;
    }

    private static class SideBarAction_Close extends AbstractAction {

        private final Runnable onClick;

        SideBarAction_Close(Runnable onClick) {
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("uk/theretiredprogrammer/actionssupport/accept.png", false));
            setEnabled(true);
            this.onClick = onClick;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            onClick.run();
        }
    }

    private static class SideBarAction_Cancel extends AbstractAction {

        private final Runnable onClick;

        SideBarAction_Cancel(Runnable onClick) {
            putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("uk/theretiredprogrammer/actionssupport/cancel.png", false));
            setEnabled(true);
            this.onClick = onClick;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            onClick.run();
        }
    }
}
