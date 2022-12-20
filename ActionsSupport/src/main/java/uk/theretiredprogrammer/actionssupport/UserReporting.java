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
package uk.theretiredprogrammer.actionssupport;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;
import org.netbeans.api.io.OutputWriter;

public class UserReporting {

    public static void startmessage(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getOut().println(message);
    }

    public static void completedmessage(String iotabname) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getOut().println("... done");
    }

    public static void exception(String iotabname, OutputWriter err, Exception ex) {
        if (iotabname != null) {
            exception(iotabname, ex);
        } else if (err != null) {
            exception(err, ex);
        } else {
            exception(ex);
        }
    }

    public static void exception(String iotabname, Exception ex) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(ex);
        Logger.getLogger("uk.theretiredprogrammer.actionssupport.UserReporting").log(Level.SEVERE, "", ex);
    }

    public static void exception(Exception ex) {
        exception("Error Reporting", ex);
    }

    public static void exception(OutputWriter err, Exception ex) {
        err.println(ex);
        Logger.getLogger("uk.theretiredprogrammer.actionssupport.UserReporting").log(Level.SEVERE, "", ex);
    }

    public static void exceptionWithMessage(String iotabname, OutputWriter err, String message, Exception ex) {
        if (iotabname != null) {
            exceptionWithMessage(iotabname, message, ex);
        } else if (err != null) {
            exceptionWithMessage(err, message, ex);
        } else {
            exceptionWithMessage(message, ex);
        }
    }

    public static void exceptionWithMessage(String iotabname, String message, Exception ex) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(message + " " + ex);
        Logger.getLogger("uk.theretiredprogrammer.actionssupport.UserReporting").log(Level.SEVERE, message, ex);
    }

    public static void exceptionWithMessage(String message, Exception ex) {
        exceptionWithMessage("Error Reporting", message, ex);
    }

    public static void exceptionWithMessage(OutputWriter err, String message, Exception ex) {
        err.println(message + " " + ex);
        Logger.getLogger("uk.theretiredprogrammer.actionssupport.UserReporting").log(Level.SEVERE, message, ex);
    }

    public static void error(String iotabname, OutputWriter err, String message) {
        if (iotabname != null) {
            error(iotabname, message);
        } else if (err != null) {
            error(err, message);
        } else {
            error(message);
        }
    }

    public static void error(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(message);
        Logger.getLogger("uk.theretiredprogrammer.actionssupport.UserReporting").log(Level.SEVERE, message);
    }

    public static void error(String message) {
        error("Error Reporting", message);
    }

    public static void error(OutputWriter err, String message) {
        err.println(message);
        Logger.getLogger("uk.theretiredprogrammer.actionssupport.UserReporting").log(Level.SEVERE, message);
    }

    public static void warning(String iotabname, OutputWriter err, String message) {
        if (iotabname != null) {
            warning(iotabname, message);
        } else if (err != null) {
            warning(err, message);
        } else {
            warning(message);
        }
    }

    public static void warning(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(message);
        Logger.getLogger("uk.theretiredprogrammer.actionssupport.UserReporting").log(Level.WARNING, message);
    }

    public static void warning(String message) {
        warning("Error Reporting", message);
    }

    public static void warning(OutputWriter err, String message) {
        err.println(message);
        Logger.getLogger("uk.theretiredprogrammer.actionssupport.UserReporting").log(Level.WARNING, message);
    }

    public static void info(String iotabname, OutputWriter err, String message) {
        if (iotabname != null) {
            info(iotabname, message);
        } else if (err != null) {
            info(err, message);
        } else {
            info(message);
        }
    }

    public static void info(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getOut().println(message);
        Logger.getLogger("uk.theretiredprogrammer.actionssupport.UserReporting").log(Level.INFO, message);
    }

    public static void info(String message) {
        info("Error Reporting", message);
    }

    public static void info(OutputWriter err, String message) {
        err.println(message);
        Logger.getLogger("uk.theretiredprogrammer.actionssupport.UserReporting").log(Level.INFO, message);
    }
}
