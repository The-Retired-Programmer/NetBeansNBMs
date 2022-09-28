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

import uk.theretiredprogrammer.actionssupportimplementation.ProjectOutputTabs;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.actionssupportimplementation.CLICommandParser;
import uk.theretiredprogrammer.actionssupportimplementation.CopyStreamThread;
import uk.theretiredprogrammer.actionssupportimplementation.CopyThread;
import static uk.theretiredprogrammer.actionssupportimplementation.CopyThread.DEFAULT_MILLISECS2FLUSH;
import static uk.theretiredprogrammer.actionssupportimplementation.CopyThread.NO_MILLISECS2FLUSH;
import uk.theretiredprogrammer.actionssupportimplementation.CopyToWriterThread;
import uk.theretiredprogrammer.actionssupportimplementation.CopyFromReaderThread;

public class CLIExec {

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
    private CopyThread stderrthread;
    private CopyThread stdinthread;
    private int stdinFlushPeriod = 0;
    private boolean stdinFromOutputWindow;
    private boolean stderrToOutputWindow;
    private boolean stdoutToOutputWindow;
    private final String tabkey;
    private Consumer<OutputWriter> postprocessing = null;
    private Consumer<OutputWriter> preprocessing = null;
    private boolean needscancel;

    public CLIExec(FileObject dir, String clicommand) {
        tabkey = dir.getName();
        pb = new ProcessBuilder(CLICommandParser.toPhrases(clicommand.replace("${NODEPATH}", FileUtil.toFile(dir).getPath())));
        pb.directory(FileUtil.toFile(dir));
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        pb.redirectInput(ProcessBuilder.Redirect.PIPE);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
    }

    public CLIExec needsCancel() {
        this.needscancel = true;
        return this;
    }

    public CLIExec stdout(FileObject fo) {
        return stdout(FileUtil.toFile(fo));
    }

    public CLIExec stdout(File file) {
        pb.redirectOutput(ProcessBuilder.Redirect.to(file));
        stdoutthreadcreate = () -> null;
        return this;
    }

    public CLIExec stdout(OutputStream os) {
        this.stdoutStream = os;
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
        stdoutthreadcreate = () -> new CopyStreamThread("stdout", process.getInputStream(), stdoutStream, this, NO_MILLISECS2FLUSH);
        return this;
    }

    public CLIExec stdout(Writer wtr) {
        this.stdoutWriter = wtr;
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
        stdoutthreadcreate = () -> new CopyToWriterThread("stdout", process.inputReader(), stdoutWriter, this);
        return this;
    }

    public CLIExec stdoutToOutputWinow() {
        this.stdoutToOutputWindow = true;
        return this;
    }

    private CLIExec setIfStdoutToOutputWindow(Writer wtr) {
        if (stdoutToOutputWindow) {
            stdout(wtr);
        }
        return this;
    }

    public CLIExec stderr(FileObject fo) {
        return stderr(FileUtil.toFile(fo));
    }

    public CLIExec stderr(File file) {
        pb.redirectError(ProcessBuilder.Redirect.to(file));
        stderrthreadcreate = () -> null;
        return this;
    }

    public CLIExec stderr(OutputStream os) {
        this.stderrStream = os;
        pb.redirectError(ProcessBuilder.Redirect.PIPE);
        stderrthreadcreate = () -> new CopyStreamThread("stderr", process.getErrorStream(), stderrStream, this, NO_MILLISECS2FLUSH);

        return this;
    }

    public CLIExec stderr(Writer wtr) {
        this.stderrWriter = wtr;
        pb.redirectError(ProcessBuilder.Redirect.PIPE);
        stderrthreadcreate = () -> new CopyToWriterThread("stderr", process.errorReader(), stderrWriter, this);
        return this;
    }

    public CLIExec stderrToOutputWindow() {
        this.stderrToOutputWindow = true;
        return this;
    }
    //

    private CLIExec setIfStderrToOutputWindow(Writer wtr) {
        if (stderrToOutputWindow) {
            stderr(wtr);
        }
        return this;
    }

    public CLIExec stderr2stdoutput() {
        pb.redirectErrorStream(true);
        return this;
    }

    public CLIExec stdin(FileObject fo) {
        return stdin(FileUtil.toFile(fo));
    }

    public CLIExec stdin(File file) {
        pb.redirectInput(ProcessBuilder.Redirect.from(file));
        stdinthreadcreate = () -> null;
        return this;
    }

    public CLIExec stdin(InputStream is) {
        this.stdinStream = is;
        pb.redirectInput(ProcessBuilder.Redirect.PIPE);
        stdinthreadcreate = () -> new CopyStreamThread("stdin", stdinStream, process.getOutputStream(), this, stdinFlushPeriod);
        return this;
    }

