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

import java.io.IOException;
import uk.theretiredprogrammer.actionssupport.CLIExec;

public abstract class CopyThread extends Thread {
    
    // parameters to flush if no further data available
    private static final int MS_DELAY_PER_LOOP = 5;
    public static final int DEFAULT_MILLISECS2FLUSH = 100;
    public static final int NO_MILLISECS2FLUSH = 0;
    int millisecs2flush = NO_MILLISECS2FLUSH;
    boolean transferdone;
    CLIExec parent;
    
    public CopyThread(String name, CLIExec parent) {
        this(name, parent, NO_MILLISECS2FLUSH);
        this.parent = parent;
    }
    
    public CopyThread(String name, CLIExec parent, int millisecs2flush) {
        super(name);
        this.parent = parent;
        this.millisecs2flush = millisecs2flush;
    } 

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        int init_countdown = millisecs2flush/MS_DELAY_PER_LOOP;
        int countdown = init_countdown;
        transferdone = false;
        while (!istransferdone()) {
            try {
                if (copyAvailableItems()) {
                    countdown = init_countdown;
                }
                if (countdown > 0) {
                    if (--countdown == 0) {
                        flush_target();
                    }
                }
                sleep(MS_DELAY_PER_LOOP);
            } catch (IOException ex) {
                parent.printerror(getName() + " copying", ex.getLocalizedMessage());
            } catch (InterruptedException ex) {
                parent.printerror(getName() + " interrupted during copying", ex.getLocalizedMessage());
            }
        }
    }

    private synchronized boolean istransferdone() {
        return transferdone;
    }
    
    public abstract void println(String line) throws IOException;
    
    abstract boolean copyAvailableItems() throws IOException;
    
    public abstract void close_target() throws IOException;
    
    public abstract void flush_target() throws IOException;
}
