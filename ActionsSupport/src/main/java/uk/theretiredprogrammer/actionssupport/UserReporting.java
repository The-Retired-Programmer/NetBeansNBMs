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
     * Otherwise the report is written to STDERR of the default reporting IoTab.
     *
     * @param iotabname the IoTab name
     * @param ex the exception to be reported
     */
    public static void exception(String iotabname, Exception ex) {
        logException(iotabname != null ? iotabname : DEFAULT_IOTAB, ex);
    }

    /**
     * Write an exception report to both the default IoTab using STDERR and also
     * the IDE log (SEVERE).
     *
     * @param ex the exception to be reported
     */
    public static void exception(Exception ex) {
        logException(DEFAULT_IOTAB, ex);
    }

    /**
     * Write an exception report with additional message to both the IoTab and
     * also the IDE log (SEVERE).
     *
     * If iotabname is defined, then the report is written to STDERR of that
     * IoTab.
     *
     * Otherwise the report is written to STDERR of the default reporting IoTab.
     *
     * @param iotabname the IoTab name
     * @param message the message which prefixes the exception reporting
     * @param ex the exception to be reported
     */
    public static void exceptionWithMessage(String iotabname, String message, Exception ex) {
        logExceptionWithMessage(iotabname != null ? iotabname : DEFAULT_IOTAB, message, ex);
    }

    /**
     * Write an exception report with additional message to both the default
     * IoTab using STDERR and also the IDE log (SEVERE).
     *
     * @param message the message which prefixes the exception reporting
     * @param ex the exception to be reported
     */
    public static void exceptionWithMessage(String message, Exception ex) {
        logExceptionWithMessage(DEFAULT_IOTAB, message, ex);
    }

    /**
     * Write an error message to both the IoTab and also the IDE log (SEVERE).
     *
     * If iotabname is defined, then the report is written to STDERR of that
     * IoTab.
     *
     * Otherwise the report is written to STDERR of the default reporting IoTab.
     *
     * @param iotabname the IoTab name
     * @param message the error message
     */
    public static void error(String iotabname, String message) {
        logError(iotabname != null ? iotabname : DEFAULT_IOTAB, message);
    }

    /**
     * Write an error message to both the default IoTab using STDERR and also
     * the IDE log (SEVERE).
     *
     * @param message the error message
     */
    public static void error(String message) {
        logError(DEFAULT_IOTAB, message);
    }

    /**
     * Write a warning message to both the IoTab and also the IDE log (WARNING).
     *
     * If iotabname is defined, then the report is written to STDERR of that
     * IoTab.
     *
     * Otherwise the report is written to STDERR of the default reporting IoTab.
     *
     * @param iotabname the IoTab name
     * @param message the warning message
     */
    public static void warning(String iotabname, String message) {
        logWarning(iotabname != null ? iotabname : DEFAULT_IOTAB, message);
    }

    /**
     * Write a warning message to both the default IoTab using STDERR and also
     * the IDE log (WARNING).
     *
     * @param message the warning message
     */
    public static void warning(String message) {
        logWarning(DEFAULT_IOTAB, message);
    }

    /**
     * Write an informational message to both the IoTab and also the IDE log
     * (INFO).
     *
     * If iotabname is defined, then the report is written to STDERR of that
     * IoTab.
     *
     * Otherwise the report is written to STDERR of the default reporting IoTab.
     *
     * @param iotabname the IoTab name
     * @param message the informational message
     */
    public static void info(String iotabname, String message) {
        logInfo(iotabname != null ? iotabname : DEFAULT_IOTAB, message);
    }

    /**
     * Write an informational message to both the default IoTab using STDERR and
     * also the IDE log (INFO).
     *
     * @param message the informational message
     */
    public static void info(String message) {
        logInfo(DEFAULT_IOTAB, message);
    }
    
    //   =======================================================================
     
    /**
     * Write an error message to the IDE log (SEVERE).
     *
     * @param message the error message
     */
    public static void errorLogOnly(String message) {
        Logger.getLogger(LOGGING).log(Level.SEVERE, message);
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
     * Write an informational message to the IDE log (INFO).
     *
     * @param message the warning message
     */
    public static void infoLogOnly(String message) {
        Logger.getLogger(LOGGING).log(Level.INFO, message);
    }
    
    //   =======================================================================
     
    private static void logException(String iotabname, Exception ex) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(ex);
        Logger.getLogger(LOGGING).log(Level.SEVERE, "", ex);
    }
    
    private static void logExceptionWithMessage(String iotabname, String message, Exception ex) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(message + " " + ex);
        Logger.getLogger(LOGGING).log(Level.SEVERE, message, ex);
    }
    
    private static void logError(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(message);
        Logger.getLogger(LOGGING).log(Level.SEVERE, message);
    }
    
    private static void logWarning(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getErr().println(message);
        Logger.getLogger(LOGGING).log(Level.WARNING, message);
    }
    
    private static void logInfo(String iotabname, String message) {
        InputOutput io = IOProvider.getDefault().getIO(iotabname, false);
        io.show();
        io.getOut().println(message);
        Logger.getLogger(LOGGING).log(Level.INFO, message);
    }
}
