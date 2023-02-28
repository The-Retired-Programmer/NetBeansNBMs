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
import java.io.OutputStream;
import java.io.Writer;
import java.util.function.Supplier;
import org.netbeans.api.io.InputOutput;
import uk.theretiredprogrammer.activity.Activity.InputDataTransferStyle;
import uk.theretiredprogrammer.util.ApplicationException;

public class InputConnection {

    private final String name;
    private final Supplier<OutputStream> streamsupplier;
    private final Supplier<Writer> writersupplier;
    private final InputDataTransferStyle datatransferstyle;
    private final InputIO inputio;
    private InputDataTask inputdatatask;

    public InputConnection(String name, InputDataTransferStyle datatransferstyle, Supplier<OutputStream> streamsupplier, Supplier<Writer> writersupplier) {
        this.name = name;
        this.datatransferstyle = datatransferstyle;
        this.streamsupplier = streamsupplier;
        this.writersupplier = writersupplier;
        this.inputio = new InputIO(name);
    }

    public InputIO getInputIO() {
        return inputio;
    }

    public Supplier<OutputStream> outputStreamSupplier() {
        return streamsupplier;
    }

    public Supplier<Writer> writerSupplier() {
        return writersupplier;
    }

    public InputDataTransferStyle getInputDataTransferStyle() {
        return datatransferstyle;
    }

    public InputDataTask startInputDataTask(String iotabname, InputOutput io) throws ApplicationException, FileNotFoundException {
        this.inputdatatask = new InputDataTask(inputio, iotabname, io, this);
        return this.inputdatatask;
    }
}
