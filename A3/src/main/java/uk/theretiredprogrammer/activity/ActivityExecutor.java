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

import org.netbeans.api.io.InputOutput;

public interface ActivityExecutor {
    
    
    public static enum DataTransferStyle {
        STREAM_TRANSFER, STREAM_OR_CHARACTER_TRANSFER,
        CHARACTER_TRANSFER_BY_CHAR, CHARACTER_TRANSFER_BY_LINE
    }

    public void open(ConnectionSTDIN stdin, ConnectionSTDOUT stdout, ConnectionSTDERR stderr, InputOutput io) throws Exception;
    
    public DeviceDescriptor getDeviceDescriptor();

    public void run() throws Exception;

    public void waitUntilCompletion() throws Exception;

    public void close() throws Exception;

    public void cancel() throws Exception;
}
