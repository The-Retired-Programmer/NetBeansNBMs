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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import uk.theretiredprogrammer.actionssupport.UserReporting;
import uk.theretiredprogrammer.activityimplementation.IO;

public class InputIO extends IO {

    private static enum InStyle {
        IGNORE, EMPTY, IO, FILEOBJECT, DATAOBJECT, FILE, FILESTREAM, FILEREADER
    }

    private InStyle mode = InStyle.IGNORE;

    private FileObject fileobject;
    private DataObject dataobject;
    private File file;
    private InputStream instream;
    private Reader reader;
    private Reader ioreader;

    public InputIO(String name) {
        super(name);
    }

    public InputIO(InputIO source) {
        super(source);
        this.mode = source.mode;
        this.fileobject = source.fileobject;
        this.dataobject = source.dataobject;
        this.file = source.file;
        this.instream = source.instream;
        this.reader = source.reader;
        this.ioreader = source.ioreader;
    }

    public void ignore() {
        mode = InStyle.IGNORE;
    }

    public void empty() {
        mode = InStyle.EMPTY;
    }

    public void fromIO() {
        mode = InStyle.IO;
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

    public void fromFile(Reader reader) {
        mode = InStyle.FILEREADER;
        this.reader = reader;
    }

    public void setReader(Reader ioreader) {
        this.ioreader = new BufferedReader(ioreader);
    }

    public Reader getReader(String iotabname) {
        switch (mode) {
            case IGNORE:
                return null;
            case EMPTY:
                return new StringReader("");
            case IO:
                return ioreader;
            case FILEOBJECT:
            case DATAOBJECT:
            case FILE:
            case FILESTREAM:
                InputStream is = getInputStream(iotabname);
                return is == null ? null : new InputStreamReader(is);
            case FILEREADER:
                return reader;
            default:
                UserReporting.error(iotabname, "Unknown mode in " + name + ": " + mode);
        }
        return null;
    }

    public BufferedReader getBufferReader(String iotabname) {
        Reader rdr = getReader(iotabname);
        return rdr == null ? null : new BufferedReader(rdr);
    }

    public InputStream getInputStream(String iotabname) {
        try {
            switch (mode) {
                case IGNORE:
                    return null;
                case EMPTY:
                    return new ByteArrayInputStream(new byte[0]);
                case IO:
                    return null;
                case FILEOBJECT:
                    return fileobject.getInputStream();
                case DATAOBJECT:
                    return dataobject.getPrimaryFile().getInputStream();
                case FILE:
                    return new FileInputStream(file);
                case FILESTREAM:
                    return instream;
                case FILEREADER:
                    return null;
                default:
                    UserReporting.error(iotabname, "Unknown mode in " + name + ": " + mode);
            }
        } catch (IOException ex) {
            UserReporting.warning(iotabname, "Could not open " + name + " InputStream " + ex);
        }
        return null;
    }
}
