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
package uk.theretiredprogrammer.actionssupport;

import java.util.Properties;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;

public class CLICommand {

    private DynamicCLIAction parent;

    public static enum InputOptions {
        NONE, FILE, UI
    };

    private final FileObject dir;
    private String label;
    private FileObject inputfile;
    private InputOptions inopt = InputOptions.NONE;
    private String cliCommandLine;
    private Action[] actions;

    public CLICommand(FileObject dir, String label) {
        this.dir = dir;
        this.label = label;
    }

    public CLICommand(FileObject dir, Properties properties, int prefix) {
        this.dir = dir;
        insertActionParameters(properties, Integer.toString(prefix));
    }

    public CLICommand setParent(DynamicCLIAction parent) {
        this.parent = parent;
        return this;
    }

    public CLICommand cliCommandLine(String cliCommandLine) {
        this.cliCommandLine = cliCommandLine.replace("${NODEPATH}", FileUtil.toFile(dir).getPath());
        return this;
    }

    public CLICommand inputfromFile(FileObject inputfile) {
        this.inputfile = inputfile;
        this.inopt = InputOptions.FILE;
        return this;
    }

    public CLICommand inputfromUI() {
        this.inputfile = null;
        this.inopt = InputOptions.UI;
        return this;
    }

    public CLICommand noinput() {
        this.inputfile = null;
        this.inopt = InputOptions.NONE;
        return this;
    }

    public CLICommand actions(Action[] actions) {
        this.actions = actions;
        return this;
    }

    public FileObject getDir() {
        return dir;
    }

    public String getLabel() {
        return label;
    }

    public FileObject getInputfile() {
        return inputfile;
    }

    public InputOptions getInopt() {
        return inopt;
    }

    public String getCliCommandLine() {
        return cliCommandLine;
    }

    public Action[] getActions() {
        return actions;
    }

    // extract clicommand values from properties
    private void insertActionParameters(Properties properties, String prefix) {
        label = properties.getProperty(prefix + ".label");
        String cmdline = properties.getProperty(prefix + ".command");
        if (cmdline != null) {
            cliCommandLine(cmdline);
        }
        String inputfrom = properties.getProperty(prefix + ".inputfrom");
        if (inputfrom != null) {
            switch (inputfrom.toLowerCase()) {
                case "file":
                    String inputfilename = properties.getProperty(prefix + ".inputfile");
                    if (inputfilename != null) {
                        inputfromFile(dir.getFileObject(inputfilename, null));
                    }
                    break;
                case "ui":
                    inputfromUI();
                    break;
                case "noinput":
                    this.noinput();
            }
        }
        String sbactions = properties.getProperty(prefix + ".sidebaractions");
        if (sbactions != null) {
            String[] sidebaractions = sbactions.split(",");
            actions = new Action[sidebaractions.length];
            for (int i = 0; i < sidebaractions.length; i++) {
                actions[i] = getAction(sidebaractions[i].trim());
            }
        }
    }

    private Action getAction(String key) {
        switch (key) {
            case "close":
                return new SideBarAction_Close();
            case "cancel":
                return new SideBarAction_Cancel();
        }
        return new SideBarAction_BAD();
    }

    private class SideBarAction_Close extends DynamicAction {

        SideBarAction_Close() {
            super();
            icon(ImageUtilities.loadImageIcon("uk/theretiredprogrammer/actionssupport/accept.png", false));
            onAction(() -> closeSTDIN());
            enable();
        }

        private void closeSTDIN() {
            parent.issueClose();
        }

    }

    private class SideBarAction_Cancel extends DynamicAction {

        SideBarAction_Cancel() {
            super();
            icon(ImageUtilities.loadImageIcon("uk/theretiredprogrammer/actionssupport/cancel.png", false));
            onAction(() -> cancel());
            enable();
        }

        private void cancel() {
            parent.issueCancel();
        }
    }

    private class SideBarAction_BAD extends DynamicAction {

        SideBarAction_BAD() {
            super();
            icon(ImageUtilities.loadImageIcon("uk/theretiredprogrammer/actionssupport/thumb_down.png", false));
            enable();
        }
    }

}
