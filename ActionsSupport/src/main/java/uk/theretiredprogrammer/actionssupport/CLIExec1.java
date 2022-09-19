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

import java.io.File;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import uk.theretiredprogrammer.actionssupportimplementation.CommandHandler;

public class CLIExec1 extends Thread {

    private Process process;
    private final String clicommand;
    private final File infile;
    private final File outfile;
    private final File errfile;
    private final File folder;

    public CLIExec1(String clicommand, FileObject folder, FileObject infile, FileObject outfile, FileObject errfile) {
        super("CLI Exec Thread");
        this.infile = FileUtil.toFile(infile);
        this.outfile = FileUtil.toFile(outfile);
        this.errfile = FileUtil.toFile(errfile);
        this.clicommand = clicommand;
        this.folder = FileUtil.toFile(folder);
    }

    @Override
    public void run() {
        try {
            ProcessBuilder pb = new ProcessBuilder(CommandHandler.toPhrases(clicommand));
            pb.directory(folder);
            pb.redirectInput(infile);
            pb.redirectOutput(outfile);
            pb.redirectError(errfile);
            process = pb.start();
            process.waitFor();
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
