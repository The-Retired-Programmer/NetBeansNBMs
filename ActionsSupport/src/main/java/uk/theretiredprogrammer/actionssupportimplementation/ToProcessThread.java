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
import java.io.Reader;
import java.io.Writer;
import uk.theretiredprogrammer.actionssupport.CLIExec2;

public class ToProcessThread extends CopyThread {

    private final Reader source;
    private final Writer target;

    public ToProcessThread(String name, Reader source, Writer target, CLIExec2 parent, int millisecs2flush) {
        super(name, parent);
        this.source = source;
        this.target = target;
        this.millisecs2flush = millisecs2flush;
    }

    @Override
    protected synchronized boolean copyAvailableItems() throws IOException {
        boolean hascopied = false;
        while (source.ready()) {
            target.write((char) source.read());
            hascopied = true;
        }
        return hascopied;
    }

    @Override
    public synchronized void close_target() throws IOException {
        target.close();
        transferdone = true;
    }

    @Override
    public synchronized void flush_target() throws IOException {
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
        target.write(line);
        target.write('\n');
        target.flush();
    }
}
