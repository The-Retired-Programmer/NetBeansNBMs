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
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.activity.ActivityIO;
import uk.theretiredprogrammer.actionssupport.UserReporting;
import uk.theretiredprogrammer.activity.DataTask;
import uk.theretiredprogrammer.activity.InputDataTask;
import uk.theretiredprogrammer.activity.OutputDataTask;

public class SerialActivity extends Activity {

    private SerialPort serialport;
    private final String devicename;
    private final int baudrate;

    public SerialActivity(String devicename, int baudrate, ActivityIO activityio) {
        super(activityio);
        this.devicename = devicename;
        this.baudrate = baudrate;
    }

    @Override
    public boolean onStart() {
        serialport = initialiseSerial(devicename, baudrate);
        return serialport != null;
    }

    @Override
    public DataTask[] createAllDataTasks() {
        return new DataTask[]{
            new InputDataTask(activityio.inputs[0].name, activityio.iotab.name).byCharReader(io, activityio.inputs[0], serialport.getOutputStream()),
            new OutputDataTask(activityio.outputs[0].name, activityio.iotab.name).byCharWriter(io, activityio.outputs[0], serialport.getInputStream())
        };
    }

    @Override
    public boolean areClosingActionsRequired() {
        return false;
    }

    @Override
    public void onCancel() {
        if (serialport.isOpen()) {
            serialport.closePort();
            UserReporting.infoLogOnly("closed serialport (SerialTerminal)");
        }
    }

    private SerialPort initialiseSerial(String devicename, int baudrate) {
        SerialPort serial = SerialPort.getCommPort(devicename);
        if (!serial.openPort()) {
            UserReporting.error(activityio.iotab.name, "Could not open the serial device for I/O");
            return null;
        }
        if (!serial.setBaudRate(baudrate)) {
            UserReporting.error(activityio.iotab.name, "Could not set the baud rate for serial device");
            return null;
        }
        serial.flushIOBuffers();
        serial.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        return serial;
    }
}
