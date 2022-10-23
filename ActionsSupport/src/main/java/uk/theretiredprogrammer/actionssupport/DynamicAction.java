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
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import org.openide.awt.DynamicMenuContent;

/**
 * A DynamicAction is an Action which can be enabled or disabled, it is not
 * displayed when disabled.
 * <p>
 * Methods return the object - allowing the builder style of method chaining
 * during construction, if required.
 * <p>
 * A DynamicAction is an implementation of the Netbeans AbstractAction class.
 *
 * @author richard linsdale
 */
public class DynamicAction extends AbstractAction implements ActionListener{

    private Runnable action;

    /**
     * Construct a DynamicAction which is enabled
     *
     * @param label The label used when this DynamicAction is displayed.
     */
    public DynamicAction(String label) {
        putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
        setEnabled(true);
        putValue("popupText", label);
    }

    /**
     * Set the Action method.
     *
     * @param action the method to be run when this action is selected.
     * @return this object
     */
    public DynamicAction onAction(Runnable action) {
        this.action = action;
        return this;
    }

    /**
     * Enable/Disable this Action.
     * 
     * @param enable true if enable, false if disable.
     * @return this object
     */
    public DynamicAction enable(boolean enable) {
        setEnabled(enable);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (action != null) {
            action.run();
        }
    }
}
