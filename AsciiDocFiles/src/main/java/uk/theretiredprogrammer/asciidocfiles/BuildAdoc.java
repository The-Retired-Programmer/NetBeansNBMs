/*
 * Copyright 2022 Richard Linsdale.
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
package uk.theretiredprogrammer.asciidocfiles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import uk.theretiredprogrammer.actionssupport.CLIExec;

@ActionID(
        category = "Build",
        id = "uk.theretiredprogrammer.asciidocfiles.BuildAdoc"
)
@ActionRegistration(
        displayName = "#CTL_BuildAdoc"
)
@ActionReference(path = "Loaders/text/x-asciidoc/Actions", position = 0)
@Messages("CTL_BuildAdoc=Build")
public final class BuildAdoc implements ActionListener {

    private final List<DataObject> context;

    public BuildAdoc(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        for (DataObject dataObject : context) {
            try {
                FileObject input = dataObject.getPrimaryFile();
                TargetLocation targetlocation = new TargetLocation(input);
                new CLIExec(input, "asciidoctor -d article -a toc2 -o - -")
                        .stdin(input)
                        .stdout(targetlocation.get("html"))
                        .stderr(targetlocation.get("errlog"))
                        .execute();
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
