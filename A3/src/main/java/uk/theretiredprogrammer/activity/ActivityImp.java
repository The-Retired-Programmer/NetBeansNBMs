/*
 * Copyright 2023 richard linsdale.
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
package uk.theretiredprogrammer.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.RequestProcessor;
import static uk.theretiredprogrammer.activity.Activity.InputDataTransferStyle.BUFFERED_READER;
import static uk.theretiredprogrammer.activity.Activity.InputDataTransferStyle.CHAR_READER;
import static uk.theretiredprogrammer.activity.Activity.OutputDataTransferStyle.BUFFERED_WRITER;
import static uk.theretiredprogrammer.activity.Activity.OutputDataTransferStyle.CHAR_WRITER;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

public class ActivityImp implements Activity {

    private static enum ActivityType {
        UNDEFINED, EXTERNAL_PROCESS, DUPLEX_TRANSFER
    };

    //
    private static String substituteNODEPATH(String source, FileObject node) {
        return source.replace("${NODEPATH}", FileUtil.toFile(node).getAbsolutePath());
    }

    // builder like methods to define Activity configuration
    private ActivityType activitytype = ActivityType.UNDEFINED;

    private String iotabname;
    private boolean iotabreset;
    private InputOutput io;
    private Process process;
    private String command;
    private String args;
    private FileObject dir;
    private Runnable onTabClose;
    private final List<InputDataTask> allInputDataTasks = new ArrayList<>();
    private final List<OutputDataTask> allOutputDataTasks = new ArrayList<>();
    private final Map<String, InputConnection> inputconnections = new HashMap<>();
    private final Map<String, OutputConnection> outputconnections = new HashMap<>();

    @Override
    public Activity needsIOTab(String iotabname) {
        this.iotabname = iotabname;
        return this;
    }

    @Override
    public Activity ioTabClear() {
        iotabreset = true;
        return this;
    }

    @Override
    public Activity setExternalProcess(final String command, final String args, FileObject dir) {
        this.command = command;
        this.args = args;
        this.dir = dir;
        activitytype = ActivityType.EXTERNAL_PROCESS;
        //
        inputconnections.clear();
        inputconnections.put(STDIN, new InputConnection(STDIN, BUFFERED_READER, () -> process.getOutputStream(), () -> process.outputWriter()));
        outputconnections.clear();
        outputconnections.put(STDOUT, new OutputConnection(STDOUT, BUFFERED_WRITER, () -> process.getInputStream(), () -> process.inputReader()));
        outputconnections.put(STDERR, new OutputConnection(STDERR, BUFFERED_WRITER, () -> process.getErrorStream(), () -> process.errorReader()));
        return this;
    }

    @Override
    public Activity setDuplexTransfer(Supplier<OutputStream> txStreamSupplier, Supplier<InputStream> rxStreamSupplier, Runnable onTabClose) {
        activitytype = ActivityType.DUPLEX_TRANSFER;
        this.onTabClose = onTabClose;
        inputconnections.clear();
        inputconnections.put(TX, new InputConnection(TX, CHAR_READER, txStreamSupplier, null));
        outputconnections.clear();
        outputconnections.put(RX, new OutputConnection(RX, CHAR_WRITER, rxStreamSupplier, null));
        return this;
    }

    @Override
    public Activity setDuplexTransfer(Supplier<OutputStream> txStreamSupplier, Supplier<InputStream> rxStreamSupplier) {
        return setDuplexTransfer(txStreamSupplier, rxStreamSupplier, null);
    }

    @Override
    public void run(String message) {
        try {
            if (iotabname != null) {
                io = IOProvider.getDefault().getIO(iotabname, false);
                io.show();
                if (iotabreset) {
                    io.reset();
                }
                io.getOut().println(message);
                runActivity();
                io.getOut().println("... done");

            } else {
                runActivity();
            }
        } catch (ApplicationException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error when running an Activity", ex);
        } catch (FileNotFoundException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error - missing file when running an Activity", ex);
        }
    }

    @Override
    public void run() {
        try {
            if (iotabname != null) {
                io = IOProvider.getDefault().getIO(iotabname, false);
                io.show();
                if (iotabreset) {
                    io.reset();
                }
                runActivity();
            } else {
                runActivity();
            }
        } catch (ApplicationException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error when running an Activity", ex);
        } catch (FileNotFoundException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error - missing file when running an Activity", ex);
        }
    }

    private void runActivity() throws ApplicationException, FileNotFoundException {
        switch (activitytype) {
            case DUPLEX_TRANSFER:
                runDuplexTransferActivity();
                break;
            case EXTERNAL_PROCESS:
                runExternalProcessActivity();
                break;
        }
    }

    private void runDuplexTransferActivity() throws ApplicationException, FileNotFoundException {
        startAllDataTasks();
    }

    private void startAllDataTasks() throws ApplicationException, FileNotFoundException {
        allInputDataTasks.clear();
        for (InputConnection connection : inputconnections.values()) {
            allInputDataTasks.add(connection.startInputDataTask(iotabname, io));
        }
        allOutputDataTasks.clear();
        for (OutputConnection connection : outputconnections.values()) {
            allOutputDataTasks.add(connection.startOutputDataTask(iotabname, io));
        }
        if (io != null) {
            IOTabCloseWatch.watch(iotabname, io, () -> cancelTasksAndProcess());
        }
    }

    private void runExternalProcessActivity() throws ApplicationException, FileNotFoundException {
        try {
            process = null;
            try {
                NbProcessDescriptor processdescriptor = new NbProcessDescriptor(command, substituteNODEPATH(args, dir));
                process = processdescriptor.exec(null, null, FileUtil.toFile(dir));
            } catch (IOException ex) {
                UserReporting.exceptionWithMessage(iotabname, "create Process - failed to find Working Directory", ex);
                process = null;
            }
            startAllDataTasks();
            if (process != null) {
                process.waitFor();
            }
            for (var ioitem : allOutputDataTasks) {
                RequestProcessor.Task task = ioitem.getTask();
                if (task != null) {
                    task.waitFinished(10000);
                }
            }
            for (var ioitem : allInputDataTasks) {
                RequestProcessor.Task task = ioitem.getTask();
                if (task != null) {
                    task.waitFinished(10000);
                }
            }
            for (var ioitem : allOutputDataTasks) {
                if (ioitem != null) {
                    ioitem.close();
                }
            }
            for (var ioitem : allInputDataTasks) {
                if (ioitem != null) {
                    ioitem.close();
                }
            }
        } catch (InterruptedException ex) {
            UserReporting.exception(iotabname, ex);
        }
    }

    private void cancelTasksAndProcess() {
        for (var ioitem : allOutputDataTasks) {
            if (ioitem.getTask() != null && !ioitem.getTask().isFinished()) {
                ioitem.getTask().cancel();
            }
        }
        for (var ioitem : allInputDataTasks) {
            if (ioitem.getTask() != null && !ioitem.getTask().isFinished()) {
                ioitem.getTask().cancel();
            }
        }
        if (process != null) {
            process.destroy();
        }
        if (onTabClose != null) {
            onTabClose.run();
        }
    }

    private InputIO getInputIO(String name) throws ApplicationException {
        InputConnection connection = inputconnections.get(name);
        if (connection == null) {
            throw new ApplicationException("Undefined Connection name: " + name);
        }
        return connection.getInputIO();
    }

    private OutputIO getOutputIO(String name) throws ApplicationException {
        OutputConnection connection = outputconnections.get(name);
        if (connection == null) {
            throw new ApplicationException("Undefined Connection name: " + name);
        }
        return connection.getOutputIO();
    }

    // input configuration methods
    // InputIO configuration
    @Override
    public Activity inputEmpty(String name) throws ApplicationException {
        getInputIO(name).empty();
        return this;
    }

    @Override
    public Activity inputFromIOSTDIN(String name) throws ApplicationException {
        getInputIO(name).fromIOSTDIN();
        return this;
    }

    @Override
    public Activity inputFromFile(String name, FileObject fileobject) throws ApplicationException {
        getInputIO(name).fromFile(fileobject);
        return this;
    }

    @Override
    public Activity inputFromFile(String name, File file) throws ApplicationException {
        getInputIO(name).fromFile(file);
        return this;
    }

    @Override
    public Activity inputFromFile(String name, DataObject dataobject) throws ApplicationException {
        getInputIO(name).fromFile(dataobject);
        return this;
    }

    @Override
    public Activity inputFromFile(String name, InputStream instream) throws ApplicationException {
        getInputIO(name).fromFile(instream);
        return this;
    }

    @Override
    public Activity inputFromFile(String name, Reader reader) throws ApplicationException {
        getInputIO(name).fromFile(reader);
        return this;
    }

    // OutputIO configuration 
    @Override
    public Activity outputDiscard(String name) throws ApplicationException {
        getOutputIO(name).discard();
        return this;
    }

    @Override
    public Activity outputToIOSTDOUT(String name) throws ApplicationException {
        getOutputIO(name).toIOSTDOUT();
        return this;
    }

    @Override
    public Activity outputToIOSTDERR(String name) throws ApplicationException {
        getOutputIO(name).toIOSTDERR();
        return this;
    }

    @Override
    public Activity outputToFile(String name, FileObject fileobject) throws ApplicationException {
        getOutputIO(name).toFile(fileobject);
        return this;
    }

    @Override
    public Activity outputToFile(String name, File file) throws ApplicationException {
        getOutputIO(name).toFile(file);
        return this;
    }

    @Override
    public Activity outputToFile(String name, DataObject dataobject) throws ApplicationException {
        getOutputIO(name).toFile(dataobject);
        return this;
    }

    @Override
    public Activity outputToFile(String name, OutputStream outstream) throws ApplicationException {
        getOutputIO(name).toFile(outstream);
        return this;
    }

    @Override
    public Activity outputToFile(String name, Writer writer) throws ApplicationException {
        getOutputIO(name).toFile(writer);
        return this;
    }
}
