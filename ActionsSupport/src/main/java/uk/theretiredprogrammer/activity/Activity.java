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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.activityimplementation.ExternalProcessActivity;
import uk.theretiredprogrammer.actionssupport.UserReporting;
import uk.theretiredprogrammer.activityimplementation.IOTabCloseWatch;
import uk.theretiredprogrammer.activityimplementation.RunActivityWithIOTab;

public abstract class Activity {

    public static void runWithIOTab(Activity activity) {
        new RunActivityWithIOTab(activity).process();
    }

    public static void runWithIOTab(Activity activity, String message) {
        new RunActivityWithIOTab(activity).process(message);
    }

    public static void run(Activity activity) {
        activity.process();
    }

    //
    public static void runExternalProcessWithIOTab(String command, String args, FileObject dir, ActivityIO activityio) {
        new RunActivityWithIOTab(new ExternalProcessActivity(command, args, dir, activityio)).process();
    }

    public static void runExternalProcessWithIOTab(String command, String args, FileObject dir, ActivityIO activityio, String message) {
        new RunActivityWithIOTab(new ExternalProcessActivity(command, args, dir, activityio)).process(message);
    }

    public static void runExternalProcess(String command, String args, FileObject dir, ActivityIO activityio) {
        new ExternalProcessActivity(command, args, dir, activityio).process();
    }

    protected InputOutput io;
    public final ActivityIO activityio;

    public Activity(ActivityIO activityio) {
        this.activityio = activityio;
    }

    public void setIO(InputOutput io) {
        this.io = io;
    }

    // the following methods can be overwridden to enable functionality and add
    // life cycle actions
    public boolean onStart() {
        return true;
    }

    public Process createProcess() {
        return null;
    }

    public InputDataTask[] createAllInputDataTasks() {
        return new InputDataTask[0];
    }
    
    public OutputDataTask[] createAllOutputDataTasks() {
        return new OutputDataTask[0];
    }

    public void onActivity() {
    }

    public boolean areClosingActionsRequired() {
        return true;
    }

    public void onEnd() {
    }

    public void onCancel() {
    }

    private Process process;
    private InputDataTask[] allInputDataTasks;
    private OutputDataTask[] allOutputDataTasks;
    private boolean cancelled;

    @SuppressWarnings("SleepWhileInLoop")
    public void process() {
        try {
            process = null;
            cancelled = false;
            if (!onStart()) {
                return;
            }
            process = createProcess();
            allInputDataTasks = createAllInputDataTasks();
            allOutputDataTasks = createAllOutputDataTasks();
            if (io != null) {
                IOTabCloseWatch.watch(activityio.iotab.name, io, () -> cancelTasksAndProcess());
            }
            onActivity();
            if (areClosingActionsRequired()) {
                if (process != null) {
                    process.waitFor();
                }
                for (var ioitem : allOutputDataTasks) {
                    if (ioitem != null) {
                        RequestProcessor.Task task = ioitem.getTask();
                        if (task != null) {
                            task.waitFinished(10000);
                        }
                    }
                }
                for (var ioitem : allInputDataTasks) {
                    if (ioitem != null) {
                        RequestProcessor.Task task = ioitem.getTask();
                        if (task != null) {
                            task.waitFinished(10000);
                        }
                    }
                }
                for (var ioitem : allOutputDataTasks) {
                    if (ioitem != null) {
                        ioitem.close();
                    }
                }
                for (var ioitem : allInputDataTasks) {
                    if (ioitem != null) {
                        ioitem.close();
                    }
                }
                onEnd();
            } else {
                while (!cancelled) {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException ex) {
            UserReporting.exception(activityio.iotab.name, ex);
        }
    }

    private void cancelTasksAndProcess() {
        for (var ioitem : allOutputDataTasks) {
            if (ioitem != null) {
                if (ioitem.getTask() != null && !ioitem.getTask().isFinished()) {
                    ioitem.getTask().cancel();
                }
            }
        }
        for (var ioitem : allInputDataTasks) {
            if (ioitem != null) {
                if (ioitem.getTask() != null && !ioitem.getTask().isFinished()) {
                    ioitem.getTask().cancel();
                }
            }
        }
        if (process != null) {
            process.destroy();
        }
        cancelled = true;
        onCancel();
    }

    //
    public static String substituteNODEPATH(String source, FileObject node) {
        return source.replace("${NODEPATH}", FileUtil.toFile(node).getAbsolutePath());
    }
}
