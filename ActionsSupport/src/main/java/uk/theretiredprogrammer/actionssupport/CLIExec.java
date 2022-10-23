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

import uk.theretiredprogrammer.actionssupportimplementation.InputOutputTab;
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

/**
 * CLIExec implements both configuration prior to execution of a CLI style
 * command and also its execution.
 *
 * Builder style method chaining is provided for configuration includes both
 * general configuration and input/output options.
 *
 * Execution methods are available to utilise either an Output Window tab, in
 * addition to a basic execution option.
 *
 * Details of the CLI Command String, which includes limited parameter
 * substitution where ${NODEPATH} is replaced with the node folder path. The
 * command has to be parsed into CLI "phrases", such as options, option
 * parameters, filenames etc. As this constructor never can have a full
 * understanding of every command's syntax, it employs a basic parsing technique
 * (breaking on word breaks i.e. white space). This works in the vast majority
 * of cases, but there are times where the CLI "phrase" should include multiple
 * words. If this is the case, the phrase should be written with enclosing
 * double quotes. These quotes will be stripped from the generated command prior
 * to it being passed to ProcessBuilder.
 *
 * @author richard linsdale
 */
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
    private boolean stdinFromOutputWindow = false;
    private boolean stderrToOutputWindow = false;
    private boolean stdoutToOutputWindow = false;
    private Consumer<OutputWriter> postprocessing = null;
    private Consumer<OutputWriter> preprocessing = null;
    private boolean needscancel;
    private String tabname;

    /**
     * Create the initial CLIExec object with the mandatory information. The
     * initial state for IO is: STDIN - no data passed to stream, STDOUT -
     * discarded, STDERR - discarded, STDIN flush period = 0 (ie OFF).
     *
     * It can be configured by use of the various builder style methods.
     *
     * @param dir The node folder - will be used as the working directory when
     * executing this object.
     * @param clicommand the command to be run when executing this command.
     */
    public CLIExec(FileObject dir, String clicommand) {
        pb = new ProcessBuilder(CLICommandParser.toPhrases(clicommand.replace("${NODEPATH}", FileUtil.toFile(dir).getPath())));
        pb.directory(FileUtil.toFile(dir));
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        pb.redirectInput(ProcessBuilder.Redirect.PIPE);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
    }

    /**
     * A FileObject accepts STDOUT output.
     *
     * @param fo The FileObject that accepts data from the Processes STDOUT
     * stream.
     * @return this object
     */
    public CLIExec stdout(FileObject fo) {
        return stdout(FileUtil.toFile(fo));
    }

    /**
     * A File accepts STDOUT output.
     *
     * @param file The File that accepts data from the Processes STDOUT stream.
     * @return this object
     */
    public CLIExec stdout(File file) {
        pb.redirectOutput(ProcessBuilder.Redirect.to(file));
        stdoutthreadcreate = () -> null;
        stdoutToOutputWindow = false;
        return this;
    }

    /**
     * An OutputStream accepts STDOUT output.
     *
     * @param os The OutputStream that accepts data from the Processes STDOUT
     * stream.
     * @return this object
     */
    public CLIExec stdout(OutputStream os) {
        this.stdoutStream = os;
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
        stdoutthreadcreate = () -> new CopyStreamThread("stdout", process.getInputStream(), stdoutStream, this, NO_MILLISECS2FLUSH);
        stdoutToOutputWindow = false;
        return this;
    }

    /**
     * A Writer accepts STDOUT output.
     *
     * @param wtr The writer that accepts data from the Processes STDOUT stream.
     * @return this object
     */
    public CLIExec stdout(Writer wtr) {
        this.stdoutWriter = wtr;
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
        stdoutthreadcreate = () -> new CopyToWriterThread("stdout", process.inputReader(), stdoutWriter, this);
        stdoutToOutputWindow = false;
        return this;
    }

    /**
     * Present STDOUT in the Output Window.
     *
     * @return this object
     */
    public CLIExec stdoutToOutputWindow() {
        this.stdoutToOutputWindow = true;
        return this;
    }

    private CLIExec setIfStdoutToOutputWindow(Writer wtr) {
        if (stdoutToOutputWindow) {
            stdout(wtr);
            stdoutToOutputWindow = true;
        }
        return this;
    }

    /**
     * A FileObject accepts STDERR output.
     *
     * @param fo The FileObject that accepts data from the Processes STDERR
     * stream.
     * @return this object
     */
    public CLIExec stderr(FileObject fo) {
        return stderr(FileUtil.toFile(fo));
    }

    /**
     * A File accepts STDERR output.
     *
     * @param file The File that accepts data from the Processes STDERR stream.
     * @return this object
     */
    public CLIExec stderr(File file) {
        pb.redirectError(ProcessBuilder.Redirect.to(file));
        stderrthreadcreate = () -> null;
        stderrToOutputWindow = false;
        return this;
    }

    /**
     * An OutputStream accepts STDERR output.
     *
     * @param os The OutputStream that accepts data from the Processes STDERR
     * stream.
     * @return this object
     */
    public CLIExec stderr(OutputStream os) {
        this.stderrStream = os;
        pb.redirectError(ProcessBuilder.Redirect.PIPE);
        stderrthreadcreate = () -> new CopyStreamThread("stderr", process.getErrorStream(), stderrStream, this, NO_MILLISECS2FLUSH);
        stderrToOutputWindow = false;
        return this;
    }

    /**
     * A Writer accepts STDERR output.
     *
     * @param wtr The Writer that accepts data from the Processes STDERR stream.
     * @return this object
     */
    public CLIExec stderr(Writer wtr) {
        this.stderrWriter = wtr;
        pb.redirectError(ProcessBuilder.Redirect.PIPE);
        stderrthreadcreate = () -> new CopyToWriterThread("stderr", process.errorReader(), stderrWriter, this);
        stderrToOutputWindow = false;
        return this;
    }

    /**
     * Present STDERR in the Output Window.
     *
     * @return this object
     */
    public CLIExec stderrToOutputWindow() {
        this.stderrToOutputWindow = true;
        return this;
    }
    //

    private CLIExec setIfStderrToOutputWindow(Writer wtr) {
        if (stderrToOutputWindow) {
            stderr(wtr);
            stderrToOutputWindow = true;
        }
        return this;
    }

    /**
     * Present both STDOUT and STDERR outputs on STDOUT.
     *
     * @return this object
     */
    public CLIExec stderr2stdoutput() {
        pb.redirectErrorStream(true);
        return this;
    }

    /**
     * A FileObject is the STDIN source.
     *
     * @param fo The FileObject that provides data for the Processes STDIN
     * stream.
     * @return this object
     */
    public CLIExec stdin(FileObject fo) {
        return stdin(FileUtil.toFile(fo));
    }

    /**
     * A File is the STDIN source.
     *
     * @param file The File that provides data for the Processes STDIN stream.
     * @return this object
     */
    public CLIExec stdin(File file) {
        pb.redirectInput(ProcessBuilder.Redirect.from(file));
        stdinthreadcreate = () -> null;
        stdinFromOutputWindow = false;
        return this;
    }

    /**
     * An InputStream is the STDIN source.
     *
     * @param is The InputStream that provides data for the Processes STDIN
     * stream.
     * @return this object
     */
    public CLIExec stdin(InputStream is) {
        this.stdinStream = is;
        pb.redirectInput(ProcessBuilder.Redirect.PIPE);
        stdinthreadcreate = () -> new CopyStreamThread("stdin", stdinStream, process.getOutputStream(), this, stdinFlushPeriod);
        stdinFromOutputWindow = false;
        return this;
    }

    /**
     * A Reader is the STDIN source.
     *
     * @param rdr The Reader that provides data for the Processes STDIN stream.
     * @return this object
     */
    public CLIExec stdin(Reader rdr) {
        this.stdinReader = rdr;
        pb.redirectInput(ProcessBuilder.Redirect.PIPE);
        stdinthreadcreate = () -> new CopyFromReaderThread("stdin", stdinReader, process.outputWriter(), this, stdinFlushPeriod);
        stdinFromOutputWindow = false;
        return this;
    }

    /**
     * The Output Window is the STDIN source.
     *
     * @return this object
     */
    public CLIExec stdinFromOutputWindow() {
        this.stdinFromOutputWindow = true;
        return this;
    }

    private boolean isStdinFromOutputWindow() {
        return stdinFromOutputWindow;
    }

    private CLIExec setIfStdinFromOutputWindow(Reader rdr) {
        if (stdinFromOutputWindow) {
            stdin(rdr);
            stdinFromOutputWindow = true;
        }
        return this;
    }

    /**
     * Define the period of inactivity prior to flushing the STDIN stream.
     *
     * @param millisecs a milliseconds period
     * @return this object
     */
    public CLIExec stdinFlushPeriod(int millisecs) {
        this.stdinFlushPeriod = millisecs;
        return this;
    }

    /**
     * Use the default period of inactivity prior to flushing the STDIN stream.
     *
     * @return this object
     */
    public CLIExec stdinFlushPeriod() {
        this.stdinFlushPeriod = DEFAULT_MILLISECS2FLUSH;
        return this;
    }

    /**
     * Define a method to be executed prior to the main Execute Process running.
     *
     * This method will be executed as part of the Execute method, if the
     * OutputWindow is to be used for input or output.
     *
     * @param preprocessing the pre processing method
     * @return this object
     */
    public CLIExec preprocessing(Consumer<OutputWriter> preprocessing) {
        this.preprocessing = preprocessing;
        return this;
    }

    /**
     * Define a method to be executed after to the main Execute Process running.
     *
     * This method will be executed as part of the Execute method, if the
     * OutputWindow is to be used for input or output.
     *
     * @param postprocessing the post processing method
     * @return this object
     */
    public CLIExec postprocessing(Consumer<OutputWriter> postprocessing) {
        this.postprocessing = postprocessing;
        return this;
    }

    /**
     * Define the tab to be used, if the Output Window is to be used for input
     * or output.
     *
     * @param tabname that output window tabname to be used.
     * @return this object
     */
    public CLIExec ioTabName(String tabname) {
        this.tabname = tabname;
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

    /**
     * Add a cancel icon in the Output Window Toolbar.
     *
     * It's action will be to cancel the Process generated by the execute
     * command.
     *
     * @return this object
     */
    public CLIExec needsCancel() {
        this.needscancel = true;
        return this;
    }

    /**
     * Execute the CLI command as a process.
     *
     * The execution process will be connected to the defined STDIN, STDOUT and
     * STDERR data and execution started in its own Process. This method will
     * wait for the process to complete before returning.
     *
     * If an Output Window is being used, then the startmessage will be written
     * to the window prior to executing the command and the message "... doneE
     * will be written to the window once execution is completed.
     *
     * @param startmessage the start message to be written to the Output window.
     */
    public void execute(String startmessage) {
        if (stdinFromOutputWindow || stdoutToOutputWindow || stderrToOutputWindow) {
            executeUsingOutputWindow(startmessage);
        } else {
            simpleExecute();
        }
    }

    /**
     * Execute the CLI command as a process.
     *
     * The execution process will be connected to the defined STDIN, STDOUT and
     * STDERR data and execution started in its own Process. This method will
     * wait for the process to complete before returning.
     *
     */
    public void execute() {
        if (stdinFromOutputWindow || stdoutToOutputWindow || stderrToOutputWindow) {
            executeUsingOutputWindow(null);
        } else {
            simpleExecute();
        }
    }

    private void executeUsingOutputWindow(String startmessage) {
        InputOutput io = needscancel
                ? InputOutputTab.getCancellable(tabname, () -> processCancel())
                : (isStdinFromOutputWindow()
                        ? InputOutputTab.getClosable(tabname, () -> stdinClose())
                        : InputOutputTab.get(tabname));
        OutputWriter outwtr = io.getOut();
        OutputWriter errwtr = io.getErr();
        if (startmessage != null) {
            outwtr.println(startmessage);
        }
        setIfStderrToOutputWindow(errwtr);
        setIfStdoutToOutputWindow(outwtr);
        setIfStdinFromOutputWindow(io.getIn());
        io.setInputVisible(isStdinFromOutputWindow());
        if (preprocessing != null) {
            preprocessing.accept(errwtr);
        }
        simpleExecute();
        if (postprocessing != null) {
            postprocessing.accept(errwtr);
        }
        if (startmessage != null) {
            outwtr.println("... done");
        }
    }

    private void simpleExecute() {
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

    /**
     * Attempt to write an error message to the STDERR receiver.
     *
     * The error message will combine the Phase parameter and the Exception
     * Message parameter.
     *
     * If there is a defined STDERR receiver then the error message will be
     * written to that stream/writer/file.
     *
     * If the STDERR receiver has not been defined then the error message will
     * be written to the Status Display.
     *
     * @param phase the phase
     * @param exmsg the exception message
     */
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
