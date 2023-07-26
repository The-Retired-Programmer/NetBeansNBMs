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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import uk.theretiredprogrammer.util.ApplicationException;

/**
 * A set of methods which describes key functions of the Device.
 */
public abstract class DeviceDescriptor {
    
    /**
     * Get a named property associated with device configuration.  Source of property values can
     * be device dependent.  Could be a properties file (project, node or device specific) or other device specific mechanism.
     * 
     * @param name of the property
     * @return value of the property
     * @throws ApplicationException if requested property cannot be found.
     */
    public String getDeviceProperty(String name) throws ApplicationException {
        throw new ApplicationException("Unknown Device Property requested - "+ name);
    }
    
    /**
     * Open the device.  Once open the device should be ready to accept and/or provide data.
     * 
     * @throws Exception if device cannot be opened
     */
    public void open() throws Exception {
    }
    
    /**
     * Close the device.  Once closed the device cannot accept or provide further data.
     * 
     * @throws Exception if device cannot be closed
     */
    public void close() throws Exception {
    }
    
    /**
     * Cancel the device. Once cancelled the device is in the same state as after close(),
     * however the cancel method will not make any effort to ensure completion of any data transfers and will not
     * ensure any data retention should the device offer data recording.
     * 
     * @throws Exception if device cannot be cancelled
     */
    public void cancel() throws Exception {
    }
    
    /**
     * Get the Device STDIN Writer. To pass unicode characters to the device.
     * 
     * @return the writer
     */
    public Writer getSTDIN() {
        return null;
    }
    
    /**
     * Get the Device STDOUT Reader. To get unicode characters from the device.
     * 
     * @return the Reader
     */
    public Reader getSTDOUT()  {
        return null;
    }
    
    /**
     * Get the Device STDIN Output Stream. To pass bytes to the device.
     * 
     * @return the OutputStream
     */
    public OutputStream getSTDINStream() {
        return null;
    }
    
    /**
     *  Get the Device STDOUT Reader. To get bytes from the device.
     * 
     * @return the InputStream
     */
    public InputStream getSTDOUTStream() {
        return null;
    }
}
