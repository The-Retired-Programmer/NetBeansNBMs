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

import org.netbeans.api.io.InputOutput;
import org.openide.execution.NbProcessDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import static uk.theretiredprogrammer.activity.ActivityExecutor.DataTransferStyle.STREAM_OR_CHARACTER_TRANSFER;

public class ProgramActivityExecutor implements ActivityExecutor {

    private static String substituteNODEPATH(String source, FileObject node) {
        return source.replace("${NODEPATH}", FileUtil.toFile(node).getAbsolutePath());
    }

    private final String command;
    private final String args;
    private final FileObject dir;
    private Process process;

    public ProgramActivityExecutor(final String command, final String args, FileObject dir) {
        this.command = command;
        this.args = args;
        this.dir = dir;
    }

    @Override
    public DeviceDescriptor getDeviceDescriptor() {
        return null;
    }

    @Override
    public void open(ConnectionSTDIN stdin, ConnectionSTDOUT stdout, ConnectionSTDERR stderr, InputOutput io) throws Exception {
        process = null;
        NbProcessDescriptor processdescriptor = new NbProcessDescriptor(command, substituteNODEPATH(args, dir));
        process = processdescriptor.exec(null, null, FileUtil.toFile(dir));
        stdin.set(STREAM_OR_CHARACTER_TRANSFER, () -> process.getOutputStream(), () -> process.outputWriter());
        stdout.set(STREAM_OR_CHARACTER_TRANSFER, () -> process.getInputStream(), () -> process.inputReader());
        stderr.set(process.errorReader());
    }

    @Override
    public void run() throws Exception {

    }

    @Override
    public void waitUntilCompletion() throws Exception {
        if (process != null) {
            process.waitFor();
        }
    }

    @Override
    public void close() {
        if (process != null) {
            process.destroy();
        }
    }

    @Override
    public void cancel() {
        if (process != null) {
            process.destroy();
        }
    }
}
