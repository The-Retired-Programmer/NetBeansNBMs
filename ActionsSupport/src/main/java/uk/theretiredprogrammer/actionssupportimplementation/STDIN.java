/*
 * Copyright 2022-23 Richard Linsdale
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.function.Supplier;
import org.netbeans.api.io.OutputWriter;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import uk.theretiredprogrammer.actionssupport.UserReporting;

public class STDIN extends ProcessIO<OutputStream, Writer> {

    private static enum InStyle {
        IGNORE, EMPTY, FILEOBJECT, DATAOBJECT, FILE, FILESTREAM, FILEREADER
    }

    private InStyle mode = InStyle.IGNORE;
    private Task task;

    private OutputStream out = null;
    private Writer writer = null;

    private FileObject fileobject;
    private DataObject dataobject;
    private File file;
    private InputStream instream;
    private BufferedReader reader;
    
    public STDIN() {
    }

    public STDIN(STDIN source) {
        this.mode = source.mode;
        this.task = source.task;
        this.out = source.out;
        this.writer = source.writer;
        this.fileobject = source.fileobject;
        this.dataobject = source.dataobject;
        this.file = source.file;
        this.instream = source.instream;
        this.reader = source.reader;
    }

    public void ignore() {
        mode = InStyle.IGNORE;
    }

    public void empty() {
        mode = InStyle.EMPTY;
    }

    public void fromFile(FileObject fileobject) {
        mode = InStyle.FILEOBJECT;
        this.fileobject = fileobject;
    }

    public void fromFile(File file) {
        mode = InStyle.FILE;
        this.file = file;
    }

    public void fromFile(DataObject dataobject) {
        mode = InStyle.DATAOBJECT;
        this.dataobject = dataobject;
    }

    public void fromFile(InputStream instream) {
        mode = InStyle.FILESTREAM;
        this.instream = instream;
    }

    public void fromFile(BufferedReader reader) {
        mode = InStyle.FILEREADER;
        this.reader = reader;
    }

    // active methods called during Process setup and take down
    @Override
    public Task startTransfer(Supplier<OutputStream> streamSupplier, Supplier<Writer> rwSupplier, String iotabname, OutputWriter err) {
        RequestProcessor processor = new RequestProcessor("stdin");
        task = processor.post(() -> stdinTransfer(streamSupplier, rwSupplier, iotabname, err));
        return task;
    }

    @Override
    public void waitFinished(long timeout) throws InterruptedException {
        task.waitFinished(timeout);
    }

    @Override
    public void close(Process process, String iotabname, OutputWriter err) {
        try {
            if (out != null) {
                out.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ex) {
            UserReporting.warning("Closing STDIN " + ex);
        }
    }

    private void stdinTransfer(Supplier<OutputStream> streamSupplier, Supplier<Writer> rwSupplier, String iotabname, OutputWriter err) {
        try {
            switch (mode) {
                case IGNORE:
                    // null action 
                    break;
                case EMPTY:
                    streamSupplier.get().close();
                    break;
                case FILEOBJECT:
                    out = streamSupplier.get();
                    streamTransfer(fileobject.getInputStream(), out);
                    break;
                case DATAOBJECT:
                    out = streamSupplier.get();
                    streamTransfer(dataobject.getPrimaryFile().getInputStream(), out);
                    break;
                case FILE:
                    out = streamSupplier.get();
                    streamTransfer(new FileInputStream(file), out);
                    break;
                case FILESTREAM:
                    out = streamSupplier.get();
                    streamTransfer(instream, out);
                    break;
                case FILEREADER:
                    writer = rwSupplier.get();
                    readerTransfer(reader, writer);
                    break;
                default:
                    UserReporting.error(iotabname, err, "Unknown mode in STDIN: " + mode);
            }
        } catch (IOException ex) {
            UserReporting.warning(iotabname, err, "Could not write to STDIN " + ex);
        }
    }
}
