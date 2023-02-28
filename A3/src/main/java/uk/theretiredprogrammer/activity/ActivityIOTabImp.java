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

import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;

public class ActivityIOTabImp implements ActivityIOTab {
    
    private final String iotabname;
    
    public ActivityIOTabImp(String iotabname) {
        this.iotabname = iotabname;
    }
    
    public void clear() {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.reset();
    }
    
    public void println(String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getOut().println(message);
    }
    
    public void printdone() {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getOut().println("... done");
    }
    
}
