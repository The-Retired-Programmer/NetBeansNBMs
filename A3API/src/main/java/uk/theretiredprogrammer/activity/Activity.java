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
import java.util.function.Consumer;
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
     * Request that the Activity runs an Method an wishes to use STDOUT file handling
     *
     * @param method the method to be called
     * @return this instance
     */
    public Activity setMethod(Consumer<Writer> method);
    
    /**
     * Request that the Activity runs an Method
     *
     * @param method the method to be called
     * @return this instance
     */
    public Activity setMethod(Runnable method);

    /**
     * Request that the Activity is a Device Transfer
     *
     * @param descriptor the device descriptor
     * @return this instance
     */
    public Activity setDevice(DeviceDescriptor descriptor);

    /**
     * Request that the Activity runs an External Process (with up to three
     * channels STDIN, STDOUT and STDERR)
     *
     * @param command the command
     * @param args the command arguments
     * @param dir the working directory
     * @return this instance
     */
    public Activity setExternalProcess(final String command, final String args, FileObject dir);

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

    // STDIN configuration
    /**
     * STDIN is an empty file;
     *
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdinFromEmpty() throws ApplicationException;

    /**
     * STDIN from a FileObject.
     *
     * @param fileobject the source
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdinFromFile(FileObject fileobject) throws ApplicationException;

    /**
     * STDIN from a File.
     *
     * @param file the source
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdinFromFile(File file) throws ApplicationException;

    /**
     * STDIN from a DataObject.
     *
     * @param dataobject the source
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdinFromFile(DataObject dataobject) throws ApplicationException;

    /**
     * STDIN from a InputStream.
     *
     * @param instream the source
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdinFromFile(InputStream instream) throws ApplicationException;

    /**
     * STDIN from a Reader.
     *
     * @param reader the source
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdinFromFile(Reader reader) throws ApplicationException;

    /**
     * STDIN from the IOTab STDIN.
     *
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdinFromIOSTDIN() throws ApplicationException;

    // STDOUT configuration
    /**
     * STDOUT is discarded.
     *
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdoutToDiscard() throws ApplicationException;

    /**
     * STDOUT to a FileObject.
     *
     * @param fileobject the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdoutToFile(FileObject fileobject) throws ApplicationException;

    /**
     * STDOUT to a File.
     *
     * @param file the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdoutToFile(File file) throws ApplicationException;

    /**
     * STDOUT to a DataObject.
     *
     * @param dataobject the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdoutToFile(DataObject dataobject) throws ApplicationException;

    /**
     * STDOUT to an OutputStream.
     *
     * @param outstream the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdoutToFile(OutputStream outstream) throws ApplicationException;

    /**
     * STDOUT to a Writer.
     *
     * @param writer the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdoutToFile(Writer writer) throws ApplicationException;

    /**
     * STDOUT to the IOTab STDERR.
     *
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdoutToIOSTDERR() throws ApplicationException;

    /**
     * STDOUT to the IOTab STDOUT.
     *
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stdoutToIOSTDOUT() throws ApplicationException;

    // STDERR configuration
    /**
     * STDERR is discarded.
     *
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stderrToDiscard() throws ApplicationException;

    /**
     * STDERR to a FileObject.
     *
     * @param fileobject the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stderrToFile(FileObject fileobject) throws ApplicationException;

    /**
     * STDERR to a File.
     *
     * @param file the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stderrToFile(File file) throws ApplicationException;

    /**
     * STDERR to a DataObject.
     *
     * @param dataobject the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stderrToFile(DataObject dataobject) throws ApplicationException;

    /**
     * STDERR to an OutputStream.
     *
     * @param outstream the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stderrToFile(OutputStream outstream) throws ApplicationException;

    /**
     * STDERR to a Writer.
     *
     * @param writer the target
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stderrToFile(Writer writer) throws ApplicationException;

    /**
     * STDERR to the IOTab STDERR.
     *
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stderrToIOSTDERR() throws ApplicationException;

    /**
     * STDERR to the IOTab STDOUT.
     *
     * @return this instance
     * @throws ApplicationException a failure Exception
     */
    public Activity stderrToIOSTDOUT() throws ApplicationException;
    
    /**
     * Convenience variable - the OS specific line terminator
     */
    public static final String NEWLINE = System.getProperty("line.separator");
}
