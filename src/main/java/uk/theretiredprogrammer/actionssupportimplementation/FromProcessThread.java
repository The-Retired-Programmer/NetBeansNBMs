/*
 * Copyright 2022 Richard Linsdale.
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
package uk.theretiredprogrammer.actionssupportimplementation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.function.BiConsumer;

public class FromProcessThread extends Thread {

    private final BufferedReader fromprocess;
    private final Writer target;
    private final BiConsumer<String, String> errorreporter;

    public FromProcessThread(String name, BufferedReader fromprocess, Writer target, BiConsumer<String, String> errorreporter) {
        super(name);
        this.target = target;
        this.fromprocess = fromprocess;
        this.errorreporter = errorreporter;
    }

    // parameters to flush if no further data available; is 100ms
    private static final int INIT_COUNTDOWN = 20;
    private static final int MS_DELAY_PER_LOOP = 5;

    @Override
    @SuppressWarnings({"SleepWhileInLoop", "ConvertToTryWithResources"})
    public void run() {
        int countdown = INIT_COUNTDOWN;
        while (true) {
            try {
                while (fromprocess.ready()) {
                    countdown = INIT_COUNTDOWN;
                    int c = fromprocess.read();
                    if (c == -1) {
                        target.flush();
                        return;
                    }
                    target.write(Character.toChars(c));
                }
                if (isEndRequired()) {
                    target.flush();
                    return;
                }
                if (countdown > 0) {
                    if (--countdown == 0) {
                        target.flush();
                    }
                }
                sleep(MS_DELAY_PER_LOOP);
            } catch (IOException ex) {
                errorreporter.accept(getName() + " copying", ex.getLocalizedMessage());
            } catch (InterruptedException ex) {
                errorreporter.accept(getName() + " interrupted during copying", ex.getLocalizedMessage());
            }
        }
    }

    private boolean endRequired = false;

    public synchronized void end() {
        endRequired = true;
    }

    private synchronized boolean isEndRequired() {
        if (endRequired) {
            endRequired = false;
            return true;
        }
        return false;
    }
}
