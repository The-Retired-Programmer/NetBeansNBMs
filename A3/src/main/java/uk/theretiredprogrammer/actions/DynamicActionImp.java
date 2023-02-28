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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.RequestProcessor;

public class DynamicActionImp extends AbstractAction implements DynamicAction {

    private Runnable action;
    private boolean isasync;

    public DynamicActionImp(String label) {
        putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
        putValue("popupText", label);
    }

    @Override
    public DynamicActionImp enable(boolean enable) {
        setEnabled(enable);
        return this;
    }

    @Override
    public DynamicActionImp onAction(Runnable action) {
        this.action = action;
        this.isasync = true;
        return this;
    }

    @Override
    public DynamicActionImp onActionAsync(Runnable action) {
        this.action = action;
        this.isasync = true;
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (action != null) {
            if (isasync) {
                RequestProcessor rp = new RequestProcessor("dynamicaction");
                rp.post(action);
            } else {
                action.run();
            }
        }
    }
}
