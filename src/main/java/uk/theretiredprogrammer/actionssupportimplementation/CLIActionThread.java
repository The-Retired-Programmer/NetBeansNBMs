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
package uk.theretiredprogrammer.actionssupportimplementation;

import uk.theretiredprogrammer.actionssupport.CLICommand;
import java.io.IOException;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileUtil;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import static uk.theretiredprogrammer.actionssupport.CLICommand.InputOptions.FILE;
import static uk.theretiredprogrammer.actionssupport.CLICommand.InputOptions.UI;

public class CLIActionThread extends Thread {

    private final CLICommand cliCommand;
    private OutputWriter errorreporter;
    private ToProcessThread stdin = null;
    private Process process;

    public CLIActionThread(CLICommand cliCommand) {
        super("CLI Action Thread");
        this.cliCommand = cliCommand;
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        long start = currentTimeMillis();

        InputOutput io = IOProvider.get("output2").getIO(cliCommand.getDir().getName() + " - " + cliCommand.getLabel(), false,
                cliCommand.getActions(), null);
        io.select();
        if (cliCommand.getInopt() == UI) {
            io.setInputVisible(true);
        }
        try ( OutputWriter msg = io.getOut();  OutputWriter err = io.getErr()) {
            try {
                msg.reset();
                errorreporter = err;
                ProcessBuilder pb = new ProcessBuilder(parse2words(cliCommand.getCliCommandLine()))
                        .directory(FileUtil.toFile(cliCommand.getDir()));
                if (cliCommand.getInopt() == FILE) {
                    pb.redirectInput(FileUtil.toFile(cliCommand.getInputfile()));
                }
                FromProcessThread stderr;
                FromProcessThread stdout;
                process = pb.start();
                stderr = new FromProcessThread("stderr", process.errorReader(), err, (p, e) -> write2err(p, e));
                stderr.start();
                if (cliCommand.getInopt() == UI) {
                    stdin = new ToProcessThread("stdin", io.getIn(), process.outputWriter(), (p, e) -> write2err(p, e));
                    stdin.start();
                }
                stdout = new FromProcessThread("stdout", process.inputReader(), msg, (p, e) -> write2err(p, e));
                stdout.start();
                process.waitFor();
                //
                if (stdout.isAlive()) {
                    stdout.end();
                    stdout.join();
                }
                if (stdin != null && stdin.isAlive()) {
                    stdin.end();
                    stdin.join();
                }
                if (stderr.isAlive()) {
                    stderr.end();
                    stderr.join();
                }
            } catch (InterruptedException ex) {
                write2err(getName() + " interrupted", ex.getLocalizedMessage());
            } catch (IOException ex) {
                write2err(getName() + " IO error", ex.getLocalizedMessage());
            }
            int elapsed = round((currentTimeMillis() - start) / 1000F);
            msg.println("COMMAND COMPLETED (total time: " + Integer.toString(elapsed) + " seconds)");
        }
    }

    private void write2err(String phase, String exmsg) {
        errorreporter.println("Error: when " + phase + "; " + exmsg);
    }

    public void inputClose() {
        if (stdin != null) {
            stdin.close();
        }
    }

    public void cancel() {
        process.destroy();
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

        char nextchar() {
            return rdr(nextpos++);
        }

        private char rdr(int index) {
            return index >= source.length() ? '\0' : source.charAt(index);
        }
    }
}
