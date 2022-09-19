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

import uk.theretiredprogrammer.actionssupport.CLICommand;
import java.io.IOException;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import org.openide.filesystems.FileUtil;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import static uk.theretiredprogrammer.actionssupport.CLICommand.InputOptions.FILE;
import static uk.theretiredprogrammer.actionssupport.CLICommand.InputOptions.UI;

public class CLIActionThread extends Thread {

    private final CLICommand cliCommand;
    private OutputWriter errorreporter;
    private ToProcessThread stdin = null;
    private Process process;

    public CLIActionThread(CLICommand cliCommand) {
        super("CLI Action Thread");
        this.cliCommand = cliCommand;
    }

    @Override
    public void run() {
        long start = currentTimeMillis();

        InputOutput io = IOProvider.get("output2").getIO(cliCommand.getDir().getName() + " - " + cliCommand.getLabel(), false,
                cliCommand.getActions(), null);
        io.select();
        if (cliCommand.getInopt() == UI) {
            io.setInputVisible(true);
        }
        try ( OutputWriter msg = io.getOut();  OutputWriter err = io.getErr()) {
            try {
                msg.reset();
                errorreporter = err;
                ProcessBuilder pb = new ProcessBuilder(CommandHandler.toPhrases(cliCommand.getCliCommandLine()))
                        .directory(FileUtil.toFile(cliCommand.getDir()));
                if (cliCommand.getInopt() == FILE) {
                    pb.redirectInput(FileUtil.toFile(cliCommand.getInputfile()));
                }
                FromProcessThread stderr;
                FromProcessThread stdout;
                process = pb.start();
                stderr = new FromProcessThread("stderr", process.errorReader(), err, (p, e) -> write2err(p, e));
                stderr.start();
                if (cliCommand.getInopt() == UI) {
                    stdin = new ToProcessThread("stdin", io.getIn(), process.outputWriter(), (p, e) -> write2err(p, e));
                    stdin.start();
                }
                stdout = new FromProcessThread("stdout", process.inputReader(), msg, (p, e) -> write2err(p, e));
                stdout.start();
                process.waitFor();
                //
                if (stdout.isAlive()) {
                    stdout.end();
                    stdout.join();
                }
                if (stdin != null && stdin.isAlive()) {
                    stdin.end();
                    stdin.join();
                }
                if (stderr.isAlive()) {
                    stderr.end();
                    stderr.join();
                }
            } catch (InterruptedException ex) {
                write2err(getName() + " interrupted", ex.getLocalizedMessage());
            } catch (IOException ex) {
                write2err(getName() + " IO error", ex.getLocalizedMessage());
            }
            int elapsed = round((currentTimeMillis() - start) / 1000F);
            msg.println("COMMAND COMPLETED (total time: " + Integer.toString(elapsed) + " seconds)");
        }
    }

    private void write2err(String phase, String exmsg) {
        errorreporter.println("Error: when " + phase + "; " + exmsg);
    }

    public void inputClose() {
        if (stdin != null) {
            stdin.close();
        }
    }

    public void cancel() {
        process.destroy();
    }
}
