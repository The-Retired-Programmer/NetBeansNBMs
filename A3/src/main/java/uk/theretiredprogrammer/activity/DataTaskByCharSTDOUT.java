/*
 * Copyright 2022-23 Richard Linsdale
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.util.UserReporting;

public class DataTaskByCharSTDOUT implements DataTask {

    private final Writer output;
    private final InputStream input;
    private final String iotabname;
    private final String name;
    private final RequestProcessor.Task task;
    private final String encoding;

    public DataTaskByCharSTDOUT(String name, String encoding, InputStream input, Writer output, String iotabname) {
        this.iotabname = iotabname;
        this.name = name.toUpperCase();
        this.output = output;
        this.input = input;
        this.encoding = encoding;
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(input, output));
    }

    private void copy(InputStream input, Writer output) {
        try (input; output) {
            byte[] buffer = new byte[1];
            while (input.read(buffer) != -1) {
                output.write(new String(buffer, encoding));
            }
        } catch (IOException ex) {
            UserReporting.error(iotabname, "While copying " + name + " using CHARACTER_TRANSFER_BY_CHAR - " + ex);
        }
    }

    public void close() {
        try {
            if (!task.waitFinished(1000)) {
                task.cancel();
            }
        } catch (InterruptedException ex) {
            UserReporting.warning(iotabname, "While waiting to close " + name + " using CHARACTER_TRANSFER_BY_CHAR - " + ex);
        }
        closeIO();
    }

    public void cancel() {
        task.cancel();
    }

    private void closeIO() {
        try {
            output.close();
            input.close();
        } catch (IOException ex) {
            UserReporting.warning(iotabname, "While closing " + name + " using CHARACTER_TRANSFER_BY_CHAR - " + ex);
        }
    }
}
