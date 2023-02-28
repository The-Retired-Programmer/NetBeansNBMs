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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.util.function.Supplier;
import org.netbeans.api.io.InputOutput;
import uk.theretiredprogrammer.activity.Activity.OutputDataTransferStyle;
import uk.theretiredprogrammer.util.ApplicationException;

public class OutputConnection {

    private final String name;
    private final Supplier<InputStream> streamsupplier;
    private final Supplier<Reader> readersupplier;
    private final OutputDataTransferStyle datatransferstyle;
    private final OutputIO outputio;
    private OutputDataTask outputdatatask;

    public OutputConnection(String name, OutputDataTransferStyle datatransferstyle, Supplier<InputStream> streamsupplier, Supplier<Reader> readersupplier) {
        this.name = name;
        this.streamsupplier = streamsupplier;
        this.readersupplier = readersupplier;
        this.datatransferstyle = datatransferstyle;
        this.outputio = new OutputIO(name);
    }

    public OutputIO getOutputIO() {
        return outputio;
    }

    public Supplier<InputStream> inputStreamSupplier() {
        return streamsupplier;
    }

    public Supplier<Reader> readerSupplier() {
        return readersupplier;
    }

    public OutputDataTransferStyle getOutputDataTransferStyle() {
        return datatransferstyle;
    }

    public OutputDataTask startOutputDataTask(String iotabname, InputOutput io) throws ApplicationException, FileNotFoundException {
        this.outputdatatask = new OutputDataTask(outputio, iotabname, io, this);
        return this.outputdatatask;
    }
}
