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
import uk.theretiredprogrammer.activity.Activity.OutputDataTransferStyle;
import static uk.theretiredprogrammer.activity.Activity.OutputDataTransferStyle.BUFFERED_STREAM_OR_WRITER;
import uk.theretiredprogrammer.util.UserReporting;
import static uk.theretiredprogrammer.activity.DataTask.NEWLINE;
import uk.theretiredprogrammer.util.ApplicationException;

public class OutputDataTask extends DataTask {

    private BufferedReader brdr;
    private InputStream in;
    private OutputStream out;
    private Writer wtr;
    private Runnable closeio;

    public OutputDataTask(OutputIO outputio, String iotabname, InputOutput io, OutputConnection connection) throws ApplicationException, FileNotFoundException {
        super(outputio.name, iotabname);
        if (outputio.isDefined()) {
            OutputDataTransferStyle transferstyle = outputio.isIO()
                    ? connection.getOutputDataTransferStyle()
                    : BUFFERED_STREAM_OR_WRITER;
            switch (transferstyle) {
                case CHAR_WRITER:
                    byCharWriter(outputio.getWriter(io, iotabname), connection.inputStreamSupplier());
                    break;
                case BUFFERED_WRITER:
                    byLineWriter(outputio.getWriter(io, iotabname), bufferedReaderSupplier(connection.readerSupplier()));
                    break;
                case BUFFERED_STREAM_OR_WRITER:
                try {
                    byStream(outputio.getOutputStream(iotabname), connection.inputStreamSupplier());
                } catch (ApplicationException ex) {
                    byLineWriter(outputio.getWriter(io, iotabname), bufferedReaderSupplier(connection.readerSupplier()));
                }
                break;
                default:
                    throw new ApplicationException("Unknown OutputDataTransferStyle");
            }
        }
    }

    private Supplier<BufferedReader> bufferedReaderSupplier(Supplier<Reader> readersupplier) {
        return () -> new BufferedReader(readersupplier.get());
    }

    private void byCharWriter(Writer wtr, Supplier<InputStream> insupplier) {
        this.in = insupplier.get();
        this.wtr = wtr;
        closeio = () -> closeByCharWriter();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(in, wtr));
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

    private void byStream(OutputStream out, Supplier<InputStream> insupplier) {
        this.in = insupplier.get();
        this.out = out;
        closeio = () -> closeByStream();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(in, out));
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

    private void byLineWriter(Writer wtr, Supplier<BufferedReader> brdrsupplier) {
        this.wtr = wtr;
        this.brdr = brdrsupplier.get();
        closeio = () -> closeByLineReader();
        RequestProcessor processor = new RequestProcessor(name);
        task = processor.post(() -> copy(brdr, wtr));
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
