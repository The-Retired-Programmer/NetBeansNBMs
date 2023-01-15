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
import org.netbeans.api.io.OutputWriter;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 * ActivityIO
 * 
 * manages the collection and presentation of all IO - used by Activity.
 * 
 */
public class ActivityIO {

    public static final int STDOUT =0;
    public static final int STDERR =1;
    public static final int STDIN =0;
    
    public final IOTab iotab;
    
    public final InputIO[] inputs;
    public final OutputIO[] outputs;
    private final int stdinIOmap;
    private final int stdoutIOmap;
    private final int stderrIOmap;
    
    public ActivityIO() {
        this(1,new String[] {"STDIN"}, 2, new String[]{"STDOUT", "STDERR"}, 0, 0, 1);
    }

    /**
     * Constructor
     * @param maxinputs 
     * @param inputnames 
     * @param maxoutputs
     * @param outputnames
     * @param stdinIOmap
     * @param stdoutIOmap
     * @param stderrIOmap
     */
    public ActivityIO(int maxinputs, String[] inputnames, int maxoutputs, String[] outputnames,
            int stdinIOmap, int stdoutIOmap, int stderrIOmap) {
        inputs = new InputIO[maxinputs];
        if (maxinputs > 0) {
            for(int i=0; i<maxinputs; i++){
                inputs[i] = new InputIO(inputnames[i]);
            }
        }
        outputs = new OutputIO[maxoutputs];
        if (maxoutputs > 0) {
            for(int i=0; i<maxoutputs; i++){
                outputs[i] = new OutputIO(outputnames[i]);
            }
        }
        this.stdinIOmap = stdinIOmap;
        this.stdoutIOmap = stdoutIOmap;
        this.stderrIOmap = stderrIOmap;
        this.iotab = new IOTab();
    }
    
    public void setIOReaderAndWriters(Reader stdin, OutputWriter stdout,OutputWriter stderr) {
        if (stdinIOmap != -1) {
            inputs[stdinIOmap].setReader(stdin);
        }
        if (stdoutIOmap != -1) {
            outputs[stdoutIOmap].setOutputWriter(stdout);
        }
        if (stderrIOmap != -1) {
            outputs[stderrIOmap].setOutputWriter(stderr);
        }
    }

    // the IOTab 
    
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

    // InputIO configuration

    /**
     * Input is ignored.
     *
     * @param i the input number (0 .. maxinputs-1)
     * @return this object
     */
    public ActivityIO inputIgnore(int i) {
        inputs[i].ignore();
        return this;
    }

    /**
     * Input is an empty file;
     *
     * @param i the input number (0 .. maxinputs-1)
     * @return this object
     */
    public ActivityIO inputEmpty(int i) {
        inputs[i].empty();
        return this;
    }

    /**
     * Input from the IOTab.
     *
     * @param i the input number (0 .. maxinputs-1)
     * @return this object
     */
    public ActivityIO inputFromIO(int i) {
        inputs[i].fromIO();
        return this;
    }

    /**
     * Input from a FileObject.
     *
     * @param i the input number (0 .. maxinputs-1)
     * @param fileobject the source
     * @return this object
     */
    public ActivityIO inputFromFile(int i, FileObject fileobject) {
        inputs[i].fromFile(fileobject);
        return this;
    }

    /**
     * Input from a File.
     *
     * @param i the input number (0 .. maxinputs-1)
     * @param file the source
     * @return this object
     */
    public ActivityIO inputFromFile(int i, File file) {
        inputs[i].fromFile(file);
        return this;
    }

    /**
     * Input from a DataObject.
     *
     * @param i the input number (0 .. maxinputs-1)
     * @param dataobject the source
     * @return this object
     */
    public ActivityIO inputFromFile(int i, DataObject dataobject) {
        inputs[i].fromFile(dataobject);
        return this;
    }

    /**
     * Input from a InputStream.
     *
     * @param i the input number (0 .. maxinputs-1)
     * @param instream the source
     * @return this object
     */
    public ActivityIO inputFromFile(int i, InputStream instream) {
        inputs[i].fromFile(instream);
        return this;
    }

    /**
     * Input from a Reader.
     *
     * @param i the input number (0 .. maxinputs-1)
     * @param reader the source
     * @return this object
     */
    public ActivityIO inputFromFile(int i, Reader reader) {
        inputs[i].fromFile(reader);
        return this;
    }

    // OutputIO configuration 
    
    /**
     * Output is ignored.
     *
     * @param i the output number (0 .. maxoutputs-1)
     * @return this object
     */
    public ActivityIO outputIgnore(int i) {
        outputs[i].ignore();
        return this;
    }

    /**
     * Output is discarded.
     *
     * @param i the output number (0 .. maxoutputs-1)
     * @return this object
     */
    public ActivityIO outputDiscard(int i) {
        outputs[i].discard();
        return this;
    }

    /**
     * Output to the IOTab.
     *
     * @param i the output number (0 .. maxoutputs-1)
     * @return this object
     */
    public ActivityIO outputToIO(int i) {
        outputs[i].toOutputWriter();
        return this;
    }

    /**
     * Output to a FileObject.
     *
     * @param i the output number (0 .. maxoutputs-1)
     * @param fileobject the target
     * @return this object
     */
    public ActivityIO outputToFile(int i, FileObject fileobject) {
        outputs[i].toFile(fileobject);
        return this;
    }

    /**
     * Output to a File.
     *
     * @param i the output number (0 .. maxoutputs-1)
     * @param file the target
     * @return this object
     */
    public ActivityIO outputToFile(int i, File file) {
        outputs[i].toFile(file);
        return this;
    }

    /**
     * Output to a DataObject.
     *
     * @param i the output number (0 .. maxoutputs-1)
     * @param dataobject the target
     * @return this object
     */
    public ActivityIO outputToFile(int i, DataObject dataobject) {
        outputs[i].toFile(dataobject);
        return this;
    }

    /**
     * Output to an OutputStream.
     *
     * @param i the output number (0 .. maxoutputs-1)
     * @param outstream the target
     * @return this object
     */
    public ActivityIO outputToFile(int i, OutputStream outstream) {
        outputs[i].toFile(outstream);
        return this;
    }

    /**
     * Output to a Writer.
     *
     * @param i the output number (0 .. maxoutputs-1)
     * @param writer the target
     * @return this object
     */
    public ActivityIO outputToFile(int i, Writer writer) {
        outputs[i].toFile(writer);
        return this;
    }
}
