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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.netbeans.api.io.InputOutput;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import uk.theretiredprogrammer.util.ApplicationException;

public class InputIO {

    private static enum InStyle {
        IGNORE, EMPTY, IOSTDIN, FILEOBJECT, DATAOBJECT, FILE, FILESTREAM, FILEREADER
    }
    
    private InStyle mode = InStyle.IGNORE;

    private FileObject fileobject;
    private DataObject dataobject;
    private File file;
    private InputStream instream;
    private Reader reader;
    
    public boolean isIOConfigured() {
        return mode != InStyle.IGNORE;
    }

    public void ignore() {
        mode = InStyle.IGNORE;
    }

    public void empty() {
        mode = InStyle.EMPTY;
    }

    public void fromIOSTDIN() {
        mode = InStyle.IOSTDIN;
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
    
    public boolean canProvideReader() {
        return mode != InStyle.IGNORE;
    }

    public Reader getReader(InputOutput io) throws ApplicationException, IOException {
        switch (mode) {
            case EMPTY:
                return new StringReader("");
            case IOSTDIN:
                return io.getIn();
            case FILEOBJECT:
            case DATAOBJECT:
            case FILE:
            case FILESTREAM:
                return new InputStreamReader(getInputStream());
            case FILEREADER:
                return reader;
        }
        throw new ApplicationException("Failed to find a STDIN reader");
    }
    
    public boolean canProvideStream() {
        return !(mode == InStyle.IGNORE || mode == InStyle.IOSTDIN || mode == InStyle.FILEREADER);
    }

    public InputStream getInputStream() throws ApplicationException, IOException {
        switch (mode) {
            case EMPTY:
                return new ByteArrayInputStream(new byte[0]);
            case FILEOBJECT:
                return fileobject.getInputStream();
            case DATAOBJECT:
                return dataobject.getPrimaryFile().getInputStream();
            case FILE:
                return new FileInputStream(file);
            case FILESTREAM:
                return instream;
        }
        throw new ApplicationException("Failed to find a STDIN stream");
    }
}
