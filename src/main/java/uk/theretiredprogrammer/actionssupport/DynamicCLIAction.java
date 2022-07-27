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

import uk.theretiredprogrammer.actionssupportimplementation.CLIActionThread;

public class DynamicCLIAction extends DynamicAction {

    private final CLICommand cliCommand;
    private CLIActionThread cliactionthread = null;

    @SuppressWarnings("LeakingThisInConstructor")
    public DynamicCLIAction(CLICommand cliCommand) {
        cliCommand.setParent(this);
        this.cliCommand = cliCommand;
        label(cliCommand.getLabel());
        onAction(() -> onCLIAction());
        setEnabled(cliCommand.getEnableIf().get());
    }
    
    public DynamicCLIAction enableIf() {
        setEnabled(cliCommand.getEnableIf().get());
        
        return this;
    }

    public void issueClose() {
        if (cliactionthread != null) {
            cliactionthread.inputClose();
        }
    }

    public void issueCancel() {
        if (cliactionthread != null) {
            cliactionthread.cancel();
        }
    }

    private void onCLIAction() {
        cliactionthread = new CLIActionThread(cliCommand);
        cliactionthread.start();
    }
}
