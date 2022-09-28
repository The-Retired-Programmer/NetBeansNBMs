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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.DynamicMenuContent;

public class DynamicAction extends AbstractAction {

    private Runnable action;

    public DynamicAction(String label) {
        putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
        setEnabled(true);
        putValue("popupText", label);
    }

    public DynamicAction onAction(Runnable action) {
        this.action = action;
        return this;
    }

    public DynamicAction enable(boolean enable) {
        setEnabled(enable);
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (action != null) {
            action.run();
        }
    }
}
