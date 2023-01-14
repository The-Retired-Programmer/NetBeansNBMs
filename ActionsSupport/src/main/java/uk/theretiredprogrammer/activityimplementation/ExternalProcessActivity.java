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
import uk.theretiredprogrammer.activity.DataTask;
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
            UserReporting.exceptionWithMessage(activityio.iotab.name, "create Process - failed to find Working Directory", ex);
            return null;
        }
    }

    @Override
    public DataTask[] createAllDataTasks() {
        return new DataTask[]{
            createInputDataTask(activityio.stdin),
            createOutputDataTask(activityio.stdout),
            createErrorDataTask(activityio.stderr)
        };
    }

    private InputDataTask createInputDataTask(InputIO inputio) {
        InputStream is = inputio.getInputStream(activityio.iotab.name);
        if (is == null) {
            BufferedReader brdr = inputio.getBufferReader(activityio.iotab.name);
            if (brdr == null) {
                return null;
            } else {
                return new InputDataTask(inputio.name, activityio.iotab.name).byLineReader(brdr, process.outputWriter());
            }
        } else {
            return new InputDataTask(inputio.name, activityio.iotab.name).byStream(is, process.getOutputStream());
        }
    }

    private OutputDataTask createOutputDataTask(OutputIO outputio) {
        OutputStream os = outputio.getOutputStream(activityio.iotab.name);
        if (os == null) {
            Writer wtr = outputio.getWriter(activityio.iotab.name);
            if (wtr == null) {
                return null;
            } else {
                return new OutputDataTask(outputio.name, activityio.iotab.name).byLineWriter(wtr, process.inputReader());
            }
        } else {
            return new OutputDataTask(outputio.name, activityio.iotab.name).byStream(os, process.getInputStream());
        }
    }

    private OutputDataTask createErrorDataTask(OutputIO outputio) {
        OutputStream os = outputio.getOutputStream(activityio.iotab.name);
        if (os == null) {
            Writer wtr = outputio.getWriter(activityio.iotab.name);
            if (wtr == null) {
                return null;
            } else {
                return new OutputDataTask(outputio.name, activityio.iotab.name).byLineWriter(wtr, process.errorReader());
            }
        } else {
            return new OutputDataTask(outputio.name, activityio.iotab.name).byStream(os, process.getErrorStream());
        }
    }
}
