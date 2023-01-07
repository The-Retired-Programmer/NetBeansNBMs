/*
 * Copyright 2022-2023 Richard Linsdale.
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

    /**
     * The default reporting IoTab is "Error Reporting";
     *
     * All reporting logging uses the
     * "uk.theretiredprogrammer.actionssupport.UserReporting" logger.
     *
     */
    private static final String DEFAULT_IOTAB = "Error Reporting";
    private static final String LOGGING = "uk.theretiredprogrammer.actionssupport.UserReporting";

    /**
     * Write a "start action" message to the defined IoTab STDOUT
     *
     * @param iotabname the IoTab name
     * @param message The message to be output
     */
    public static void startmessage(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getOut().println(message);
    }

    /**
     * Write the standard "end action" message ("... done") to the defined IoTab
     * STDOUT.
     *
     * @param iotabname the IoTab name
     */
    public static void completedmessage(String iotabname) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getOut().println("... done");
    }

    /**
     * Write an exception report to both the IoTab and also the IDE log
     * (SEVERE).
     *
     * If iotabname is defined, then the report is written to STDERR of that
     * IoTab.
     *
     * Otherwise if err is defined the the report is written to that writer.
     *
     * Otherwise the report is written to STDERR of the default reporting IoTab.
     *
     * @param iotabname the IoTab name
     * @param err the output writer to be used
     * @param ex the exception to be reported
     */
    public static void exception(String iotabname, OutputWriter err, Exception ex) {
        if (iotabname != null) {
            exception(iotabname, ex);
        } else if (err != null) {
            exception(err, ex);
        } else {
            exception(ex);
        }
    }

    /**
     * Write an exception report to both the IoTab using STDERR and also the IDE
     * log (SEVERE).
     *
     * @param iotabname the IoTab name
     * @param ex the exception to be reported
     */
    public static void exception(String iotabname, Exception ex) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(ex);
        Logger.getLogger(LOGGING).log(Level.SEVERE, "", ex);
    }

    /**
     * Write an exception report to both the default IoTab using STDERR and also
     * the IDE log (SEVERE).
     *
     * @param ex the exception to be reported
     */
    public static void exception(Exception ex) {
        exception(DEFAULT_IOTAB, ex);
    }

    /**
     * Write an exception report to both an OutputWriter and also the IDE log
     * (SEVERE).
     *
     * @param err the OutputWriter
     * @param ex the exception to be reported
     */
    public static void exception(OutputWriter err, Exception ex) {
        err.println(ex);
        Logger.getLogger(LOGGING).log(Level.SEVERE, "", ex);
    }

    /**
     * Write an exception report with additional message to both the IoTab and
     * also the IDE log (SEVERE).
     *
     * If iotabname is defined, then the report is written to STDERR of that
     * IoTab.
     *
     * Otherwise if err is defined the the report is written to that writer.
     *
     * Otherwise the report is written to STDERR of the default reporting IoTab.
     *
     * @param iotabname the IoTab name
     * @param err the output writer to be used
     * @param message the addition message which prefixes the exception
     * reporting
     * @param ex the exception to be reported
     */
    public static void exceptionWithMessage(String iotabname, OutputWriter err, String message, Exception ex) {
        if (iotabname != null) {
            exceptionWithMessage(iotabname, message, ex);
        } else if (err != null) {
            exceptionWithMessage(err, message, ex);
        } else {
            exceptionWithMessage(message, ex);
        }
    }

    /**
     * Write an exception report with additional message to both the IoTab using
     * STDERR and also the IDE log (SEVERE).
     *
     * @param iotabname the IoTab name
     * @param message the addition message which prefixes the exception
     * reporting
     * @param ex the exception to be reported
     */
    public static void exceptionWithMessage(String iotabname, String message, Exception ex) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(message + " " + ex);
        Logger.getLogger(LOGGING).log(Level.SEVERE, message, ex);
    }

    /**
     * Write an exception report with additional message to both the default
     * IoTab using STDERR and also the IDE log (SEVERE).
     *
     * @param message the addition message which prefixes the exception
     * reporting
     * @param ex the exception to be reported
     */
    public static void exceptionWithMessage(String message, Exception ex) {
        exceptionWithMessage(DEFAULT_IOTAB, message, ex);
    }

    /**
     * Write an exception report with additional message to both an OutputWriter
     * and also the IDE log (SEVERE).
     *
     * @param err the OutputWriter
     * @param message the addition message which prefixes the exception
     * reporting
     * @param ex the exception to be reported
     */
    public static void exceptionWithMessage(OutputWriter err, String message, Exception ex) {
        err.println(message + " " + ex);
        Logger.getLogger(LOGGING).log(Level.SEVERE, message, ex);
    }

    /**
     * Write an error message to both the IoTab and also the IDE log (SEVERE).
     *
     * If iotabname is defined, then the report is written to STDERR of that
     * IoTab.
     *
     * Otherwise if err is defined the the report is written to that writer.
     *
     * Otherwise the report is written to STDERR of the default reporting IoTab.
     *
     * @param iotabname the IoTab name
     * @param err the output writer to be used
     * @param message the error message
     */
    public static void error(String iotabname, OutputWriter err, String message) {
        if (iotabname != null) {
            error(iotabname, message);
        } else if (err != null) {
            error(err, message);
        } else {
            error(message);
        }
    }

    /**
     * Write an error message to both the IoTab using STDERR and also the IDE
     * log (SEVERE).
     *
     * @param iotabname the IoTab name
     * @param message the error message
     */
    public static void error(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(message);
        Logger.getLogger(LOGGING).log(Level.SEVERE, message);
    }
    
    /**
     * Write an error message to the IDE log (SEVERE).
     *
     * @param message the error message
     */
    public static void errorLogOnly(String message) {
        Logger.getLogger(LOGGING).log(Level.SEVERE, message);
    }

    /**
     * Write an error message to both the default IoTab using STDERR and also
     * the IDE log (SEVERE).
     *
     * @param message the error message
     */
    public static void error(String message) {
        error(DEFAULT_IOTAB, message);
    }

    /**
     * Write an error message to both an OutputWriter and also the IDE log
     * (SEVERE).
     *
     * @param err the OutputWriter
     * @param message the error message
     */
    public static void error(OutputWriter err, String message) {
        err.println(message);
        Logger.getLogger(LOGGING).log(Level.SEVERE, message);
    }

    /**
     * Write a warning message to both the IoTab and also the IDE log (WARNING).
     *
     * If iotabname is defined, then the report is written to STDERR of that
     * IoTab.
     *
     * Otherwise if err is defined the the report is written to that writer.
     *
     * Otherwise the report is written to STDERR of the default reporting IoTab.
     *
     * @param iotabname the IoTab name
     * @param err the output writer to be used
     * @param message the warning message
     */
    public static void warning(String iotabname, OutputWriter err, String message) {
        if (iotabname != null) {
            warning(iotabname, message);
        } else if (err != null) {
            warning(err, message);
        } else {
            warning(message);
        }
    }

    /**
     * Write a warning message to both the IoTab using STDERR and also the IDE
     * log (WARNING).
     *
     * @param iotabname the IoTab name
     * @param message the warning message
     */
    public static void warning(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(message);
        Logger.getLogger(LOGGING).log(Level.WARNING, message);
    }
    
    /**
     * Write a warning message to the IDE log (WARNING).
     *
     * @param message the warning message
     */
    public static void warningLogOnly(String message) {
        Logger.getLogger(LOGGING).log(Level.WARNING, message);
    }

    /**
     * Write a warning message to both the default IoTab using STDERR and also
     * the IDE log (WARNING).
     *
     * @param message the warning message
     */
    public static void warning(String message) {
        warning(DEFAULT_IOTAB, message);
    }

    /**
     * Write a warning message to both an OutputWriter and also the IDE log
     * (WARNING).
     *
     * @param err the OutputWriter
     * @param message the warning message
     */
    public static void warning(OutputWriter err, String message) {
        err.println(message);
        Logger.getLogger(LOGGING).log(Level.WARNING, message);
    }

    /**
     * Write an informational message to both the IoTab and also the IDE log
     * (INFO).
     *
     * If iotabname is defined, then the report is written to STDERR of that
     * IoTab.
     *
     * Otherwise if err is defined the the report is written to that writer.
     *
     * Otherwise the report is written to STDERR of the default reporting IoTab.
     *
     * @param iotabname the IoTab name
     * @param err the output writer to be used
     * @param message the informational message
     */
    public static void info(String iotabname, OutputWriter err, String message) {
        if (iotabname != null) {
            info(iotabname, message);
        } else if (err != null) {
            info(err, message);
        } else {
            info(message);
        }
    }

    /**
     * Write an informational message to both the IoTab using STDERR and also
     * the IDE log (INFO).
     *
     * @param iotabname the IoTab name
     * @param message the informational message
     */
    public static void info(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getOut().println(message);
        Logger.getLogger(LOGGING).log(Level.INFO, message);
    }
    
    /**
     * Write an informational message to the IDE log (INFO).
     *
     * @param message the warning message
     */
    public static void infoLogOnly(String message) {
        Logger.getLogger(LOGGING).log(Level.INFO, message);
    }

    /**
     * Write an informational message to both the default IoTab using STDERR and
     * also the IDE log (INFO).
     *
     * @param message the informational message
     */
    public static void info(String message) {
        info(DEFAULT_IOTAB, message);
    }

    /**
     * Write an informational message to both an OutputWriter and also the IDE
     * log (INFO).
     *
     * @param err the OutputWriter
     * @param message the informational message
     */
    public static void info(OutputWriter err, String message) {
        err.println(message);
        Logger.getLogger(LOGGING).log(Level.INFO, message);
    }
}
