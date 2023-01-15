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
import uk.theretiredprogrammer.activity.Activity;

/**
 * RunWithIOTab creates an IOTab in order to execute an Activity.
 *
 */
public class RunActivityWithIOTab {

    private final InputOutput io;
    private final String iotabname;
    private final Activity activity;

    /**
     * Constructor
     *
     * @param activity the activity to be run
     */
    public RunActivityWithIOTab(Activity activity) {
        this.activity = activity;
        iotabname = activity.activityio.iotabname;
        io = IOProvider.getDefault().getIO(iotabname, false);
        activity.setIO(io);
        io.show();
        if (activity.activityio.iotabreset) {
            io.reset();
        }
    }

    /**
     * Start the Activity in the IOTab environment
     *
     * @param startmessage message which can be written to the IO Tab prior to
     * starting the worker and "... done" is written at the end))
     */
    public void process(String startmessage) {
        io.getOut().println(startmessage);
        process();
        io.getOut().println("... done");
    }

    /**
     * Start the Activity in the IOTab environment
     *
     */
    public void process() {
        activity.process();
    }
}
