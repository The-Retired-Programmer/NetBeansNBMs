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

import uk.theretiredprogrammer.actionssupportimplementation.STDIN;
import uk.theretiredprogrammer.actionssupportimplementation.STDOUT;
import uk.theretiredprogrammer.actionssupportimplementation.STDERR;
import uk.theretiredprogrammer.actionssupportimplementation.IOCOMMANDS;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import java.util.function.Consumer;
import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;
import org.netbeans.api.io.OutputWriter;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import uk.theretiredprogrammer.actionssupportimplementation.IOCOMMANDS.CommandPair;
import uk.theretiredprogrammer.actionssupportimplementation.Logging;

/**
 * NbCLIExec implements both configuration of a CLI style command and also its
 * execution.
 *
 * Builder style method chaining is provided for configuration includes both
 * general configuration and input/output options.
 *
 * The CLI CommandArgs is processed to do limited parameter substitution,
 * ${NODEPATH} is replaced with the node folder path.
 */
public class NbCliDescriptor {

    private Process process;
    private Consumer<OutputWriter> postprocessing = null;
    private Consumer<OutputWriter> preprocessing = null;
    private String tabname;
    private FileObject dir;
    private String clicommand;
    private String cliargs;

    private final Logging logging;
    private final STDIN stdin;
    private final STDOUT stdout;
    private final STDERR stderr;
    private final IOCOMMANDS iocommands;
    private boolean iotabclear = false;

    /**
     * Create the initial NbCLIExec object with the mandatory information. It
     * can be further configured by use of the various builder style methods.
     *
     *
     * @param dir The node folder - will be used as the working directory when
     * executing this object.
     * @param clicommand the command to be run when executing this command.
     * @param cliargs the command arguements to use used when executing this
     * command.
     */
    public NbCliDescriptor(FileObject dir, String clicommand, String cliargs) {
        this.dir = dir;
        this.clicommand = clicommand;
        this.cliargs = cliargs;
        this.logging = new Logging();
        this.stdin = new STDIN(logging);
        this.stdout = new STDOUT(logging);
        this.stderr = new STDERR(logging);
        this.iocommands = new IOCOMMANDS(logging);
    }

    /**
     * Create the empty NbCLIExec object. It can be further configured by use of
     * the various builder style methods.
     */
    public NbCliDescriptor() {
        this(null, "", "");
    }

    /**
     * Clone a NbCLIExec object.
     *
     * @param source the object to clone from.
     */
    public NbCliDescriptor(NbCliDescriptor source) {
        this.dir = source.dir;
        this.clicommand = source.clicommand;
        this.cliargs = source.cliargs;
        this.logging = source.logging;
        this.stdin = new STDIN(source.stdin);
        this.stderr = new STDERR(source.stderr);
        this.stdout = new STDOUT(source.stdout);
        this.iocommands = new IOCOMMANDS(source.iocommands);
        this.preprocessing = source.preprocessing;
        this.postprocessing = source.postprocessing;
        this.tabname = source.tabname;
        this.iotabclear = source.iotabclear;
    }

    /**
     * Define the working folder for process execution. This is typically the
     * Node folder.
     *
     * @param dir the folder
     * @return this object
     */
    public NbCliDescriptor directory(FileObject dir) {
        this.dir = dir;
        return this;
    }

    /**
     * Define the cli command to be used for process execution.
     * 
     * @param clicommand the cli command
     * @return this object
     */
    public NbCliDescriptor cliCommand(String clicommand) {
        this.clicommand = clicommand;
        return this;
    }

    /**
     * Define the cli arguements to be used for process execution.
     * 
     * @param cliargs the cli arguements
     * @return this object
     */
    public NbCliDescriptor cliArgs(String cliargs) {
        this.cliargs = cliargs;
        return this;
    }

    /**
     * Define a method to be executed prior to the main Execute Process running.
     *
     * This method will be executed as part of the exec method, if the
     * OutputWindow is to be used for input or output.
     *
     * @param preprocessing the pre processing method
     * @return this object
     */
    public NbCliDescriptor preprocessing(Consumer<OutputWriter> preprocessing) {
        this.preprocessing = preprocessing;
        return this;
    }

    /**
     * Define a method to be executed after to the main Execute Process running.
     *
     * This method will be executed as part of the exec method, if the
     * OutputWindow is to be used for input or output.
     *
     * @param postprocessing the post processing method
     * @return this object
     */
    public NbCliDescriptor postprocessing(Consumer<OutputWriter> postprocessing) {
        this.postprocessing = postprocessing;
        return this;
    }

