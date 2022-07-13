/*
 * Copyright 2022 richard linsdale.
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
package uk.theretiredprogrammer.actionssupport.implementation;

import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.io.InputOutput;
import org.netbeans.api.io.OutputWriter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

public class CLICommand implements Runnable {

    private final String name;
    private final String label;
    private final String command;
    private final FileObject folder;

    public CLICommand(FileObject folder, String name, String label, String command) {
        this.folder = folder;
        this.name = name;
        this.label = label;
        this.command = command;
    }

    public final void execute() {
        new RequestProcessor(CLICommand.class).post(this);
    }

    @Override
    public void run() {
        boolean errflag = false;
        long start = currentTimeMillis();
        InputOutput io = InputOutput.get(name + " - " + label, false);
        io.show();
        io.reset();
        try ( OutputWriter msg = io.getOut();  OutputWriter err = io.getErr()) {
            ProcessExecutor pexec = new ProcessExecutor(parse2words(command));
            pexec.setDisplayName(label);
            pexec.setWorkingDirectory(FileUtil.toFile(folder));
            pexec.setErrorLineFunction(s -> err.println(s));
            pexec.setOutputLineFunction(s -> msg.println(s));
            pexec.execute();
            msg.flush();
            err.flush();
            int elapsed = round((currentTimeMillis() - start) / 1000F);
            msg.println("COMMAND COMPLETED" + (errflag ? " WITH ERRORS" : "") + " (total time: " + Integer.toString(elapsed) + " seconds)");
        }
    }

    private enum State {
        WHITESPACE, INQUOTED, BASIC, DONE
    };

    String[] parse2words(String command) {
        List<String> wordlist = new ArrayList<>();
        StringBuilder wordbuilder = new StringBuilder();
        CharProvider chars = new CharProvider(command.trim());
        State state = State.WHITESPACE;
        while (true) {
            char nextc = chars.nextchar();
            switch (state) {
                case WHITESPACE:
                    switch (nextc) {
                        case '\0':
                            state = State.DONE;
                            break;
                        case ' ':
                            break;
                        case '"':
                            state = State.INQUOTED;
                            wordbuilder.setLength(0);
                            break;
                        default:
                            state = State.BASIC;
                            wordbuilder.setLength(0);
                            wordbuilder.append(nextc);
                            break;
                    }
                    break;
                case INQUOTED:
                    switch (nextc) {
                        case '\0':
                            //ignore missing trailing quote
                            if (!wordbuilder.isEmpty()) {
                                wordlist.add(wordbuilder.toString());
                            }
                            state = State.DONE;
                            break;
                        case ' ':
                            wordbuilder.append(nextc);
                            break;
                        case '"':
                            state = State.WHITESPACE;
                            if (!wordbuilder.isEmpty()) {
                                wordlist.add(wordbuilder.toString());
                            }
                            break;
                        default:
                            wordbuilder.append(nextc);
                            break;
                    }
                    break;
                case BASIC:
                    switch (nextc) {
                        case '\0':
                            if (!wordbuilder.isEmpty()) {
                                wordlist.add(wordbuilder.toString());
                            }
                            state = State.DONE;
                            break;
                        case ' ':
                            state = State.WHITESPACE;
                            if (!wordbuilder.isEmpty()) {
                                wordlist.add(wordbuilder.toString());
                            }
                            break;
                        case '"':
                            wordbuilder.append(nextc);
                            break;
                        default:
                            wordbuilder.append(nextc);
                            break;
                    }
                    break;
                case DONE:
                    String[] words = new String[wordlist.size()];
                    int i = 0;
                    for (String word : wordlist) {
                        words[i++] = word.trim();
                    }
                    return words;
            }
        }
    }

    private class CharProvider {

        private final String source;
        private int nextpos;

        CharProvider(String source) {
            this.source = source;
            this.nextpos = 0;
        }

        char peek() {
            return rdr(nextpos);
        }

        char nextchar() {
            return rdr(nextpos++);
        }

        private char rdr(int index) {
            return index >= source.length() ? '\0' : source.charAt(index);
        }
    }
}
