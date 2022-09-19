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
import java.io.InputStream;
import java.io.OutputStream;
import uk.theretiredprogrammer.actionssupportimplementation.CommandHandler;
import uk.theretiredprogrammer.actionssupportimplementation.CopyStreamThread;

public class CLIExec extends Thread {

    private Process process;
    private final InputStream input;
    private final OutputStream output;
    private final OutputStream err;
    private final String clicommand;

    public CLIExec(String clicommand, InputStream input, OutputStream output, OutputStream err) {
        super("CLI Exec Thread");
        this.input = input;
        this.output = output;
        this.err = err;
        this.clicommand = clicommand;
    }

    @Override
    public void run() {
        try {
            ProcessBuilder pb = new ProcessBuilder(CommandHandler.toPhrases(clicommand));
            CopyStreamThread stderr;
            CopyStreamThread stdout;
            CopyStreamThread stdin;
            process = pb.start();
            stderr = new CopyStreamThread("stderr", process.getErrorStream(), err, (p, e) -> write2err(p, e));
            stderr.start();
            stdin = new CopyStreamThread("stdin", input, process.getOutputStream(), (p, e) -> write2err(p, e));
            stdin.start();
            stdout = new CopyStreamThread("stdout", process.getInputStream(), output, (p, e) -> write2err(p, e));
            stdout.start();
            process.waitFor();
            //
            if (stdout.isAlive()) {
                stdout.join();
            }
            if (stdin.isAlive()) {
                stdin.join();
            }
            if (stderr.isAlive()) {
                stderr.join();
            }
        } catch (InterruptedException ex) {
            write2err(getName() + " interrupted", ex.getLocalizedMessage());
        } catch (IOException ex) {
            write2err(getName() + " IO error", ex.getLocalizedMessage());
        }
    }

    private void write2err(String phase, String exmsg) {
        System.out.println("Error: when " + phase + "; " + exmsg);
    }
}
