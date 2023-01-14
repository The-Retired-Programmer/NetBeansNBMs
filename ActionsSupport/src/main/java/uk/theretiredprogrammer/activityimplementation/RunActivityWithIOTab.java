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

import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;
import org.netbeans.api.io.OutputWriter;
import uk.theretiredprogrammer.activity.Activity;

/**
 * RunWithIOTab executes an AbstractRunnable instance involving an IoTab.
 *
 * Uses a NbCliDescriptor to define the IO configuration.
 */
public class RunActivityWithIOTab {

    private final InputOutput io;
    private final String iotabname;
    private final OutputWriter outwtr;
    private final Activity activity;

    /**
     * Constructor
     *
     * @param activity the activity to be run
     */
    public RunActivityWithIOTab(Activity activity) {
        this.activity = activity;
        iotabname = activity.activityio.iotab.name;
        io = IOProvider.getDefault().getIO(iotabname, false);
        activity.setIO(io);
        io.show();
        if (activity.activityio.iotab.reset) {
            io.reset();
        }
        outwtr = io.getOut();
        activity.activityio.stderr.setOutputWriter(io.getErr());
        activity.activityio.stdout.setOutputWriter(outwtr);
        activity.activityio.stdin.setReader(io.getIn());
    }

    /**
     * Start the Activity in the IOTab environment
     *
     * @param startmessage message which can be written to the IO Tab prior to
     * starting the worker and "... done" is written at the end))
     */
    public void process(String startmessage) {
        outwtr.println(startmessage);
        process();
        outwtr.println("... done");
    }

    /**
     * Start the Activity in the IOTab environment
     *
     */
    public void process() {
        activity.process();
    }
}
