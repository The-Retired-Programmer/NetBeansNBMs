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
package uk.theretiredprogrammer.actionssupportimplementation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.function.Supplier;
import org.netbeans.api.io.OutputWriter;
import org.openide.util.RequestProcessor.Task;

public class STDERR extends TransferOUT {

    public STDERR() {
        super("STDERR");
    }

    public STDERR(STDERR source) {
        super(source);
    }

    @Override
    public void setOutputWriter(OutputWriter outputwriter) {
        super.setOutputWriter(outputwriter);
    }
    
    
    @Override
    public Task startTransfer(Supplier<InputStream> streamSupplier, Supplier<BufferedReader> rwSupplier, String iotabname, OutputWriter err) {
        super.transferOut(streamSupplier, rwSupplier, iotabname, err);
        return null;
    }

    @Override
    public void waitFinished(long timeout) throws InterruptedException {
        // null action as no task created
    }

}
