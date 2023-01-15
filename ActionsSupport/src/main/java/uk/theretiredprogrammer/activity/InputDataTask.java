/*
 * Copyright 2022-23 Richard Linsdale
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import org.netbeans.api.io.InputOutput;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.actionssupport.UserReporting;
import static uk.theretiredprogrammer.activity.DataTask.NEWLINE;

public class InputDataTask extends DataTask {

    private Reader rdr;
    private BufferedReader brdr;
    private InputStream in;
    private OutputStream out;
    private Writer wtr;
    private Runnable closeio;

    public InputDataTask(String name, String iotabname) {
        super(name, iotabname);
    }

    public InputDataTask byCharReader(InputOutput io, InputIO inIO, OutputStream out) {
        this.out = out;
        rdr = inIO.getReader(io, iotabname);
        if (rdr == null) {
            UserReporting.warning(iotabname, name + " does not have a suitable Reader defined");
            return null;
        }
        closeio = () -> closeByCharReader();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(rdr, out));
        return this;
    }

    public InputDataTask byCharReader(Reader rdr, OutputStream out) {
        this.rdr = rdr;
        this.out = out;
        closeio = () -> closeByCharReader();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(rdr, out));
        return this;
    }

    private void copy(Reader input, OutputStream out) {
        try (input; out) {
            int chr;
            while ((chr = input.read()) != -1) {
                out.write(chr);
            }
        } catch (IOException ex) {
            UserReporting.error(iotabname, "While copying " + name + " \"byCharReaderToOutputStream\" - " + ex);
        }
    }

    private void closeByCharReader() {
        try {
            if (out != null) {
                out.close();
            }
            if (rdr != null) {
                rdr.close();
            }
        } catch (IOException ex) {
            UserReporting.warning(iotabname, "Closing " + name + " \"byCharReader\" " + ex);
        }
    }

    public InputDataTask byStream(InputIO inIO, OutputStream out) {
        this.out = out;
        in = inIO.getInputStream(iotabname);
        if (in == null) {
            UserReporting.warning(iotabname, name + "does not have a suitable Stream defined");
            return null;
        }
        closeio = () -> closeByStream();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(in, out));
        return this;
    }

    public InputDataTask byStream(InputStream in, OutputStream out) {
        this.out = out;
        this.in = in;
        closeio = () -> closeByStream();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(in, out));
        return this;
    }

    private void copy(InputStream in, OutputStream out) {
        try (in; out) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        } catch (IOException ex) {
            UserReporting.error(iotabname, "While copying " + name + " \"byStream\" - " + ex);
        }
    }

    private void closeByStream() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException ex) {
            UserReporting.warning(iotabname, "Closing " + name + " \"byStream\" " + ex);
        }
    }

    public InputDataTask byLineReader(InputOutput io, InputIO inIO, Writer wtr) {
        this.wtr = wtr;
        brdr = inIO.getBufferReader(io, iotabname);
        if (brdr == null) {
            UserReporting.warning(iotabname, name + " does not have a suitable BufferedWriter defined");
            return null;
        }
        closeio = () -> closeByLineReader();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(brdr, wtr));
        return this;
    }

    public InputDataTask byLineReader(BufferedReader brdr, Writer wtr) {
        this.brdr = brdr;
        this.wtr = wtr;
        closeio = () -> closeByLineReader();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(brdr, wtr));
        return this;
    }

    private void copy(BufferedReader in, Writer out) {
        try (in; out) {
            String line;
            while ((line = in.readLine()) != null) {
                out.write(line);
                out.write(NEWLINE);
            }
        } catch (IOException ex) {
            UserReporting.error(iotabname, "While copying " + name + " \"byLineReader\" - " + ex);
        }
    }

    private void closeByLineReader() {
        try {
            if (wtr != null) {
                wtr.close();
            }
            if (brdr != null) {
                brdr.close();
            }
        } catch (IOException ex) {
            UserReporting.warning(iotabname, "Closing " + name + " \"byLineReader\" " + ex);
        }
    }

    @Override
    public void close() {
        closeio.run();
    }
}
