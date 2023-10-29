/*
 * Copyright 2022-2023 Richard Linsdale.
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
package uk.theretiredprogrammer.html2textile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.util.ActivitiesAndActionsFactory;
import uk.theretiredprogrammer.util.ApplicationException;
import uk.theretiredprogrammer.util.SaveSelfBeforeAction;
import uk.theretiredprogrammer.util.UserReporting;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.html2textilefiles"
)
@ActionRegistration(
        displayName = "#CTL_Html2Textile"
)
@ActionReference(path = "Loaders/text/html/Actions", position = 150)
@Messages("CTL_Html2Textile=Convert to Textile")
public final class ActionHtml2Textile implements ActionListener, Runnable {

    private final List<DataObject> context;
    private final static String IOTABNAME = "Convert to Textile";

    public ActionHtml2Textile(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor rp = new RequestProcessor("text-html_convert");
        rp.post(this);
    }

    @Override
    public void run() {
        ErrHandler err = new ErrHandler((s) -> UserReporting.error(IOTABNAME, s));
        for (DataObject dataObject : context) {
            FileObject input = dataObject.getPrimaryFile();
            SaveSelfBeforeAction.saveIfModified(dataObject);
            Activity translate;
            try {
                Reader rdr = getReader(input);
                PrintWriter wtr = getWriter(input);
                translate = ActivitiesAndActionsFactory.createActivity()
                        .setMethod(() -> convert2textile(rdr, wtr, err, FileUtil.toFile(input)))
                        .needsIOTab(IOTABNAME);
            } catch (IOException | ApplicationException ex) {
                err.exception(ex);
                return;
            }
            translate.run("Converting " + input.getName() + ".html");
        }
    }

    private void convert2textile(Reader from, PrintWriter textilewriter, ErrHandler err, File inputfile) {
        try (from; textilewriter) {
            Html2Textile.convert(from, textilewriter, err, inputfile);
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
            err.exception(ex);
        }
    }

    private Reader getReader(FileObject input) throws FileNotFoundException {
        return new InputStreamReader(input.getInputStream());
    }

    private PrintWriter getWriter(FileObject input) throws IOException {
        FileObject parent = input.getParent();
        FileObject target = parent.getFileObject(input.getName(), "textile");
        if (target != null) {
            target.delete();
        }
        return new PrintWriter(new OutputStreamWriter(parent.createAndOpen(input.getName() + ".textile")));
    }
}
