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
import java.io.Reader;
import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;
import org.netbeans.api.io.OutputWriter;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import uk.theretiredprogrammer.actionssupport.IOTabCloseWatch;
import uk.theretiredprogrammer.actionssupport.UserReporting;

public class SerialTerminal {

    private Task fromserialtask;
    private Task toserialtask;
    SerialPort serialport;

    public void open(String iotabname, String serialdevicename, int baudrate) {
        serialport = initialiseSerial(serialdevicename, baudrate, iotabname);
        if (serialport != null) {
            InputOutput iotab = initialiseIOTab(iotabname);
            copyfromSerial(serialport, iotab, iotabname);
            copytoSerial(iotab, serialport, iotabname);
            IOTabCloseWatch.watch(iotab, () -> runWhenIoTabClosed());
        }
    }

    private void runWhenIoTabClosed() {
        UserReporting.infoLogOnly("called from IOTabClosedWatch");
        if (!fromserialtask.isFinished()) {
            fromserialtask.cancel();
            UserReporting.infoLogOnly("cancelled fromserialtask (SerialTerminal)");
        }
        if (!toserialtask.isFinished()) {
            toserialtask.cancel();
            UserReporting.infoLogOnly("cancelled toserialtask (SerialTerminal)");
        }
        if (serialport.isOpen()) {
            serialport.closePort();
            UserReporting.infoLogOnly("closed serialport (SerialTerminal)");
        }
    }

    private SerialPort initialiseSerial(String serialdevicename, int baudrate, String iotabname) {
        SerialPort serial = SerialPort.getCommPort(serialdevicename);
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

    private InputOutput initialiseIOTab(String iotabname) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        return io;
    }

    private void copyfromSerial(SerialPort serial, InputOutput iotab, String iotabname) {
        RequestProcessor processor = new RequestProcessor("serialreader");
        fromserialtask = processor.post(() -> copy(serial, iotab, iotabname));
    }

    private void copytoSerial(InputOutput iotab, SerialPort serial, String iotabname) {
        RequestProcessor processor = new RequestProcessor("serialwriter");
        toserialtask = processor.post(() -> copy(iotab, serial, iotabname));
    }

    private void copy(SerialPort serial, InputOutput iotab, String iotabname) {
        try ( OutputWriter totab = iotab.getOut();  InputStream in = serial.getInputStream()) {
            int chr;
            while ((chr = in.read()) != -1) {
                totab.print((char) chr);
            }
        } catch (IOException ex) {
            UserReporting.error(iotabname, "While reading from the Serial Device - " + ex);
        }
    }

    public void copy(InputOutput iotab, SerialPort serial, String iotabname) {
        try ( Reader fromtab = iotab.getIn();  OutputStream out = serial.getOutputStream()) {
            int chr;
            while ((chr = fromtab.read()) != -1) {
                out.write(chr);
            }
        } catch (IOException ex) {
            UserReporting.error(iotabname, "While writing to the Serial Device - " + ex);
        }
    }
}
