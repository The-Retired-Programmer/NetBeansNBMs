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
import java.io.InputStream;
import java.io.OutputStream;
import uk.theretiredprogrammer.actionssupport.CLIExec2;

public class CopyStreamThread extends CopyThread {

    private final InputStream source;
    private final OutputStream target;

    public CopyStreamThread(String name, InputStream source, OutputStream target, CLIExec2 parent, int millisecs2flush) {
        super(name, parent, millisecs2flush);
        this.target = target;
        this.source = source;
    }

    @Override
    synchronized boolean copyAvailableItems() throws IOException {
        try {
            int c = source.read();
            if (c == -1) {
                target.close();
                transferdone = true;
                return true;
            }
            target.write(c);
        } catch (IOException ex) {
            parent.printerror(getName() + " copying", ex.getLocalizedMessage());
        }
        return true;
    }

    @Override
    public synchronized void close_target() throws IOException {
    }

    @Override
    public synchronized void flush_target() {
        if (!transferdone) {
            try {
                target.flush();
            } catch (IOException ex) {
                parent.printerror(getName() + " flush failed", ex.getLocalizedMessage());
            }
        }
        transferdone = true;
    }

    @Override
    public synchronized void println(String line) throws IOException {
        target.write(line.getBytes("UTF-8"));
        target.write('\n');
        target.flush();
    }
}
