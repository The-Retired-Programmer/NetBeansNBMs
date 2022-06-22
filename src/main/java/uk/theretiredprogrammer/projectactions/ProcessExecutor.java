/*
 * Copyright 2019 - 2022 richard linsdale.
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A process executor which provides a wrapper class around the ProcessBuilder
 * to provides an improved class for providing external process execution.
 *
 * @author richard linsdale
 */
public class ProcessExecutor {

    private final ProcessBuilder pb;
    private Consumer<String> errorlinehandler = null;
    private String name = "Command";
    private Consumer<String> readlinehandler = null;
    private Supplier<String> writelinehandler = null;

    /**
     * Constructor
     *
     * @param commandline a series of command line components
     */
    public ProcessExecutor(String... commandline) {
        pb = new ProcessBuilder(commandline);
    }

    /**
     * Set the display name of this process executor (used in error messages)
     *
     * @param name the name
     */
    public void setDisplayName(String name) {
        this.name = name;
    }

    /**
     * Set the function which will be used to process error messages (both
     * STDERR and class error messages).
     *
     * @param errorlinehandler the error handling function (Consumer of String)
     */
    public void setErrorLineFunction(Consumer<String> errorlinehandler) {
        this.errorlinehandler = errorlinehandler;
    }

    /**
     * Set the function which will be used to process output (STDOUT).
     *
     * @param readlinehandler the output handling function (Consumer of String)
     */
    public void setOutputLineFunction(Consumer<String> readlinehandler) {
        this.readlinehandler = readlinehandler;
    }

    /**
     * Set the function which will be used to generate input (STDIN).
     *
     * @param writelinehandler the input handling function (Supplier of String)
     */
    public void setInputLineFunction(Supplier<String> writelinehandler) {
        this.writelinehandler = writelinehandler;
    }

    /**
     * Execute the external process.
     */
    @SuppressWarnings("SleepWhileInLoop")
    protected void execute() {
        STDIN stdin;
        STDOUT stdout = null;
        STDERR stderr = null;
        try {
            Process process = pb.start();
            if (writelinehandler != null) {
                stdin = new STDIN(process.outputWriter(), writelinehandler);
                stdin.start();
            }
            if (readlinehandler != null && errorlinehandler != null) {
                stdout = new STDOUT(process.inputReader(), readlinehandler, errorlinehandler);
                stdout.start();
            }
            if (errorlinehandler != null) {
                stderr = new STDERR(process.errorReader(), errorlinehandler);
                stderr.start();
            }
            process.waitFor();
            while ((stdout!=null && stdout.isAlive() ) || (stderr!=null && stderr.isAlive())) {
                Thread.sleep(1);
            }
            
        } catch (InterruptedException | IOException ex) {
            errorlinehandler.accept("Error during " + name + " processing: " + ex.getLocalizedMessage());
        }
    }

    private class STDOUT extends Thread {

        private final Consumer<String> readlinehandler;
        private final Consumer<String> errorlinehandler;
        private final BufferedReader reader;

        STDOUT(BufferedReader reader, Consumer<String> readlinehandler, Consumer<String> errorlinehandler) {
            this.reader = reader;
            this.readlinehandler = readlinehandler;
            this.errorlinehandler = errorlinehandler;
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    readlinehandler.accept(line);
                }
            } catch (IOException ex) {
                errorlinehandler.accept("Error during " + name + " processing: " + ex.getLocalizedMessage());
            }
        }
    }

    private class STDERR extends Thread {

        private final Consumer<String> errorlinehandler;
        private final BufferedReader reader;

        STDERR(BufferedReader reader, Consumer<String> errorlinehandler) {
            this.reader = reader;
            this.errorlinehandler = errorlinehandler;
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    errorlinehandler.accept(line);
                }
            } catch (IOException ex) {
                errorlinehandler.accept("Error during " + name + " processing: " + ex.getLocalizedMessage());
            }
        }
    }

    private class STDIN extends Thread {

        private final Supplier<String> writelinehandler;
        private final BufferedWriter writer;

        STDIN(BufferedWriter writer, Supplier<String> writelinehandler) {
            this.writer = writer;
            this.writelinehandler = writelinehandler;
        }

        @Override
        public void run() {
            try ( PrintWriter pwriter = new PrintWriter(writer)) {
                String line;
                while ((line = writelinehandler.get()) != null) {
                    pwriter.println(line);
                }
            }
        }
    }
}
