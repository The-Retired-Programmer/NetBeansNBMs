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
import java.io.OutputStream;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.util.UserReporting;

public class DataTaskByStream implements DataTask {

    private final OutputStream os;
    private final InputStream is;
    private final String iotabname;
    private final String name;
    private final RequestProcessor.Task task;

    public DataTaskByStream(String name, InputStream is, OutputStream os, String iotabname) {
        this.iotabname = iotabname;
        this.name = name.toUpperCase();
        this.os = os;
        this.is = is;
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(is, os));
    }

    private void copy(InputStream is, OutputStream os) {
        try (is; os) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) != -1) {
                os.write(buf, 0, n);
            }
        } catch (IOException ex) {
            UserReporting.error(iotabname, "While copying " + name + " using STREAM_TRANSFER - " + ex);
        }
    }

    public void close() {
        try {
            if (!task.waitFinished(1000)) {
                task.cancel();
            }
        } catch (InterruptedException ex) {
            UserReporting.warning(iotabname, "While waiting to close " + name + " using STREAM_TRANSFER - " + ex);
        }
        closeIO();
    }

    public void cancel() {
        task.cancel();
    }

    private void closeIO() {
        try {
            os.close();
            is.close();
        } catch (IOException ex) {
            UserReporting.warning(iotabname, "While closing " + name + " using STREAM_TRANSFER - " + ex);
        }
    }
}
