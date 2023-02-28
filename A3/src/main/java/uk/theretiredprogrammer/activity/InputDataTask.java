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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Supplier;
import org.netbeans.api.io.InputOutput;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.activity.Activity.InputDataTransferStyle;
import static uk.theretiredprogrammer.activity.Activity.InputDataTransferStyle.BUFFERED_STREAM_OR_READER;
import uk.theretiredprogrammer.util.UserReporting;
import static uk.theretiredprogrammer.activity.DataTask.NEWLINE;
import uk.theretiredprogrammer.util.ApplicationException;

public class InputDataTask extends DataTask {

    private Reader rdr;
    private BufferedReader brdr;
    private InputStream in;
    private OutputStream out;
    private Writer wtr;
    private Runnable closeio;

    public InputDataTask(InputIO inputio, String iotabname, InputOutput io,
            InputConnection connection) throws ApplicationException, FileNotFoundException {
        super(inputio.name, iotabname);
        if (inputio.isDefined()) {
            InputDataTransferStyle transferstyle = inputio.isIO()
                    ? connection.getInputDataTransferStyle()
                    : BUFFERED_STREAM_OR_READER;
            switch (transferstyle) {
                case CHAR_READER:
                    byCharReader(inputio.getBufferReader(io, iotabname), connection.outputStreamSupplier());
                    break;
                case BUFFERED_READER:
                    byLineReader(inputio.getBufferReader(io, iotabname), connection.writerSupplier());
                    break;
                case BUFFERED_STREAM_OR_READER:
                try {
                    byStream(inputio.getInputStream(iotabname), connection.outputStreamSupplier());
                } catch (ApplicationException | FileNotFoundException ex) {
                    byLineReader(inputio.getBufferReader(io, iotabname), connection.writerSupplier());
                }
                break;
            }
        }
    }

    private void byCharReader(Reader rdr, Supplier<OutputStream> out) {
        this.rdr = rdr;
        this.out = out.get();
        closeio = () -> closeByCharReader();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(rdr, this.out));
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

    private void byStream(InputStream in, Supplier<OutputStream> out) {
        this.out = out.get();
        this.in = in;
        closeio = () -> closeByStream();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(in, this.out));
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

    private void byLineReader(BufferedReader brdr, Supplier<Writer> wtr) {
        this.brdr = brdr;
        this.wtr = wtr.get();
        closeio = () -> closeByLineReader();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(brdr, this.wtr));
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
        if (closeio!=null) {
            closeio.run();
        }
    }
}
