/*
 * Copyright 2022 Richard Linsdale
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
package uk.theretiredprogrammer.actionssupportimplementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.function.Supplier;
import org.netbeans.api.io.OutputWriter;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

public abstract class TransferOUT extends ProcessIO<InputStream, BufferedReader> {

    private static enum OutStyle {
        IGNORE, DISCARD, FILEOBJECT, DATAOBJECT, FILE, FILESTREAM, FILEWRITER, OUTPUTWRITER
    }

    private final String name;
    private OutStyle mode = OutStyle.IGNORE;

    private OutputWriter outputwriter;
    private Writer writer;
    private OutputStream outstream;
    private FileObject fileobject;
    private File file;
    private DataObject dataobject;

    private InputStream in = null;
    private BufferedReader reader = null;

    public TransferOUT(String name, Logging logging) {
        super(logging);
        this.name = name;
    }
    
    public TransferOUT(TransferOUT source) {
        super(source);
        this.name = source.name;
        this.mode = source.mode;
        this.outputwriter = source.outputwriter;
        this.writer = source.writer;
        this.outstream = source.outstream;
        this.fileobject = source.fileobject;
        this.file = source.file;
        this.dataobject = source.dataobject;
        this.in = source.in;
        this.reader = source.reader;
    }

    public void ignore() {
        mode = OutStyle.IGNORE;
    }

    public void discard() {
        mode = OutStyle.DISCARD;
    }

    public void toOutputWriter() {
        mode = OutStyle.OUTPUTWRITER;
    }

    public void toFile(FileObject fileobject) {
        mode = OutStyle.FILEOBJECT;
        this.fileobject = fileobject;
    }

    public void toFile(File file) {
        mode = OutStyle.FILE;
        this.file = file;
    }

    public void toFile(DataObject dataobject) {
        mode = OutStyle.DATAOBJECT;
        this.dataobject = dataobject;
    }

    public void toFile(OutputStream outstream) {
        mode = OutStyle.FILESTREAM;
        this.outstream = outstream;
    }

    public void toFile(Writer writer) {
        mode = OutStyle.FILEWRITER;
        this.writer = writer;
    }

    public void setOutputWriter(OutputWriter outputwriter) {
        this.outputwriter = outputwriter;
    }

    // methods called during Process setup and take down
    @Override
    public void startTransfer(Supplier<InputStream>streamSupplier, Supplier<BufferedReader>rwSupplier) {
        try {
            switch (mode) {
                case IGNORE:
                    // null action 
                    break;
                case DISCARD:
                    in = streamSupplier.get();
                    byte[] buf = new byte[8192];
                    while (in.read(buf) != -1) {
                    }
                    break;
                case FILEOBJECT:
                    in = streamSupplier.get();
                    streamTransfer(in, fileobject.getOutputStream());
                    break;
                case DATAOBJECT:
                    in = streamSupplier.get();
                    streamTransfer(in, dataobject.getPrimaryFile().getOutputStream());
                    break;
                case FILE:
                    in = streamSupplier.get();
                    streamTransfer(in, new FileOutputStream(file));
                    break;
                case FILESTREAM:
                    in = streamSupplier.get();
                    streamTransfer(in, outstream);
                    break;
                case FILEWRITER:
                    reader = rwSupplier.get();
                    readerTransfer(reader, writer);
                    break;
                case OUTPUTWRITER:
                    if (outputwriter == null) {
                        logging.user("Could not read " + name + ": OutputWriter is undefined");
                        return;
                    }
                    reader = rwSupplier.get();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        outputwriter.println(line);
                    }
                    break;
                default:
                    logging.severe("Unknown mode in " + name + ": " + mode);
            }
        } catch (IOException ex) {
            logging.user("Could not read " + name + " " + ex);
        }
    }

    @Override
    public void close(Process process) {
        try {
            if (in != null) {
                in.close();
            }
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ex) {
            logging.warning("Closing " + name + " " + ex);
        }
    }
}
