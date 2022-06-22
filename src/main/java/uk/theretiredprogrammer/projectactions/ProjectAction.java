/*
 * Copyright 2015-2022 Richard Linsdale.
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
package uk.theretiredprogrammer.projectactions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class ProjectAction extends AbstractAction {

    private String command;
    private String label;
    private final String projectname;
    private final String projectpath;

    public ProjectAction(FileObject projectdir, String label, String command) {
        this.projectpath = FileUtil.toFile(projectdir).getPath();
        this.projectname = projectdir.getName();
        this.label = label;
        this.command = command;
        setupAction();
    }

    public ProjectAction(FileObject projectdir) {
        this.projectpath = FileUtil.toFile(projectdir).getPath();
        this.projectname = projectdir.getName();
        this.label = null;
        this.command = null;
        setupAction();
    }

    public final void setCommand(String label, String command) {
        this.label = label;
        this.command = command;
        setupAction();
    }
    
    public final void clearCommand() {
        this.label = null;
        this.command = null;
        setupAction();
    }

    private void setupAction() {
        setEnabled(label != null);
        putValue(DynamicMenuContent.HIDE_WHEN_DISABLED, true);
        putValue("popupText", label);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new CLICommand(projectname, label, substitute(command)).execute();
    }

    private String substitute(String command) {
        return command.replace("${PROJECTPATH}", projectpath);
    }
}
