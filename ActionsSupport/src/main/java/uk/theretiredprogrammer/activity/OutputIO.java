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
import uk.theretiredprogrammer.actionssupport.UserReporting;

public class OutputIO {

    private static enum OutStyle {
        IGNORE, DISCARD, FILEOBJECT, DATAOBJECT, FILE, FILESTREAM, FILEWRITER, IOSTDOUT, IOSTDERR
    }

    public final String name;

    private OutStyle mode = OutStyle.IGNORE;

    private Writer writer;
    private OutputStream outstream;
    private FileObject fileobject;
    private File file;
    private DataObject dataobject;

    public OutputIO(String name) {
        this.name = name;
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

    public Writer getWriter(InputOutput io, String iotabname) {
        switch (mode) {
            case IGNORE:
                return null;
            case DISCARD:
                return Writer.nullWriter();
            case IOSTDOUT:
                return io.getOut();
            case IOSTDERR:
                return io.getErr();
            case DATAOBJECT:
            case FILE:
            case FILESTREAM:
                OutputStream os = getOutputStream(iotabname);
                return os == null ? null : new OutputStreamWriter(os);
            case FILEWRITER:
                return writer;
            default:
                UserReporting.error(iotabname, "Unknown mode in " + name + ": " + mode);
        }
        return null;
    }

    public OutputStream getOutputStream(String iotabname) {
        try {
            switch (mode) {
                case IGNORE:
                    return null;
                case DISCARD:
                    return OutputStream.nullOutputStream();
                case IOSTDOUT:
                    return null;
                case IOSTDERR:
                    return null;
                case FILEOBJECT:
                    return fileobject.getOutputStream();
                case DATAOBJECT:
                    return dataobject.getPrimaryFile().getOutputStream();
                case FILE:
                    return new FileOutputStream(file);
                case FILESTREAM:
                    return outstream;
                case FILEWRITER:
                    return null;
                default:
                    UserReporting.error(iotabname, "Unknown mode in " + name + ": " + mode);
            }
        } catch (IOException ex) {
            UserReporting.warning(iotabname, "Could not open " + name + " OutputStream " + ex);
        }
        return null;
    }
}
