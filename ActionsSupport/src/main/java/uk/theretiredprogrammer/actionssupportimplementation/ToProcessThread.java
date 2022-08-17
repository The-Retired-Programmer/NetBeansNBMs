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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.function.BiConsumer;

public class ToProcessThread extends Thread {

    private final Reader source;
    private final BufferedWriter toprocess;
    private final BiConsumer<String, String> errorreporter;

    public ToProcessThread(String name, Reader source, BufferedWriter toprocess, BiConsumer<String, String> errorreporter) {
        super(name);
        this.source = source;
        this.toprocess = toprocess;
        this.errorreporter = errorreporter;
    }

    // parameters to flush if no further data available; is 100ms
    private static final int INIT_COUNTDOWN = 20;
    private static final int MS_DELAY_PER_LOOP = 5;

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        int countdown = INIT_COUNTDOWN;
        while (true) {
            try {
                while (source.ready()) {
                    countdown = INIT_COUNTDOWN;
                    toprocess.write((char) source.read());
                }
                if (isCloseRequired()) {
                    toprocess.close();
                    return;
                }
                if (isEndRequired()) {
                    return;
                }
                if (countdown > 0) {
                    if (--countdown == 0) {
                        toprocess.flush();
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

    private boolean closeRequired = false;
    private boolean endRequired = false;

    public synchronized void close() {
        closeRequired = true;
    }

    public synchronized void end() {
        endRequired = true;
    }

    private synchronized boolean isCloseRequired() {
        if (closeRequired) {
            closeRequired = false;
            return true;
        }
        return false;
    }

    private synchronized boolean isEndRequired() {
        if (endRequired) {
            endRequired = false;
            return true;
        }
        return false;
    }
}