    public CLIExec stdin(Reader rdr) {
        this.stdinReader = rdr;
        pb.redirectInput(ProcessBuilder.Redirect.PIPE);
        stdinthreadcreate = () -> new CopyFromReaderThread("stdin", stdinReader, process.outputWriter(), this, stdinFlushPeriod);
        return this;
    }

    public CLIExec stdinFromOutputWindow() {
        this.stdinFromOutputWindow = true;
        return this;
    }

    public boolean isStdinFromOutputWindow() {
        return stdinFromOutputWindow;
    }

    private CLIExec setIfStdinFromOutputWindow(Reader rdr) {
        if (stdinFromOutputWindow) {
            stdin(rdr);
        }
        return this;
    }

    public CLIExec stdinFlushPeriod(int millisecs) {
        this.stdinFlushPeriod = millisecs;
        return this;
    }

    public CLIExec stdinFlushPeriod() {
        this.stdinFlushPeriod = DEFAULT_MILLISECS2FLUSH;
        return this;
    }

    public CLIExec preprocessing(Consumer<OutputWriter> preprocessing) {
        this.preprocessing = preprocessing;
        return this;
    }

    public CLIExec postprocessing(Consumer<OutputWriter> postprocessing) {
        this.postprocessing = postprocessing;
        return this;
    }

    private void stdinClose() {
        try {
            stdinthread.close_target();
        } catch (IOException ex) {
            printerror("closing stdin", ex.getLocalizedMessage());
        }
    }

    private void processCancel() {
        process.destroy();
    }

    public void executeUsingOutput(String startmessage) {
        InputOutput io = needscancel
                ? ProjectOutputTabs.getDefault().getCancellable(startmessage, () -> processCancel())
                : (isStdinFromOutputWindow()
                        ? ProjectOutputTabs.getDefault().get(tabkey, () -> stdinClose())
                        : ProjectOutputTabs.getDefault().get(tabkey));
        OutputWriter outwtr = io.getOut();
        OutputWriter errwtr = io.getErr();
        outwtr.println(startmessage);
        setIfStderrToOutputWindow(errwtr);
        setIfStdoutToOutputWindow(outwtr);
        setIfStdinFromOutputWindow(io.getIn());
        io.setInputVisible(isStdinFromOutputWindow());
        if (preprocessing != null) {
            preprocessing.accept(errwtr);
        }
        execute();
        if (postprocessing != null) {
            postprocessing.accept(errwtr);
        }
        outwtr.println("... done");
    }

    public void executeUsingOutput() {
        InputOutput io = isStdinFromOutputWindow()
                ? ProjectOutputTabs.getDefault().get(tabkey, () -> stdinClose())
                : ProjectOutputTabs.getDefault().get(tabkey);
        OutputWriter outwtr = io.getOut();
        OutputWriter errwtr = io.getErr();
        setIfStderrToOutputWindow(errwtr);
        setIfStdoutToOutputWindow(outwtr);
        setIfStdinFromOutputWindow(io.getIn());
        io.setInputVisible(isStdinFromOutputWindow());
        if (preprocessing != null) {
            preprocessing.accept(errwtr);
        }
        execute();
        if (postprocessing != null) {
            postprocessing.accept(errwtr);
        }
    }

    public void execute() {
        try {
            process = pb.start();
            stderrthread = stderrthreadcreate.get();
            if (stderrthread != null) {
                stderrthread.start();
            }
            CopyThread stdoutthread = stdoutthreadcreate.get();
            if (stdoutthread != null) {
                stdoutthread.start();
            }
            stdinthread = stdinthreadcreate.get();
            if (stdinthread != null) {
                stdinthread.start();
            }
            process.waitFor();
            if (stdoutthread != null && stdoutthread.isAlive()) {
                stdoutthread.flush_target();
                stdoutthread.join();
            }
            if (stdinthread != null && stdinthread.isAlive()) {
                stdinthread.flush_target();
                stdinthread.join();
            }
            if (stderrthread != null && stderrthread.isAlive()) {
                stderrthread.flush_target();
                stderrthread.join();
            }
        } catch (InterruptedException ex) {
            printerror("CLIExec interrupted", ex.getLocalizedMessage());
        } catch (IOException ex) {
            printerror("CLIExec IO error", ex.getLocalizedMessage());
        }
    }

    public void printerror(String phase, String exmsg) {
        try {
            if (stderrthread != null) {
                stderrthread.println(phase + "; " + exmsg);
            } else {
                StatusDisplayer.getDefault().setStatusText(phase + "; " + exmsg);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
