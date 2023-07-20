/*
 * Copyright 2023 richard linsdale.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Supplier;
import org.netbeans.api.io.InputOutput;
import uk.theretiredprogrammer.activity.ActivityExecutor.DataTransferStyle;
import static uk.theretiredprogrammer.activity.ActivityExecutor.DataTransferStyle.CHARACTER_TRANSFER_BY_CHAR;
import static uk.theretiredprogrammer.activity.ActivityExecutor.DataTransferStyle.CHARACTER_TRANSFER_BY_LINE;
import static uk.theretiredprogrammer.activity.ActivityExecutor.DataTransferStyle.STREAM_OR_CHARACTER_TRANSFER;
import static uk.theretiredprogrammer.activity.ActivityExecutor.DataTransferStyle.STREAM_TRANSFER;
import uk.theretiredprogrammer.util.ApplicationException;

public class ConnectionSTDIN extends InputIO {

    private Supplier<OutputStream> streamsupplier;
    private Supplier<Writer> writersupplier;
    private DataTransferStyle datatransferstyle;
    private boolean isCoreIOconfigured = false;
    private String encoding = "US-ASCII";

    public void set(DataTransferStyle datatransferstyle, Supplier<OutputStream> streamsupplier, Supplier<Writer> writersupplier) {
        this.datatransferstyle = datatransferstyle;
        this.streamsupplier = streamsupplier;
        this.writersupplier = writersupplier;
        isCoreIOconfigured = true;
    }

    public void set(DataTransferStyle datatransferstyle, Supplier<OutputStream> streamsupplier) {
        this.datatransferstyle = datatransferstyle;
        this.streamsupplier = streamsupplier;
        this.writersupplier = null;
        isCoreIOconfigured = true;
    }

    public void set(DataTransferStyle datatransferstyle, Supplier<OutputStream> streamsupplier, String encoding) {
        this.datatransferstyle = datatransferstyle;
        this.streamsupplier = streamsupplier;
        this.writersupplier = null;
        isCoreIOconfigured = true;
        this.encoding = encoding;
    }

    public DataTask createTask(String iotabname, InputOutput io) throws ApplicationException, IOException {
        if (isCoreIOconfigured && isIOConfigured()) {
            if (datatransferstyle == CHARACTER_TRANSFER_BY_LINE && canProvideReader()) {
                return new DataTaskByLine("stdin", getReader(io), writersupplier.get(), iotabname);
            }
            if (datatransferstyle == CHARACTER_TRANSFER_BY_CHAR && canProvideReader()) {
                return new DataTaskByCharSTDIN("stdin", encoding, getReader(io), streamsupplier.get(), iotabname);
            }
            if (datatransferstyle == STREAM_TRANSFER && canProvideStream()) {
                return new DataTaskByStream("stdin", getInputStream(), streamsupplier.get(), iotabname);
            }
            if (datatransferstyle == STREAM_OR_CHARACTER_TRANSFER && canProvideStream()) {
                return new DataTaskByStream("stdin", getInputStream(), streamsupplier.get(), iotabname);
            }
            if (datatransferstyle == STREAM_OR_CHARACTER_TRANSFER && canProvideReader()) {
                return new DataTaskByLine("stdin", getReader(io), writersupplier.get(), iotabname);
            }
            throw new ApplicationException("Cannot create a STDIN DataTask - check it has been configured");
        }
        return new NullTask("stdin", iotabname);
    }

    public Reader getSTDINConnection(InputOutput io) throws ApplicationException, IOException {
        if (canProvideReader()) {
            return getReader(io);
        } else {
            throw new ApplicationException("Cannot find a STDIN connection - check it has been configured");
        }
    }

    public InputStream getSTDINStreamConnection() throws ApplicationException, IOException {
        if (canProvideStream()) {
            return getInputStream();
        } else {
            throw new ApplicationException("Cannot find a STDIN Stream connection - check it has been configured");
        }
    }
}
