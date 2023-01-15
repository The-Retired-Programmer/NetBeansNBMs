/*
 * Copyright 2022-23 Richard Linsdale.
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
package uk.theretiredprogrammer.activityimplementation;

import uk.theretiredprogrammer.activity.OutputDataTask;
import uk.theretiredprogrammer.activity.InputDataTask;
import uk.theretiredprogrammer.activity.OutputIO;
import uk.theretiredprogrammer.activity.InputIO;
import uk.theretiredprogrammer.activity.ActivityIO;
import uk.theretiredprogrammer.activity.Activity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import uk.theretiredprogrammer.actionssupport.UserReporting;
import static uk.theretiredprogrammer.activity.ActivityIO.STDERR;
import static uk.theretiredprogrammer.activity.ActivityIO.STDIN;
import static uk.theretiredprogrammer.activity.ActivityIO.STDOUT;

/**
 * External Process Activity
 *
 */
public class ExternalProcessActivity extends Activity {

    private Process process;
    private final String command;
    private final String args;
    private final FileObject dir;

    public ExternalProcessActivity(final String command, final String args, FileObject dir, ActivityIO activityio) {
        super(activityio);
        this.command = command;
        this.args = args;
        this.dir = dir;
    }

    @Override
    public Process createProcess() {
        try {
            NbProcessDescriptor processdescriptor = new NbProcessDescriptor(command, substituteNODEPATH(args, dir));
            process = processdescriptor.exec(null, null, FileUtil.toFile(dir));
            return process;
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage(activityio.iotabname, "create Process - failed to find Working Directory", ex);
            return null;
        }
    }

    @Override
    public InputDataTask[] createAllInputDataTasks() {
        return new InputDataTask[]{
            createInputDataTask(activityio.getInputIO(STDIN))
        };
    }

    private InputDataTask createInputDataTask(InputIO inputio) {
        InputStream is = inputio.getInputStream(activityio.iotabname);
        if (is == null) {
            BufferedReader brdr = inputio.getBufferReader(io, activityio.iotabname);
            if (brdr == null) {
                return null;
            } else {
                return new InputDataTask(inputio.name, activityio.iotabname).byLineReader(brdr, process.outputWriter());
            }
        } else {
            return new InputDataTask(inputio.name, activityio.iotabname).byStream(is, process.getOutputStream());
        }
    }
    
    @Override
    public OutputDataTask[] createAllOutputDataTasks() {
        return new OutputDataTask[]{
            createOutputDataTask(activityio.getOutputIO(STDOUT)),
            createErrorDataTask(activityio.getOutputIO(STDERR))
        };
    }

    private OutputDataTask createOutputDataTask(OutputIO outputio) {
        OutputStream os = outputio.getOutputStream(activityio.iotabname);
        if (os == null) {
            Writer wtr = outputio.getWriter(io, activityio.iotabname);
            if (wtr == null) {
                return null;
            } else {
                return new OutputDataTask(outputio.name, activityio.iotabname).byLineWriter(wtr, process.inputReader());
            }
        } else {
            return new OutputDataTask(outputio.name, activityio.iotabname).byStream(os, process.getInputStream());
        }
    }

    private OutputDataTask createErrorDataTask(OutputIO outputio) {
        OutputStream os = outputio.getOutputStream(activityio.iotabname);
        if (os == null) {
            Writer wtr = outputio.getWriter(io, activityio.iotabname);
            if (wtr == null) {
                return null;
            } else {
                return new OutputDataTask(outputio.name, activityio.iotabname).byLineWriter(wtr, process.errorReader());
            }
        } else {
            return new OutputDataTask(outputio.name, activityio.iotabname).byStream(os, process.getErrorStream());
        }
    }
}
