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

import uk.theretiredprogrammer.actionssupportimplementation.CLIAsync;

public class DynamicCLIAction extends DynamicAction {

    private final CLIExecUsingOutput cliexec;

    @SuppressWarnings("LeakingThisInConstructor")
    public DynamicCLIAction(String label, CLIExecUsingOutput cliexec) {
        super(label);
        this.cliexec = cliexec;
        onAction(() -> onCLIAction());
    }
    
    public DynamicCLIAction enable(boolean enable) {
        setEnabled(enable);
        return this;
    }

    private void onCLIAction() {
        Thread thd = new CLIAsync(cliexec);
        thd.start();
    }
}
