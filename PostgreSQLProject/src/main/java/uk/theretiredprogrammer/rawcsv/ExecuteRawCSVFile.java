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
package uk.theretiredprogrammer.rawcsv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.util.SaveSelfBeforeAction;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.rawcsv.ExecuteRawCSVFile"
)
@ActionRegistration(
        displayName = "#CTL_ExecuteFile"
)
@ActionReference(path = "Loaders/text/x-rawcsv/Actions", position = 150)
@Messages("CTL_ExecuteFile=Make csv file")
public final class ExecuteRawCSVFile implements ActionListener, Runnable {

    private final List<DataObject> context;

    public ExecuteRawCSVFile(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("text-x-rawcsv_execute");
        rp.post(this);
    }

    @Override
    public void run() {
        // fix this later
        PrintWriter err = new PrintWriter(System.err);
        for (DataObject dataObject : context) {
            FileObject input = dataObject.getPrimaryFile();
            SaveSelfBeforeAction.saveIfModified(dataObject);
            try ( BufferedReader rdr = getReader(input);  BufferedReader rules = getRules(input);  PrintWriter wtr = getWriter(input); err) {
                new RawCSV2CSV().convert(rdr, wtr, rules, err);
            } catch (IOException ex) {
                err.println(ex.getLocalizedMessage());
            }
        }
    }

    private BufferedReader getRules(FileObject input) throws FileNotFoundException {
        FileObject parent = input.getParent();
        FileObject rulesfo = parent.getFileObject("rules");
        if (rulesfo == null) {
            throw new FileNotFoundException("rules");
        }
        return new BufferedReader(new InputStreamReader(rulesfo.getInputStream()));
    }

    private BufferedReader getReader(FileObject input) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(input.getInputStream()));
    }

    private PrintWriter getWriter(FileObject input) throws IOException {
        FileObject parent = input.getParent();
        FileObject target = parent.getFileObject(input.getName(), "csv");
        if (target != null) {
            target.delete();
        }
        return new PrintWriter(new OutputStreamWriter(parent.createAndOpen(input.getName() + ".csv")));
    }
}
