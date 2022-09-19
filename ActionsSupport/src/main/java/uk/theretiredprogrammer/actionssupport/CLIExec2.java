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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Supplier;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import uk.theretiredprogrammer.actionssupportimplementation.CommandHandler;
import uk.theretiredprogrammer.actionssupportimplementation.CopyStreamThread;
import uk.theretiredprogrammer.actionssupportimplementation.CopyThread;
import static uk.theretiredprogrammer.actionssupportimplementation.CopyThread.DEFAULT_MILLISECS2FLUSH;
import static uk.theretiredprogrammer.actionssupportimplementation.CopyThread.NO_MILLISECS2FLUSH;
import uk.theretiredprogrammer.actionssupportimplementation.FromProcessThread;
import uk.theretiredprogrammer.actionssupportimplementation.ToProcessThread;

public class CLIExec2 extends Thread {

    private final ProcessBuilder pb;
    private Process process;
    private Supplier<CopyThread> stdinthreadcreate = () -> null;
    private Supplier<CopyThread> stdoutthreadcreate = () -> null;
    private Supplier<CopyThread> stderrthreadcreate = () -> null;
    private OutputStream stdoutStream = null;
    private OutputStream stderrStream = null;
    private InputStream stdinStream = null;
    private Writer stdoutWriter = null;
    private Writer stderrWriter = null;
    private Reader stdinReader = null;
    private CopyThread stderr;
    private CopyThread stdin;
    private int stdinFlushPeriod = 0;

    public CLIExec2(String clicommand) {
        super("CLI Exec Thread");
        pb = new ProcessBuilder(CommandHandler.toPhrases(clicommand));
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        pb.redirectInput(ProcessBuilder.Redirect.PIPE);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
    }

    public CLIExec2 directory(FileObject fo) {
        return directory(FileUtil.toFile(fo));
    }

    public CLIExec2 directory(File folder) {
        pb.directory(folder);
        return this;
    }

    public CLIExec2 stdout(FileObject fo) {
        return stdout(FileUtil.toFile(fo));
    }

    public CLIExec2 stdout(File file) {
        pb.redirectOutput(ProcessBuilder.Redirect.to(file));
        stdoutthreadcreate = () -> null ;
        return this;
    }

    public CLIExec2 stdout(OutputStream os) {
        this.stdoutStream = os;
        stdoutthreadcreate = ()-> new CopyStreamThread("stdout", process.getInputStream(), stdoutStream, this, NO_MILLISECS2FLUSH);
        return this;
    }

    public CLIExec2 stdout(Writer wtr) {
        this.stdoutWriter = wtr;
        stdoutthreadcreate = () -> new FromProcessThread("stdout", process.inputReader(), stdoutWriter, this);
        return this;
    }

    public CLIExec2 stderr(FileObject fo) {
        return stderr(FileUtil.toFile(fo));
    }

    public CLIExec2 stderr(File file) {
        pb.redirectError(ProcessBuilder.Redirect.to(file));
        stderrthreadcreate = () -> null ;
        return this;
    }

    public CLIExec2 stderr(OutputStream os) {
        this.stderrStream = os;
        stderrthreadcreate = ()-> new CopyStreamThread("stderr", process.getErrorStream(), stderrStream, this, NO_MILLISECS2FLUSH);
        
        return this;
    }

    public CLIExec2 stderr(Writer wtr) {
        this.stderrWriter = wtr;
        stderrthreadcreate = () -> new FromProcessThread("stderr", process.errorReader(), stderrWriter, this);
        return this;
    }
    
    public CLIExec2 stderr2stdoutput() {
        pb.redirectErrorStream(true);
        return this;
    }

    public CLIExec2 stdin(FileObject fo) {
        return stdin(FileUtil.toFile(fo));
    }

    public CLIExec2 stdin(File file) {
        pb.redirectInput(ProcessBuilder.Redirect.from(file));
        stdinthreadcreate = () -> null ;
        return this;
    }

    public CLIExec2 stdin(InputStream is) {
        this.stdinStream = is;
        stdinthreadcreate = () -> new CopyStreamThread("stdin", stdinStream, process.getOutputStream(), this, stdinFlushPeriod);
        return this;
    }

    public CLIExec2 stdin(Reader rdr) {
        this.stdinReader = rdr;
        stdinthreadcreate = () -> new ToProcessThread("stdin", stdinReader, process.outputWriter(), this, stdinFlushPeriod);
        return this;
    }

    public CLIExec2 stdinFlushPeriod(int millisecs) {
        this.stdinFlushPeriod = millisecs;
        return this;
    }
    
    public CLIExec2 stdinFlushPeriod() {
        this.stdinFlushPeriod = DEFAULT_MILLISECS2FLUSH;
        return this;
    }

    public void stdinClose() {
        try {
            stdin.close_target();
        } catch (IOException ex) {
           printerror("closing stdin", ex.getLocalizedMessage());
        }

    }

    public void execute() {
        try {
            process = pb.start();
            stderr = stderrthreadcreate.get();
            if (stderr != null) stderr.start();
            CopyThread stdout = stdoutthreadcreate.get();
            if (stdout != null) stdout.start();
            stdin = stdinthreadcreate.get();
            if (stdin != null) stdin.start();
            process.waitFor();
            if (stdout != null && stdout.isAlive()) {
                stdout.flush_target();
                stdout.join();
            }
            if (stdin != null && stdin.isAlive()) {
                stdin.flush_target();
                stdin.join();
            }
            if (stderr != null && stderr.isAlive()) {
                stderr.flush_target();
                stderr.join();
            }
        } catch (InterruptedException ex) {
            printerror(getName() + " interrupted", ex.getLocalizedMessage());
        } catch (IOException ex) {
            printerror(getName() + " IO error", ex.getLocalizedMessage());
        }
    }
     
    public void printerror(String phase, String exmsg) {
        try {
            stderr.println("CLIExec Error: when " + phase + "; " + exmsg);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
