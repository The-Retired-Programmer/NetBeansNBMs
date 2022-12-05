/*
 * Copyright 2022 Richard Linsdale
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;

public class IOCOMMANDS extends ProcessIO<InputStream, BufferedReader> {

    private Task task = null;
    private boolean inuse = false;

    private Reader inputreader = null;
    private final Map<String, CommandPair> commands = new HashMap<>();

    public IOCOMMANDS(Logging logging) {
        super(logging);
    }
    
    public IOCOMMANDS(IOCOMMANDS source) {
        super(source);
        this.task = source.task;
        this.inuse = source.inuse;
        this.inputreader = source.inputreader;
        commands.putAll(source.commands);
    }

    public void addCommands(Map<String, CommandPair> commands) {
        this.commands.putAll(commands);
    }

    public void addCommand(String command, Runnable action, boolean terminating) {
        this.commands.put(command, new CommandPair(action,terminating));
    }

    public void setInputreader(Reader inputreader) {
        this.inputreader = inputreader;
    }

    @Override
    public void startTransfer(Supplier<InputStream> streamSupplier, Supplier<BufferedReader> rwSupplier) {
        if (!commands.isEmpty() && inputreader != null) {
            inuse = true;
            RequestProcessor processor = new RequestProcessor("commands");
            task = processor.post(() -> commandsTransfer());
        }
    }

    @Override
    public void waitFinished(long timeout) throws InterruptedException {
        if (inuse) {
            task.waitFinished(timeout);
        }
    }

    @Override
    public void close(Process process) {
        try {
            if (inuse) {
                inputreader.close();
            }
        } catch (IOException ex) {
            logging.warning("Closing IOCOMMANDS " + ex);
        }
    }

    @SuppressWarnings("SleepWhileInLoop")
    private void commandsTransfer() {
        try {
            BufferedReader inputlinereader = new BufferedReader(inputreader);
            String commandline;
            while ((commandline = inputlinereader.readLine()) != null) {
                if (processCommands(commandline)) {
                    return; // was a terminating command
                }
            }
        } catch (IOException ex) {
            logging.severe("Could not read IOCOMMANDS " + ex);
        }
    }

    private boolean processCommands(String commandline) {
        commandline = commandline.toLowerCase();
        CommandPair command = commands.get(commandline);
        if (command == null) {
            logging.user("unknown command");
            return false;
        }
        command.commandaction.run();
        return command.terminating;
    }
    
    public class CommandPair {

        public final Runnable commandaction;
        public final boolean terminating;
        
        public CommandPair(Runnable commandaction, boolean terminating) {
            this.commandaction = commandaction;
            this.terminating = terminating;
        }
    }
}
