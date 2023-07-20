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

public class ConnectionSTDOUT extends OutputIO {

    private Supplier<Reader> readersupplier;
    private Supplier<InputStream> streamsupplier;
    private DataTransferStyle datatransferstyle;
    private boolean isCoreIOconfigured = false;
    private String encoding = "US-ASCII";

    public void set(DataTransferStyle datatransferstyle, Supplier<InputStream> streamsupplier, Supplier<Reader> readersupplier) {
        this.readersupplier = readersupplier;
        this.streamsupplier = streamsupplier;
        this.datatransferstyle = datatransferstyle;
        isCoreIOconfigured = true;
    }

    public void set(DataTransferStyle datatransferstyle, Supplier<InputStream> streamsupplier) {
        this.readersupplier = null;
        this.streamsupplier = streamsupplier;
        this.datatransferstyle = datatransferstyle;
        isCoreIOconfigured = true;
    }

    public void set(DataTransferStyle datatransferstyle, Supplier<InputStream> streamsupplier, String encoding) {
        this.readersupplier = null;
        this.streamsupplier = streamsupplier;
        this.datatransferstyle = datatransferstyle;
        isCoreIOconfigured = true;
        this.encoding = encoding;
    }

    public DataTask createTask(String iotabname, InputOutput io) throws ApplicationException, IOException {
        if (isCoreIOconfigured && isIOConfigured()) {
            if (datatransferstyle == CHARACTER_TRANSFER_BY_LINE && canProvideWriter()) {
                return new DataTaskByLine("stdout", readersupplier.get(), getWriter(io), iotabname);
            }
            if (datatransferstyle == CHARACTER_TRANSFER_BY_CHAR && canProvideWriter()) {
                return new DataTaskByCharSTDOUT("stdout", encoding, streamsupplier.get(), getWriter(io), iotabname);
            }
            if (datatransferstyle == STREAM_TRANSFER && canProvideStream()) {
                return new DataTaskByStream("stdout", streamsupplier.get(), getOutputStream(), iotabname);
            }
            if (datatransferstyle == STREAM_OR_CHARACTER_TRANSFER && canProvideStream()) {
                return new DataTaskByStream("stdout", streamsupplier.get(), getOutputStream(), iotabname);
            }
            if (datatransferstyle == STREAM_OR_CHARACTER_TRANSFER && canProvideWriter()) {
                return new DataTaskByLine("stdout", readersupplier.get(), getWriter(io), iotabname);
            }
            throw new ApplicationException("Cannot create a STDOUT DataTask - check it has been configured");
        }
        return new NullTask("stdout", iotabname);
    }

    public Writer getSTDOUTConnection(InputOutput io) throws ApplicationException, IOException {
        if (canProvideWriter()) {
            return getWriter(io);
        } else {
            throw new ApplicationException("Cannot find a STDOUT connection - check it has been configured");
        }
    }

    public OutputStream getSTDOUTStreamConnection() throws ApplicationException, IOException {
        if (canProvideStream()) {
            return getOutputStream();
        } else {
            throw new ApplicationException("Cannot find a STDOUT Stream connection - check it has been configured");
        }
    }
}
