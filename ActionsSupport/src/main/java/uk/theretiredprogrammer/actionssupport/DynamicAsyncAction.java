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

import uk.theretiredprogrammer.actionssupportimplementation.Async;

/**
 * DynamicAsyncAction is a extension of DynamicAction.
 * <pre>
 * It's action method runs the Action method on another thread, so not
 * blocking the Action calling thread.
 * </pre>
 * @author richard linsdale
 */
public class DynamicAsyncAction extends DynamicAction {

    private Runnable asyncAction;

    /**
     * Construct a DynamicAsyncAction which is enabled
     *
     * @param label The label used when this DynamicAsyncAction is displayed.
     */
    public DynamicAsyncAction(String label) {
        super(label);
    }
    
    @Override
    public DynamicAsyncAction onAction(Runnable action) {
        this.asyncAction = action;
        super.onAction(() -> onAsyncAction());
        return this;
    }
    
    @Override
    public DynamicAsyncAction enable(boolean enable) {
        super.enable(enable);
        return this;
    }

    private void onAsyncAction() {
        Thread thd = new Async(asyncAction);
        thd.start();
    }
}
