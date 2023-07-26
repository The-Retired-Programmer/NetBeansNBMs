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
package uk.theretiredprogrammer.csv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
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
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.util.ActivitiesAndActionsFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.SaveSelfBeforeAction;
import uk.theretiredprogrammer.util.UserReporting;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.csv.BuildSQLfromCSVFile"
)
@ActionRegistration(
        displayName = "#CTL_BuildFile"
)
@ActionReference(path = "Loaders/text/x-csv/Actions", position = 150)
@Messages("CTL_BuildFile=Build SQL Create Table")
public final class BuildSQLfromCSVFile implements ActionListener, Runnable {

    private final List<DataObject> context;
    private final static String IOTABNAME = "Build SQL from CSV";

    public BuildSQLfromCSVFile(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("text-x-csv_buildsql");
        rp.post(this);
    }

    @Override
    public void run() {
        for (DataObject dataObject : context) {
            FileObject csvfile = dataObject.getPrimaryFile();
            SaveSelfBeforeAction.saveIfModified(dataObject);
            Activity activity;
            try {
                activity = ActivitiesAndActionsFactory.createActivity()
                        .setMethod(() -> createSQL(csvfile))
                        .needsIOTab(IOTABNAME);
            } catch (ApplicationException ex) {
                UserReporting.exception(IOTABNAME, ex);
                return;
            }
            activity.run("Building " + csvfile.getName() + ".sql");
        }
    }

    private void createSQL(FileObject csvfile) {
        try ( PrintWriter sqlwriter = createSQLWriter(csvfile);  BufferedReader csvreader = new BufferedReader(new InputStreamReader(csvfile.getInputStream()))) {
            create(csvfile.getName().replace("-", "_").replace(" ", "_"), csvreader, sqlwriter);
        } catch (IOException ex) {
            UserReporting.exception(IOTABNAME, ex);
        }
    }

    private PrintWriter createSQLWriter(FileObject csvfile) throws IOException {
        String outfilename = csvfile.getName() + ".sql";
        FileObject out = csvfile.getParent().getFileObject(outfilename);
        if (out != null) {
            out.delete();
        }
        return new PrintWriter(new OutputStreamWriter(csvfile.getParent().createAndOpen(outfilename)));
    }

    private void create(String tablename, BufferedReader csvrdr, PrintWriter out) throws IOException {
        String line = csvrdr.readLine();
        out.println("drop table if exists " + tablename + ";\n");
        out.println("create table " + tablename + " (");
        out.println(line.replace(",", " VARCHAR(1000) default null,\n"));
        out.println(" VARCHAR(1000) default null\n);");
    }
}
