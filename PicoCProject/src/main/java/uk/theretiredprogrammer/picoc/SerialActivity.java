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
package uk.theretiredprogrammer.picoc;

import com.fazecast.jSerialComm.SerialPort;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;
import uk.theretiredprogrammer.util.UserReporting;

public class SerialActivity  implements Closeable {

    private SerialPort serialport;
    private final String devicename;
    private final int baudrate;
    private final String iotabname;

    public SerialActivity(String iotabname, String devicename, int baudrate) {
        this.iotabname = iotabname;
        this.devicename = devicename;
        this.baudrate = baudrate;
    }

    public boolean open() {
        serialport = initialiseSerial();
        return serialport != null;
    }
    
    public Supplier<OutputStream> getOutputStreamSupplier() {
        return () -> serialport.getOutputStream();
    }
    
    public Supplier<InputStream> getInputStreamSupplier() {
        return () -> serialport.getInputStream();
    }
    
    @Override
    public void close() {
        if (serialport.isOpen()) {
            serialport.closePort();
            UserReporting.infoLogOnly("closed serialport (SerialTerminal)");
        }
    }

    private SerialPort initialiseSerial() {
        SerialPort serial = SerialPort.getCommPort(devicename);
        if (!serial.openPort()) {
            UserReporting.error(iotabname, "Could not open the serial device for I/O");
            return null;
        }
        if (!serial.setBaudRate(baudrate)) {
            UserReporting.error(iotabname, "Could not set the baud rate for serial device");
            return null;
        }
        serial.flushIOBuffers();
        serial.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        return serial;
    }
}
