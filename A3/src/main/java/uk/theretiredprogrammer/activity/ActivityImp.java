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
import java.util.function.Consumer;
import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

public class ActivityImp implements Activity {

    private ActivityExecutor activityexecutor;
    private String iotabname;
    private boolean iotabreset;
    private InputOutput io;
    private final ConnectionSTDIN connectionSTDIN = new ConnectionSTDIN();
    private DataTask taskSTDIN;
    private final ConnectionSTDOUT connectionSTDOUT = new ConnectionSTDOUT();
    private DataTask taskSTDOUT;
    private final ConnectionSTDERR connectionSTDERR = new ConnectionSTDERR();
    private DataTask taskSTDERR;
   
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
        activityexecutor = new ProgramActivityExecutor(command, args, dir);
        return this;
    }

    @Override
    public Activity setDevice(DeviceDescriptor dd) {
        activityexecutor = new DeviceActivityExecutor(dd);
        return this;
    }

    @Override
    public Activity setMethod(Consumer<Writer> method) {
        activityexecutor = new ConsumerMethodActivityExecutor(method);
        return this;
    }

    @Override
    public Activity setMethod(Runnable method) {
        activityexecutor = new RunnableMethodActivityExecutor(method);
        return this;
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
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error - processing file when running an Activity", ex);
        } catch (Exception ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error when running an Activity", ex);
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
            }
            runActivity();
        } catch (ApplicationException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error when running an Activity", ex);
        } catch (FileNotFoundException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error - missing file when running an Activity", ex);
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error - processing file when running an Activity", ex);
        } catch (Exception ex) {
            UserReporting.exceptionWithMessage(iotabname, "Error when running an Activity", ex);
        }
    }

    private void runActivity() throws ApplicationException, IOException, Exception {
        activityexecutor.open(connectionSTDIN, connectionSTDOUT, connectionSTDERR, io);
        taskSTDERR = connectionSTDERR.createTask(iotabname, io);
        taskSTDOUT = connectionSTDOUT.createTask(iotabname, io);
        taskSTDIN = connectionSTDIN.createTask(iotabname, io);
        if (io != null) {
            IOTabCloseWatch.watch(iotabname, io, () -> cancelTasksAndProcess());
        }
        activityexecutor.run();
        try {
            activityexecutor.waitUntilCompletion();
        } catch (Exception ex) {
            UserReporting.exception(iotabname, ex);
        }
        taskSTDIN.close();
        taskSTDOUT.close();
        taskSTDERR.close();
        activityexecutor.close();
    }

    private void cancelTasksAndProcess() {
        taskSTDIN.cancel();
        taskSTDOUT.cancel();
        taskSTDERR.cancel();
        try {
            activityexecutor.cancel();
        } catch (Exception ex) {
            UserReporting.exception(iotabname, ex);
        }
    }

    // STDIN configuration methods
    @Override
    public Activity stdinFromEmpty() throws ApplicationException {
        connectionSTDIN.empty();
        return this;
    }

    @Override
    public Activity stdinFromIOSTDIN() throws ApplicationException {
        connectionSTDIN.fromIOSTDIN();
        return this;
    }

    @Override
    public Activity stdinFromFile(FileObject fileobject) throws ApplicationException {
        connectionSTDIN.fromFile(fileobject);
        return this;
    }

    @Override
    public Activity stdinFromFile(File file) throws ApplicationException {
        connectionSTDIN.fromFile(file);
        return this;
    }

    @Override
    public Activity stdinFromFile(DataObject dataobject) throws ApplicationException {
        connectionSTDIN.fromFile(dataobject);
        return this;
    }

    @Override
    public Activity stdinFromFile(InputStream instream) throws ApplicationException {
        connectionSTDIN.fromFile(instream);
        return this;
    }

    @Override
    public Activity stdinFromFile(Reader reader) throws ApplicationException {
        connectionSTDIN.fromFile(reader);
        return this;
    }

    // STDOUT configuration
    @Override
    public Activity stdoutToDiscard() throws ApplicationException {
        connectionSTDOUT.discard();
        return this;
    }

    @Override
    public Activity stdoutToIOSTDOUT() throws ApplicationException {
        connectionSTDOUT.toIOSTDOUT();
        return this;
    }

    @Override
    public Activity stdoutToIOSTDERR() throws ApplicationException {
        connectionSTDOUT.toIOSTDERR();
        return this;
    }

    @Override
    public Activity stdoutToFile(FileObject fileobject) throws ApplicationException {
        connectionSTDOUT.toFile(fileobject);
        return this;
    }

    @Override
    public Activity stdoutToFile(File file) throws ApplicationException {
        connectionSTDOUT.toFile(file);
        return this;
    }

    @Override
    public Activity stdoutToFile(DataObject dataobject) throws ApplicationException {
        connectionSTDOUT.toFile(dataobject);
        return this;
    }

    @Override
    public Activity stdoutToFile(OutputStream outstream) throws ApplicationException {
        connectionSTDOUT.toFile(outstream);
        return this;
    }

    @Override
    public Activity stdoutToFile(Writer writer) throws ApplicationException {
        connectionSTDOUT.toFile(writer);
        return this;
    }

    // STDERR configuration
    @Override
    public Activity stderrToDiscard() throws ApplicationException {
        connectionSTDERR.discard();
        return this;
    }

    @Override
    public Activity stderrToIOSTDOUT() throws ApplicationException {
        connectionSTDERR.toIOSTDOUT();
        return this;
    }

    @Override
    public Activity stderrToIOSTDERR() throws ApplicationException {
        connectionSTDERR.toIOSTDERR();
        return this;
    }

    @Override
    public Activity stderrToFile(FileObject fileobject) throws ApplicationException {
        connectionSTDERR.toFile(fileobject);
        return this;
    }

    @Override
    public Activity stderrToFile(File file) throws ApplicationException {
        connectionSTDERR.toFile(file);
        return this;
    }

    @Override
    public Activity stderrToFile(DataObject dataobject) throws ApplicationException {
        connectionSTDERR.toFile(dataobject);
        return this;
    }

    @Override
    public Activity stderrToFile(OutputStream outstream) throws ApplicationException {
        connectionSTDERR.toFile(outstream);
        return this;
    }

    @Override
    public Activity stderrToFile(Writer writer) throws ApplicationException {
        connectionSTDERR.toFile(writer);
        return this;
    }
}
