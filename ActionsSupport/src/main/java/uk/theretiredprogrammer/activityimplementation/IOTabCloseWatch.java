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
package uk.theretiredprogrammer.activityimplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.netbeans.api.io.InputOutput;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.actionssupport.UserReporting;

public class IOTabCloseWatch {

    private static final List<WatchParameters> iotabstowatch = new ArrayList<>();

    public static synchronized boolean watch(String iotabname, InputOutput io, Runnable runifclosed) {
        if (runifclosed == null || watchcontains(io)) {
            UserReporting.errorLogOnly("Problem in setting up an iotab watch - either the Runnable is null or the iotab is already being watched");
            return false;
        } else {
            iotabstowatch.add(new WatchParameters(io, runifclosed, iotabname));
            UserReporting.infoLogOnly("inserted iotabclosewatch for " + iotabname);
            return true;
        }
    }

    static {
        UserReporting.infoLogOnly("IOTabCloseWatch static initialisation");
        RequestProcessor processor = new RequestProcessor("iotabclosewatch");
        processor.scheduleAtFixedRate(() -> checkAndRemove(), 100, 100, TimeUnit.MILLISECONDS);
    }

    private static boolean watchcontains(InputOutput io) {
        for (int i = 0; i < iotabstowatch.size(); i++) {
            WatchParameters watch = iotabstowatch.get(i);
            if (watch.io.equals(io)) {
                return true;
            }
        }
        return false;
    }

    private static synchronized void checkAndRemove() {
        int index;
        while ((index = check()) != -1) {
            iotabstowatch.remove(index);
        }
    }

    private static int check() {
        for (int i = 0; i < iotabstowatch.size(); i++) {
            WatchParameters watch = iotabstowatch.get(i);
            if (watch.io.isClosed()) {
                watch.runifclosed.run();
                UserReporting.infoLogOnly("removed iotabclosewatch: "+watch.iotabname+"; index = " + Integer.toString(i));
                return i;
            }
        }
        return -1;
    }

    private static class WatchParameters {

        public final InputOutput io;
        public final Runnable runifclosed;
        public final String iotabname;

        public WatchParameters(InputOutput io, Runnable runifclosed, String iotabname) {
            this.io = io;
            this.runifclosed = runifclosed;
            this.iotabname = iotabname;
        }
    }
}
