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
package uk.theretiredprogrammer.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.netbeans.api.io.InputOutput;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import uk.theretiredprogrammer.util.ApplicationException;

public class OutputIO {

    private static enum OutStyle {
        IGNORE, DISCARD, FILEOBJECT, DATAOBJECT, FILE, FILESTREAM, FILEWRITER, IOSTDOUT, IOSTDERR
    }

    private OutStyle mode = OutStyle.IGNORE;

    private Writer writer;
    private OutputStream outstream;
    private FileObject fileobject;
    private File file;
    private DataObject dataobject;
    

    public boolean isIOConfigured() {
        return mode != OutStyle.IGNORE;
    }

    public void ignore() {
        mode = OutStyle.IGNORE;
    }

    public void discard() {
        mode = OutStyle.DISCARD;
    }

    public void toIOSTDOUT() {
        mode = OutStyle.IOSTDOUT;
    }

    public void toIOSTDERR() {
        mode = OutStyle.IOSTDERR;
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

    public boolean canProvideWriter() {
        return mode != OutStyle.IGNORE;
    }

    public Writer getWriter(InputOutput io) throws ApplicationException, IOException {
        switch (mode) {
            case DISCARD:
                return Writer.nullWriter();
            case IOSTDOUT:
                return io.getOut();
            case IOSTDERR:
                return io.getErr();
            case DATAOBJECT:
            case FILE:
            case FILESTREAM:
                return new OutputStreamWriter(getOutputStream());
            case FILEWRITER:
                return writer;
        }
        throw new ApplicationException("Failed to find a STDERR writer");
    }

    public boolean canProvideStream() {
        return !(mode == OutStyle.IGNORE || mode == OutStyle.IOSTDOUT
                || mode == OutStyle.IOSTDERR || mode == OutStyle.FILEWRITER);
    }

    public OutputStream getOutputStream() throws ApplicationException, IOException {
        switch (mode) {
            case DISCARD:
                return OutputStream.nullOutputStream();
            case FILEOBJECT:
                return fileobject.getOutputStream();
            case DATAOBJECT:
                return dataobject.getPrimaryFile().getOutputStream();
            case FILE:
                return new FileOutputStream(file);
            case FILESTREAM:
                return outstream;
        }
        throw new ApplicationException("Failed to find a STDERR stream");
    }
}
