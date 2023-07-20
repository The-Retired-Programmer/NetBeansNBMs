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

import java.io.Writer;
import java.util.function.Consumer;
import org.netbeans.api.io.InputOutput;

public class ConsumerMethodActivityExecutor implements ActivityExecutor {

    private final Consumer<Writer> method;
    private Thread runningthread;
    private Writer stdoutwriter;

    public ConsumerMethodActivityExecutor(Consumer<Writer> method) {
        this.method = method;
    }

    @Override
    public DeviceDescriptor getDeviceDescriptor() {
        return null;
    }

    @Override
    public void open(ConnectionSTDIN stdin, ConnectionSTDOUT stdout,
            ConnectionSTDERR stderr, InputOutput io) throws Exception {
        stdoutwriter = stdout.getSTDOUTConnection(io);
    }

    @Override
    public void run() {
        runningthread = Thread.currentThread();
        method.accept(stdoutwriter);
    }

    //    No wait required as method runs on called thread to completion before returning
    @Override
    public void waitUntilCompletion() {
    }

    @Override
    public void close() {
        runningthread = null;
    }

    //    Cancel the running Task if cancel issued
    @Override
    public void cancel() {
        if (runningthread != null) {
            runningthread.interrupt();
        }
        runningthread = null;
    }
}
