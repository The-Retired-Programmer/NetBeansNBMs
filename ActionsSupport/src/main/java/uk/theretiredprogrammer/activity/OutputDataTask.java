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
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.actionssupport.UserReporting;
import static uk.theretiredprogrammer.activity.DataTask.NEWLINE;

public class OutputDataTask extends DataTask {

    private BufferedReader brdr;
    private InputStream in;
    private OutputStream out;
    private Writer wtr;
    private Runnable closeio;

    public OutputDataTask(String name, String iotabname) {
        super(name, iotabname);
    }

    public OutputDataTask byCharWriter(OutputIO io, InputStream in) {
        this.in = in;
        wtr = io.getWriter(iotabname);
        if (wtr == null) {
            UserReporting.warning(iotabname, name + " does not have a suitable Writer defined");
            return null;
        }
        closeio = () -> closeByCharWriter();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(in, wtr));
        return this;
    }

    public OutputDataTask byCharWriter(Writer wtr, InputStream in) {
        this.in = in;
        this.wtr = wtr;
        closeio = () -> closeByCharWriter();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(in, wtr));
        return this;
    }

    private void copy(InputStream input, Writer out) {
        try (input; out) {
            int chr;
            while ((chr = input.read()) != -1) {
                out.write(chr);
            }
        } catch (IOException ex) {
            UserReporting.error(iotabname, "While copying " + name + " \"byCharWriterFromInputStream\" - " + ex);
        }
    }

    private void closeByCharWriter() {
        try {
            if (in != null) {
                in.close();
            }
            if (wtr != null) {
                wtr.close();
            }
        } catch (IOException ex) {
            UserReporting.warning(iotabname, "Closing " + name + " \"byCharWriter\" " + ex);
        }
    }

    public OutputDataTask byStream(OutputIO io, InputStream in) {
        this.in = in;
        out = io.getOutputStream(iotabname);
        if (out == null) {
            UserReporting.warning(iotabname, name + " does not have a suitable Stream defined");
            return null;
        }
        closeio = () -> closeByStream();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(in, out));
        return this;
    }

    public OutputDataTask byStream(OutputStream out, InputStream in) {
        this.in = in;
        this.out = out;
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

    public OutputDataTask byLineWriter(OutputIO io, Reader rdr) {
        wtr = io.getWriter(iotabname);
        brdr = new BufferedReader(rdr);
        if (wtr == null) {
            UserReporting.warning(iotabname, name + " does not have a suitable Writer defined");
            return null;
        }
        closeio = () -> closeByLineReader();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(brdr, wtr));
        return this;
    }

    public OutputDataTask byLineWriter(Writer wtr, BufferedReader brdr) {
        this.wtr = wtr;
        this.brdr = brdr;
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
