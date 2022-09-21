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

import java.io.IOException;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

public class CLIExecUsingOutput extends CLIExec {

    private final String tabtext;
    private boolean stdinFromUI = false;
    private Action[] sidebaractions = new Action[0];

    public CLIExecUsingOutput(FileObject dir, String tabtext, String clicommand) {
        super(dir, clicommand);
        this.tabtext = tabtext;
    }
    
    public CLIExecUsingOutput stdinFromUI() {
        this.stdinFromUI = true;
        return this;
    }
    
    public CLIExecUsingOutput sidebarActions(Action[] sidebaractions) {
        this.sidebaractions = sidebaractions;
        return this;
    }

    @Override
    public void execute() {
        long start = currentTimeMillis();
        InputOutput io = IOProvider.get("output2").getIO(tabtext, false,
                sidebaractions, null);
        io.select();
        io.setInputVisible(stdinFromUI);
        try ( OutputWriter msg = io.getOut();  OutputWriter err = io.getErr()) {
            msg.reset();
            stderr(err);
            stdout(msg);
            if (stdinFromUI){
               stdin(io.getIn());
            }
            super.execute();
            int elapsed = round((currentTimeMillis() - start) / 1000F);
            msg.println("COMMAND COMPLETED (total time: " + Integer.toString(elapsed) + " seconds)");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
