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
import uk.theretiredprogrammer.actionssupport.UserReporting;

/**
 * ActivityIO
 *
 * manages the collection and presentation of all IO - used by Activity.
 *
 */
public class ActivityIO {

    public static final String STDOUT = "STDOUT";
    public static final String STDERR = "STDERR";
    public static final String STDIN = "STDIN";

    public final IOTab iotab;

    private InputIO[] inputs;
    private OutputIO[] outputs;

    public ActivityIO() {
        iotab = new IOTab();
        inputs("STDIN");
        outputs("STDOUT", "STDERR");
    }

    public final ActivityIO inputs(String... names) {
        inputs = new InputIO[names.length];
        if (names.length > 0) {
            for (int i = 0; i < names.length; i++) {
                inputs[i] = new InputIO(names[i]);
            }
        }
        return this;
    }

    public final ActivityIO outputs(String... names) {
        outputs = new OutputIO[names.length];
        if (names.length > 0) {
            for (int i = 0; i < names.length; i++) {
                outputs[i] = new OutputIO(names[i]);
            }
        }
        return this;
    }
    
    public InputIO getInputIO(String name) {
        for (InputIO input : inputs) {
            if (name.equals(input.name)) {
                return input;
            }
        }
        UserReporting.warning(iotab.name,"InputIO "+ name+ " not found");
        return null;
    }
    
    public OutputIO getOutputIO(String name) {
        for (OutputIO output : outputs) {
            if (name.equals(output.name)) {
                return output;
            }
        }
        UserReporting.warning(iotab.name,"OutputIO "+ name+ " not found");
        return null;
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
     * @param name the inputIO name
     * @return this object
     */
    public ActivityIO inputIgnore(String name) {
        getInputIO(name).ignore();
        return this;
    }

    /**
     * Input is an empty file;
     *
     * @param name the inputIO name
     * @return this object
     */
    public ActivityIO inputEmpty(String name) {
         getInputIO(name).empty();
        return this;
    }

    /**
     * Input from the IOTab STDIN.
     *
     * @param name the inputIO name
     * @return this object
     */
    public ActivityIO inputFromIOSTDIN(String name) {
         getInputIO(name).fromIOSTDIN();
        return this;
    }

    /**
     * Input from a FileObject.
     *
     * @param name the inputIO name
     * @param fileobject the source
     * @return this object
     */
    public ActivityIO inputFromFile(String name, FileObject fileobject) {
         getInputIO(name).fromFile(fileobject);
        return this;
    }

    /**
     * Input from a File.
     *
     * @param name the inputIO name
     * @param file the source
     * @return this object
     */
    public ActivityIO inputFromFile(String name, File file) {
         getInputIO(name).fromFile(file);
        return this;
    }

    /**
     * Input from a DataObject.
     *
     * @param name the inputIO name
     * @param dataobject the source
     * @return this object
     */
    public ActivityIO inputFromFile(String name, DataObject dataobject) {
         getInputIO(name).fromFile(dataobject);
        return this;
    }

    /**
     * Input from a InputStream.
     *
     * @param name the inputIO name
     * @param instream the source
     * @return this object
     */
    public ActivityIO inputFromFile(String name, InputStream instream) {
         getInputIO(name).fromFile(instream);
        return this;
    }

    /**
     * Input from a Reader.
     *
     * @param name the inputIO name
     * @param reader the source
     * @return this object
     */
    public ActivityIO inputFromFile(String name, Reader reader) {
         getInputIO(name).fromFile(reader);
        return this;
    }

    // OutputIO configuration 
    /**
     * Output is ignored.
     *
     * @param name the outputIO name
     * @return this object
     */
    public ActivityIO outputIgnore(String name) {
         getOutputIO(name).ignore();
        return this;
    }

    /**
     * Output is discarded.
     *
     * @param name the outputIO name
     * @return this object
     */
    public ActivityIO outputDiscard(String name) {
         getOutputIO(name).discard();
        return this;
    }

    /**
     * Output to the IOTab STDOUT.
     *
     * @param name the outputIO name
     * @return this object
     */
    public ActivityIO outputToIOSTDOUT(String name) {
        getOutputIO(name).toIOSTDOUT();
        return this;
    }

    /**
     * Output to the IOTab STDERR.
     *
     * @param name the outputIO name
     * @return this object
     */
    public ActivityIO outputToIOSTDERR(String name) {
        getOutputIO(name).toIOSTDERR();
        return this;
    }

    /**
     * Output to a FileObject.
     *
     * @param name the outputIO name
     * @param fileobject the target
     * @return this object
     */
    public ActivityIO outputToFile(String name, FileObject fileobject) {
        getOutputIO(name).toFile(fileobject);
        return this;
    }

    /**
     * Output to a File.
     *
     * @param name the outputIO name
     * @param file the target
     * @return this object
     */
    public ActivityIO outputToFile(String name, File file) {
        getOutputIO(name).toFile(file);
        return this;
    }

    /**
     * Output to a DataObject.
     *
     * @param name the outputIO name
     * @param dataobject the target
     * @return this object
     */
    public ActivityIO outputToFile(String name, DataObject dataobject) {
        getOutputIO(name).toFile(dataobject);
        return this;
    }

    /**
     * Output to an OutputStream.
     *
     * @param name the outputIO name
     * @param outstream the target
     * @return this object
     */
    public ActivityIO outputToFile(String name, OutputStream outstream) {
        getOutputIO(name).toFile(outstream);
        return this;
    }

    /**
     * Output to a Writer.
     *
     * @param name the outputIO name
     * @param writer the target
     * @return this object
     */
    public ActivityIO outputToFile(String name, Writer writer) {
        getOutputIO(name).toFile(writer);
        return this;
    }
}
