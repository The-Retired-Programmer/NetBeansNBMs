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
import uk.theretiredprogrammer.actionssupport.CLIExec;

public class CopyToWriterThread extends CopyThread {

    private final Reader source;
    private final Writer target;

    public CopyToWriterThread(String name, Reader source, Writer target, CLIExec parent) {
        super(name, parent);
        this.target = target;
        this.source = source;
    }

    @Override
    synchronized boolean copyAvailableItems() throws IOException {
        boolean hascopied = false;
        while (source.ready()) {
            int c = source.read();
            if (c == -1) {
                target.flush();
                return hascopied;
            }
            target.write(Character.toChars(c));
            hascopied = true;
        }
        return hascopied;
    }

    @Override
    public synchronized void close_target() throws IOException {
        // not required
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
