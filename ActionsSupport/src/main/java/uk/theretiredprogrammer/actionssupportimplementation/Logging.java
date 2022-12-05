/*
 * Copyright 2022 Richard Linsdale.
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

import java.util.logging.Logger;
import org.netbeans.api.io.OutputWriter;

public class Logging {
    
    private OutputWriter ioerr = null;
    private static final Logger logger = Logger.getLogger("uk.theretiredprogrammer.actionssupport");
    
    public void setErrorReporter(OutputWriter ioerr) {
        this.ioerr = ioerr;
    }
    
    public void user(String line) {
        if (ioerr != null) {
            ioerr.println(line);
        } 
        info(line);
    }
    
    public void info(String line) {
        logger.info(line);
    }

    public void warning(String line) {
        logger.warning(line);
    }
    
    public void severe(String line) {
        logger.severe(line);
    }
}
