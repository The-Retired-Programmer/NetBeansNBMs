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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import uk.theretiredprogrammer.activity.DeviceDescriptor;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.UserReporting;

public class SerialDeviceDescriptor extends DeviceDescriptor {

    private SerialPort serial;
    private final String devicename;
    private final int baudrate;
    private final String encoding;

    public SerialDeviceDescriptor(String devicename, int baudrate, String encoding) {
        this.devicename = devicename;
        this.baudrate = baudrate;
        this.encoding = encoding;
    }

    public SerialDeviceDescriptor(String devicename, int baudrate) {
        this.devicename = devicename;
        this.baudrate = baudrate;
        this.encoding = "ISO_8859_1";
    }

    public String getDeviceProperty(String name) throws ApplicationException {
        switch (name) {
            case "encoding":
                return encoding;
            default:
                return super.getDeviceProperty(name);
        }
    }

    public void open() throws IOException {
        serial = SerialPort.getCommPort(devicename);
        if (!serial.openPort()) {
            throw new IOException("Could not open the serial device for I/O");
        }
        if (!serial.setBaudRate(baudrate)) {
            throw new IOException("Could not set the baud rate for serial device");
        }
        serial.flushIOBuffers();
        serial.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
    }

    public void close() throws Exception {
        if (serial.isOpen()) {
            serial.closePort();
            UserReporting.infoLogOnly("closed serial (SerialDeviceDescriptor)");
        }
    }

    public void cancel() throws Exception {
        if (serial.isOpen()) {
            serial.closePort();
            UserReporting.infoLogOnly("cancelled serial (SerialDeviceDescriptor))");
        }
    }

    public OutputStream getSTDINStream() {
        return serial.getOutputStream();
    }

    public InputStream getSTDOUTStream() {
        return serial.getInputStream();
    }

}