    /**
     * Define the tab to be used, if the Output Window is to be used for input
     * or output.
     *
     * @param tabname tabname to be used/reused.
     * @return this object
     */
    public NbCliDescriptor ioTabName(String tabname) {
        this.tabname = tabname;
        return this;
    }

    /**
     * Clear the IOTab before each usage.
     * 
     * @return this object
     */
    public NbCliDescriptor ioTabClear() {
        this.iotabclear = true;
        return this;
    }

    /**
     * Add a set of commands to the allowed commands.
     * 
     * @param commands the map containing the command definitions
     * @return this object
     */
    public NbCliDescriptor addCommands(Map<String, CommandPair> commands) {
        iocommands.addCommands(commands);
        return this;
    }

    /**
     * Add a command to the allowed commands.
     *
     * @param command the command word
     * @param action the command action
     * @param terminating true if this command is a terminating command - ie
     * after it is executed it will stop further command processing.
     * @return this object
     */
    public NbCliDescriptor addCommand(String command, Runnable action, boolean terminating) {
        iocommands.addCommand(command, action, terminating);
        return this;
    }

    /**
     * Add a Kill command to the allowed commands(to KILL the external process).
     *
     * @param command the command word
     * @return this object
     */
    public NbCliDescriptor addKillCommand(String command) {
        iocommands.addCommand(command, () -> process.destroy(), true);
        return this;
    }

    /**
     * Do not handle STDIN.
     *
     * @return this object
     */
    public NbCliDescriptor stdinIgnore() {
        stdin.ignore();
        return this;
    }

    /**
     * Pass an empty file to STDIN;
     *
     * @return this object
     */
    public NbCliDescriptor stdinEmpty() {
        stdin.empty();
        return this;
    }

    /**
     * Pass a FileObject to STDIN.
     *
     * @param fileobject the source
     * @return this object
     */
    public NbCliDescriptor stdinFromFile(FileObject fileobject) {
        stdin.fromFile(fileobject);
        return this;
    }

    /**
     * Pass a File to STDIN.
     *
     * @param file the source
     * @return this object
     */
    public NbCliDescriptor stdinFromFile(File file) {
        stdin.fromFile(file);
        return this;
    }

    /**
     * Pass a DataObject to STDIN.
     *
     * @param dataobject the source
     * @return this object
     */
    public NbCliDescriptor stdinFromFile(DataObject dataobject) {
        stdin.fromFile(dataobject);
        return this;
    }

    /**
     * Pass an InputStream to STDIN.
     *
     * @param instream the source
     * @return this object
     */
    public NbCliDescriptor stdinFromFile(InputStream instream) {
        stdin.fromFile(instream);
        return this;
    }

    /**
     * Pass a Reader to STDIN.
     *
     * @param reader the source
     * @return this object
     */
    public NbCliDescriptor stdinFromFile(BufferedReader reader) {
        stdin.fromFile(reader);
        return this;
    }

    /**
     * Do not handle STDOUT.
     *
     * @return this object
     */
    public NbCliDescriptor stdoutIgnore() {
        stdout.ignore();
        return this;
    }

    /**
     * Discard the STDOUT generated.
     *
     * @return this object
     */
    public NbCliDescriptor stdoutDiscard() {
        stdout.discard();
        return this;
    }

    /**
     * Pass the STDOUT to the IOTab.
     *
     * @return this object
     */
    public NbCliDescriptor stdoutToIO() {
        stdout.toOutputWriter();
        return this;
    }

    /**
     * Pass the STDOUT to a FileObject.
     *
     * @param fileobject the target
     * @return this object
     */
    public NbCliDescriptor stdoutToFile(FileObject fileobject) {
        stdout.toFile(fileobject);
        return this;
    }

    /**
     * Pass the STDOUT to a File.
     *
     * @param file the target
     * @return this object
     */
    public NbCliDescriptor stdoutToFile(File file) {
        stdout.toFile(file);
        return this;
    }

    /**
     * Pass the STDOUT to a DataObject.
     *
     * @param dataobject the target
     * @return this object
     */
    public NbCliDescriptor stdoutToFile(DataObject dataobject) {
        stdout.toFile(dataobject);
        return this;
    }

    /**
     * Pass the STDOUT to an OutputStream.
     *
     * @param outstream the target
     * @return this object
     */
    public NbCliDescriptor stdoutToFile(OutputStream outstream) {
        stdout.toFile(outstream);
        return this;
    }

