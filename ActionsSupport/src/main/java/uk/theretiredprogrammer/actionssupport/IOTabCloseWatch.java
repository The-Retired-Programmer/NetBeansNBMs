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
package uk.theretiredprogrammer.actionssupport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.netbeans.api.io.InputOutput;
import org.openide.util.RequestProcessor;

public class IOTabCloseWatch {

    private static final Map<InputOutput, Runnable> iotabstowatch = new HashMap<>();

    public static synchronized boolean watch(InputOutput io, Runnable runifclosed) {
        if (runifclosed == null || iotabstowatch.containsKey(io)) {
            UserReporting.errorLogOnly("Problem in setting up an iotab watch - either the Runnable is null or the iotab is already being watched");
            return false;
        } else {
            iotabstowatch.put(io, runifclosed);
            UserReporting.infoLogOnly("inserted iotabclosewatch");
            return true;
        }
    }

    static {
        UserReporting.infoLogOnly("IOTabCloseWatch static initialisation");
        RequestProcessor processor = new RequestProcessor("iotabclosewatch");
        processor.scheduleAtFixedRate(() -> check(), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    private static synchronized void check() {
        for (var entry : iotabstowatch.entrySet()) {
            InputOutput io = entry.getKey();
            if (io.isClosed()) {
                entry.getValue().run();
                iotabstowatch.remove(io);
                UserReporting.infoLogOnly("removed iotabclosewatch");
            }
        }
    }
}
