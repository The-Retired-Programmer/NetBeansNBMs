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
import static uk.theretiredprogrammer.activity.ActivityExecutor.DataTransferStyle.CHARACTER_TRANSFER_BY_CHAR;

public class DeviceActivityExecutor implements ActivityExecutor {

    private final DeviceDescriptor dd;

    public DeviceActivityExecutor(DeviceDescriptor dd) {
        this.dd = dd;
    }

    @Override
    public DeviceDescriptor getDeviceDescriptor() {
        return dd;
    }

    @Override
    public void open(ConnectionSTDIN stdin, ConnectionSTDOUT stdout, ConnectionSTDERR stderr, InputOutput io) throws Exception {
        dd.open();
        String encoding = dd.getDeviceProperty("encoding");
        stdin.set(CHARACTER_TRANSFER_BY_CHAR, () -> dd.getSTDINStream(), encoding);
        stdout.set(CHARACTER_TRANSFER_BY_CHAR, () -> dd.getSTDOUTStream(), encoding);
    }

    @Override
    public void run() throws Exception {
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void waitUntilCompletion() {
        try {
            while (true) { // wait until cancelled
                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
        }
    }

    @Override
    public void close() throws Exception {
        dd.close();
    }

    @Override
    public void cancel() throws Exception {
        dd.cancel();
    }
}