    /**
     * Pass the STDOUT to a Writer.
     *
     * @param writer the target
     * @return this object
     */
    public NbCliDescriptor stdoutToFile(Writer writer) {
        stdout.toFile(writer);
        return this;
    }

    /**
     * Do not handle STDERR.
     *
     * @return this object
     */
    public NbCliDescriptor stderrIgnore() {
        stderr.ignore();
        return this;
    }

    /**
     * Discard the STDERR generated.
     *
     * @return this object
     */
    public NbCliDescriptor stderrDiscard() {
        stderr.discard();
        return this;
    }

    /**
     * Pass the STDERR to the IOTab.
     *
     * @return this object
     */
    public NbCliDescriptor stderrToIO() {
        stderr.toOutputWriter();
        return this;
    }

    /**
     * Pass the STDERR to a FileObject.
     *
     * @param fileobject the target
     * @return this object
     */
    public NbCliDescriptor stderrToFile(FileObject fileobject) {
        stderr.toFile(fileobject);
        return this;
    }

    /**
     * Pass the STDERR to a File.
     *
     * @param file the target
     * @return this object
     */
    public NbCliDescriptor stderrToFile(File file) {
        stderr.toFile(file);
        return this;
    }

    /**
     * Pass the STDERR to a DataObject.
     *
     * @param dataobject the target
     * @return this object
     */
    public NbCliDescriptor stderrToFile(DataObject dataobject) {
        stderr.toFile(dataobject);
        return this;
    }

    /**
     * Pass the STDERR to an OutputStream.
     *
     * @param outstream the target
     * @return this object
     */
    public NbCliDescriptor stderrToFile(OutputStream outstream) {
        stderr.toFile(outstream);
        return this;
    }

    /**
     * Pass the STDERR to a Writer.
     *
     * @param writer the target
     * @return this object
     */
    public NbCliDescriptor stderrToFile(Writer writer) {
        stderr.toFile(writer);
        return this;
    }

    /**
     * Execute the NbCliDescription - will run the defined command/arguements in
     * an external process.
     *
     * will create any necessary threads to manage data to and from that
     * process.
     *
     * can create (or reuse) an IOTab to optionally display the process's STDERR
     * and STDOUT.
     *
     * can create a simple command system taking input from IOTab.
     *
     * @param startmessage optional message which can be written to the IO Tab
     * prior to starting the external process (set to null to ignore message
     * writing)
     */
    @SuppressWarnings("null")
    public void exec(String startmessage) {
        OutputWriter outwtr = null;
        OutputWriter errwtr = null;
        if (tabname != null) {
            InputOutput io = IOProvider.getDefault().getIO(tabname, false);
            io.show();
            if (iotabclear) {
                io.reset();
            }
            outwtr = io.getOut();
            errwtr = io.getErr();
            if (startmessage != null) {
                outwtr.println(startmessage);
            }
            stderr.setOutputWriter(errwtr);
            stdout.setOutputWriter(outwtr);
            iocommands.setInputreader(io.getIn());
            if (preprocessing != null) {
                preprocessing.accept(errwtr);
            }
        }
        try {
            NbProcessDescriptor processdescriptor = new NbProcessDescriptor(clicommand, substituteNODEPATH(cliargs, dir));
            process = processdescriptor.exec(null, null, FileUtil.toFile(dir));
            // IOCOMMAND handling
            iocommands.startTransfer(null, null);
            // STDIN handling
            stdin.startTransfer(() -> process.getOutputStream(), () -> process.outputWriter());
            // STDOUT handling
            stdout.startTransfer(() -> process.getInputStream(), () -> process.inputReader());
            // STDERR handling
            stderr.startTransfer(() -> process.getErrorStream(), () -> process.errorReader());
            // wait for completion - and tidy up
            process.waitFor();
            iocommands.waitFinished(1000);
            stdin.waitFinished(10000);
            stdout.waitFinished(10000);
            stderr.waitFinished(10000);
            // close the process command handler ie IOCOMMAND
            iocommands.close(process);
            // close the process input stream ie STDIN
            stdin.close(process);
            //  close the process output stream ie STDOUT
            stdout.close(process);
            // close the process error stream ie TransferOUT
            stderr.close(process);
        } catch (InterruptedException | IOException ex) {
            logging.severe(ex.toString());
        }
        if (tabname != null) {
            if (postprocessing != null) {
                postprocessing.accept(errwtr);
            }
            if (startmessage != null) {
                outwtr.println("... done");
            }
        }
    }

    private String substituteNODEPATH(String source, FileObject node) {
        return source.replace("${NODEPATH}", FileUtil.toFile(node).getAbsolutePath());
    }
}
