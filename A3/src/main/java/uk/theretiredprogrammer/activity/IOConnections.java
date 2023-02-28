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

import java.util.HashMap;
import java.util.Map;


public class IOConnections {
    
    private final Map<String, OutputConnection> outputconnections = new HashMap<>();
    private final Map<String, InputConnection> inputconnections = new HashMap<>();
    
    public IOConnections addOutputConnection(String name, OutputConnection connection) {
        outputconnections.put(name, connection);
        return this;
    }
    
    public IOConnections addInputConnection(String name, InputConnection connection) {
        inputconnections.put(name, connection);
        return this;
    }
    
    
    
    
}
