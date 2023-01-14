/*
 * Copyright 2022-23 Richard Linsdale.
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
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 * ActivityIO
 * 
 * manages the collection and presentation of all IO - used by Activity.
 * 
 */
public class ActivityIO {

    public final InputIO stdin;
    public final OutputIO stdout;
    public final OutputIO stderr;
    public final IOTab iotab;

    /**
     * Constructor
     */
    public ActivityIO() {
        this.stdin = new InputIO("STDIN");
        this.stdout = new OutputIO("STDOUT");
        this.stderr = new OutputIO("STDERR");
        this.iotab = new IOTab();
    }

    /**
     * Clone a NbCLIDescriptor object.
     *
     * @param source the object to clone from.
     */
    public ActivityIO(ActivityIO source) {
        this.stderr = new OutputIO(source.stderr);
        this.stdout = new OutputIO(source.stdout);
        this.stdin = new InputIO(source.stdin);
        this.iotab = new IOTab(source.iotab);
    }

    // the IOTab Descriptor 
    /**
     * Define the IOTab to be used.
     *
     * @param tabname tabname to be used/reused.
     * @return this object
     */
    public ActivityIO ioTabName(String tabname) {
        this.iotab.name = tabname;
        return this;
    }

    /**
     * Clear the IOTab before each usage.
     *
     * @return this object
     */
    public ActivityIO ioTabClear() {
        this.iotab.reset = true;
        return this;
    }

    // the InputIO Descriptor
    /**
     * Ignore InputIO.
     *
     * @return this object
     */
    public ActivityIO stdinIgnore() {
        stdin.ignore();
        return this;
    }

    /**
     * Pass an empty file to InputIO;
     *
     * @return this object
     */
    public ActivityIO stdinEmpty() {
        stdin.empty();
        return this;
    }

    /**
     * Pass the IO Tab to InputIO.
     *
     * @return this object
     */
    public ActivityIO stdinFromIO() {
        stdin.fromIO();
        return this;
    }

    /**
     * Pass a FileObject to InputIO.
     *
     * @param fileobject the source
     * @return this object
     */
    public ActivityIO stdinFromFile(FileObject fileobject) {
        stdin.fromFile(fileobject);
        return this;
    }

    /**
     * Pass a File to InputIO.
     *
     * @param file the source
     * @return this object
     */
    public ActivityIO stdinFromFile(File file) {
        stdin.fromFile(file);
        return this;
    }

    /**
     * Pass a DataObject to InputIO.
     *
     * @param dataobject the source
     * @return this object
     */
    public ActivityIO stdinFromFile(DataObject dataobject) {
        stdin.fromFile(dataobject);
        return this;
    }

    /**
     * Pass an InputStream to InputIO.
     *
     * @param instream the source
     * @return this object
     */
    public ActivityIO stdinFromFile(InputStream instream) {
        stdin.fromFile(instream);
        return this;
    }

    /**
     * Pass a Reader to InputIO.
     *
     * @param reader the source
     * @return this object
     */
    public ActivityIO stdinFromFile(Reader reader) {
        stdin.fromFile(reader);
        return this;
    }

    // the STDOUT Descriptor
    /**
     * Do not handle STDOUT.
     *
     * @return this object
     */
    public ActivityIO stdoutIgnore() {
        stdout.ignore();
        return this;
    }

    /**
     * Discard the STDOUT generated.
     *
     * @return this object
     */
    public ActivityIO stdoutDiscard() {
        stdout.discard();
        return this;
    }

    /**
     * Pass the STDOUT to the IOTab.
     *
     * @return this object
     */
    public ActivityIO stdoutToIO() {
        stdout.toOutputWriter();
        return this;
    }

    /**
     * Pass the STDOUT to a FileObject.
     *
     * @param fileobject the target
     * @return this object
     */
    public ActivityIO stdoutToFile(FileObject fileobject) {
        stdout.toFile(fileobject);
        return this;
    }

    /**
     * Pass the STDOUT to a File.
     *
     * @param file the target
     * @return this object
     */
    public ActivityIO stdoutToFile(File file) {
        stdout.toFile(file);
        return this;
    }

    /**
     * Pass the STDOUT to a DataObject.
     *
     * @param dataobject the target
     * @return this object
     */
    public ActivityIO stdoutToFile(DataObject dataobject) {
        stdout.toFile(dataobject);
        return this;
    }

    /**
     * Pass the STDOUT to an OutputStream.
     *
     * @param outstream the target
     * @return this object
     */
    public ActivityIO stdoutToFile(OutputStream outstream) {
        stdout.toFile(outstream);
        return this;
    }

    /**
     * Pass the STDOUT to a Writer.
     *
     * @param writer the target
     * @return this object
     */
    public ActivityIO stdoutToFile(Writer writer) {
        stdout.toFile(writer);
        return this;
    }

    // the STDERR Descriptor
    /**
     * Do not handle STDERR.
     *
     * @return this object
     */
    public ActivityIO stderrIgnore() {
        stderr.ignore();
        return this;
    }

    /**
     * Discard the STDERR generated.
     *
     * @return this object
     */
    public ActivityIO stderrDiscard() {
        stderr.discard();
        return this;
    }

    /**
     * Pass the STDERR to the IOTab.
     *
     * @return this object
     */
    public ActivityIO stderrToIO() {
        stderr.toOutputWriter();
        return this;
    }

    /**
     * Pass the STDERR to a FileObject.
     *
     * @param fileobject the target
     * @return this object
     */
    public ActivityIO stderrToFile(FileObject fileobject) {
        stderr.toFile(fileobject);
        return this;
    }

    /**
     * Pass the STDERR to a File.
     *
     * @param file the target
     * @return this object
     */
    public ActivityIO stderrToFile(File file) {
        stderr.toFile(file);
        return this;
    }

    /**
     * Pass the STDERR to a DataObject.
     *
     * @param dataobject the target
     * @return this object
     */
    public ActivityIO stderrToFile(DataObject dataobject) {
        stderr.toFile(dataobject);
        return this;
    }

    /**
     * Pass the STDERR to an OutputStream.
     *
     * @param outstream the target
     * @return this object
     */
    public ActivityIO stderrToFile(OutputStream outstream) {
        stderr.toFile(outstream);
        return this;
    }

    /**
     * Pass the STDERR to a Writer.
     *
     * @param writer the target
     * @return this object
     */
    public ActivityIO stderrToFile(Writer writer) {
        stderr.toFile(writer);
        return this;
    }

}
