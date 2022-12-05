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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.function.Supplier;

public abstract class ProcessIO<S,RW> {
    
    Logging logging;
    
    public abstract void startTransfer(Supplier<S>streamSupplier, Supplier<RW>rwSupplier);
    
    public abstract void waitFinished(long timeout) throws InterruptedException;
    
    public abstract void close(Process process);
    
    // common methods
    
    public ProcessIO(Logging logging){
        this.logging = logging;
    }
    
    public ProcessIO(ProcessIO source) {
        this.logging = source.logging;
    }
    
    private static final String NEWLINE = System.getProperty("line.separator");
    
    public void streamTransfer(InputStream instream, OutputStream outstream) throws IOException {
        try (outstream) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = instream.read(buf)) != -1) {
                outstream.write(buf, 0, n);
            }
        }
    }

    public void readerTransfer(BufferedReader rdr, Writer writer) throws IOException {
        try (writer) {
            String line;
            while ((line = rdr.readLine()) != null) {
                writer.write(line);
                writer.write(NEWLINE);
            }
        }
    }
}
