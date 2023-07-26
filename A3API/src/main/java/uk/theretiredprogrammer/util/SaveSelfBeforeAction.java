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
package uk.theretiredprogrammer.util;

import java.io.IOException;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.DataObject;

/**
 * A simple method to execute a SaveBeforeAction without the needs for
 * project properties to control the mode of operation.
 * 
 * See SaveBeforeAction for the fuller implementation
 * 
 */
public class SaveSelfBeforeAction {
  
    /**
     * Save the defined file if it has been modified within Netbeans.
     * 
     * @param dataobject the file to be saved
     */
    public static void saveIfModified(DataObject dataobject) {
        try {
            if (dataobject.isModified()) {
                SaveCookie cookie = dataobject.getLookup().lookup(SaveCookie.class);
                if (cookie != null) {
                    cookie.save();
                }
            }
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage("Unable to save a modified file prior to execution: ", ex);
        }
    }
}
