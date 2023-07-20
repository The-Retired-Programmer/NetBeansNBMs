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
import java.io.Reader;
import java.io.Writer;
import org.netbeans.api.io.InputOutput;
import uk.theretiredprogrammer.util.ApplicationException;

public class ConnectionSTDERR extends OutputIO {

    private Reader reader;
    private boolean isCoreIOconfigured = false;

    public void set(Reader reader) {
        this.reader = reader;
        isCoreIOconfigured = true;
    }

    public DataTask createTask(String iotabname, InputOutput io) throws ApplicationException, IOException {
        if (isCoreIOconfigured && isIOConfigured()) {
            if (canProvideWriter()) {
                return new DataTaskByLine("stderr", reader, getWriter(io), iotabname);
            }
            throw new ApplicationException("Cannot create a STDERR DataTask - check it has been configured");
        }
        return new NullTask("stderr", reader, iotabname);
    }

    public Writer getSTDERRConnection(InputOutput io) throws ApplicationException, IOException {
        if (canProvideWriter()) {
            return getWriter(io);
        } else {
            throw new ApplicationException("Cannot find a STDERR connection - check it has been configured");
        }
    }
}
