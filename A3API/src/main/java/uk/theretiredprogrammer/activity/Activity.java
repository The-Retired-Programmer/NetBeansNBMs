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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Supplier;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import uk.theretiredprogrammer.util.ApplicationException;

/**
 * The Interface for the Activity
 * 
 * Methods to configure and execute objects from the Activities Library.
 *
 */
public interface Activity {

    /**
     * Request the Activity can be associated with an IO Tab
     *
     * @param iotabname the name of the tab
     * @return this instance
     */
    public Activity needsIOTab(String iotabname);

    /**
     * Request that the Activity is a Duplex Transfer (No external process and
     * two channels TX and RX)
     *
     * @param txStreamSupplier the supplier of an OutputStream which receives
     * data from the Activity TX channel
     * @param rxStreamSupplier the supplier of an InputStream which will provide
     * data for the Activity RX channel
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity setDuplexTransfer(Supplier<OutputStream> txStreamSupplier, Supplier<InputStream> rxStreamSupplier) throws ApplicationException;

    /**
     * Request that the Activity is a Duplex Transfer (No external process and
     * two channels TX and RX)
     *
     * @param txStreamSupplier the supplier of an OutputStream which receives
     * data from the Activity TX channel
     * @param rxStreamSupplier the supplier of an InputStream which will provide
     * data for the Activity RX channel
     * @param onTabClose a function to be run when the IO Tab is closed
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity setDuplexTransfer(Supplier<OutputStream> txStreamSupplier, Supplier<InputStream> rxStreamSupplier, Runnable onTabClose) throws ApplicationException;

    /**
     * Request that the Activity runs an External Process (with up to three
     * channels STDIN, STDOUT and STDERR)
     *
     * @param command the command
     * @param args the command arguments
     * @param dir the working directory
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity setExternalProcess(final String command, final String args, FileObject dir) throws ApplicationException;

    /**
     * Run the configured Activity
     *
     * Wrapping the IOTab output with a initial message and a final "... done"
     * message when the activity has finished.
     *
     * @param message the initial message
     */
    public void run(String message);

    /**
     * Run the configured Activity
     */
    public void run();

    // the IOTab
    /**
     * Clear the IOTab before each usage.
     *
     * @return this instance
     */
    public Activity ioTabClear();

    /**
     * the STDERR channel identifier
     */
    public String STDERR = "STDERR";

    /**
     * the STDIN channel identifier
     */
    public String STDIN = "STDIN";

    /**
     * the STDOUT channel identifier
     */
    public String STDOUT = "STDOUT";

    /**
     * the RX channel identifier
     */
    public String RX = "RX";

    /**
     * the TX channel identifier
     */
    public String TX = "TX";

    /**
     * The Channel's expected Data Transfer Mode
     */
    public static enum InputDataTransferStyle {

        /**
         * Transfer one char at a time (reader-> output stream)
         */
        CHAR_READER,
        /**
         * Transfer one line at a time (reader-> writer)
         */
        BUFFERED_READER,
        /**
         * Transfer 8K at a time (input stream -> out stream)
         */
        BUFFERED_STREAM,
        /**
         * Either a Buffered Stream (or if it is not available) a Buffered
         * Reader
         */
        BUFFERED_STREAM_OR_READER
    }

    // InputIO configuration
    /**
     * Input is an empty file;
     *
     * @param name the channel name
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity inputEmpty(String name) throws ApplicationException;

    /**
     * Input from a FileObject.
     *
     * @param name the channel name
     * @param fileobject the source
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity inputFromFile(String name, FileObject fileobject) throws ApplicationException;

    /**
     * Input from a File.
     *
     * @param name the channel name
     * @param file the source
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity inputFromFile(String name, File file) throws ApplicationException;

    /**
     * Input from a DataObject.
     *
     * @param name the channel name
     * @param dataobject the source
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity inputFromFile(String name, DataObject dataobject) throws ApplicationException;

    /**
     * Input from a InputStream.
     *
     * @param name the channel name
     * @param instream the source
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity inputFromFile(String name, InputStream instream) throws ApplicationException;

    /**
     * Input from a Reader.
     *
     * @param name the channel name
     * @param reader the source
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity inputFromFile(String name, Reader reader) throws ApplicationException;

    /**
     * Input from the IOTab STDIN.
     *
     * @param name the channel name
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity inputFromIOSTDIN(String name) throws ApplicationException;

    /**
     * The Channel's expected Data Transfer Mode
     */
    public static enum OutputDataTransferStyle {

        /**
         * Transfer one char at a time (input stream -> Writer)
         */
        CHAR_WRITER,
        /**
         * Transfer one line at a time (reader-> writer)
         */
        BUFFERED_WRITER,
        /**
         * Transfer 8K at a time (input stream -> out stream)
         */
        BUFFERED_STREAM,
        /**
         * Either a Buffered Stream (or if it is not available) a Buffered
         * Writer
         */
        BUFFERED_STREAM_OR_WRITER
    }

    // OutputIO configuration
    /**
     * Output is discarded.
     *
     * @param name the channel name
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity outputDiscard(String name) throws ApplicationException;

    /**
     * Output to a FileObject.
     *
     * @param name the channel name
     * @param fileobject the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity outputToFile(String name, FileObject fileobject) throws ApplicationException;

    /**
     * Output to a File.
     *
     * @param name the channel name
     * @param file the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity outputToFile(String name, File file) throws ApplicationException;

    /**
     * Output to a DataObject.
     *
     * @param name the channel name
     * @param dataobject the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity outputToFile(String name, DataObject dataobject) throws ApplicationException;

    /**
     * Output to an OutputStream.
     *
     * @param name the channel name
     * @param outstream the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity outputToFile(String name, OutputStream outstream) throws ApplicationException;

    /**
     * Output to a Writer.
     *
     * @param name the channel name
     * @param writer the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity outputToFile(String name, Writer writer) throws ApplicationException;

    /**
     * Output to the IOTab STDERR.
     *
     * @param name the channel name
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity outputToIOSTDERR(String name) throws ApplicationException;

    /**
     * Output to the IOTab STDOUT.
     *
     * @param name the channel name
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity outputToIOSTDOUT(String name) throws ApplicationException;
}
