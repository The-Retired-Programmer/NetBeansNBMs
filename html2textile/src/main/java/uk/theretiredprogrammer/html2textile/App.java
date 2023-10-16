/*
 * Copyright 2023 richard.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class App {

    private final PrintWriter err = new PrintWriter(System.err);
    private String rulesfilename = null;
    private String commonrulesfilename = null;
    private boolean usecommonrules = false;
    private boolean usepairedrules = false;
    private String outputfilename = null;
    private String filename = null;

    public App() {
    }

    public void go(String[] args) {
        System.exit(goInner(args));
    }

    public int goInner(String[] args) {
        int rc;
        try (err) {
            if ((rc = extractFromCLI(args)) >= 0) {
                return rc;
            }
            return run();
        }
    }

    private int extractFromCLI(String[] args) {
        int l = args.length;
        if (l == 0) {
            err.println(getHelp());
            return 0;
        }
        int i = 0;

        while (i < l) {
            switch (args[i]) {
                case "-r" -> {
                    if (i++ < l) {
                        rulesfilename = args[i];
                    } else {
                        err.println("missing argument after -r");
                        return 4;
                    }
                }
                case "-uc" ->
                    usecommonrules = true;
                case "-ur" ->
                    usepairedrules = true;
                case "-o" -> {
                    if (i++ < l) {
                        outputfilename = args[i];
                    } else {
                        err.println("missing argument after -o");
                        return 4;
                    }
                }
                default -> {
                    filename = args[i];
                    if (i != l - 1) {
                        err.println("excess arguments after filename (" + filename + ")");
                        return 4;
                    }
                }
            }
            i++;
        }
        if (filename == null) {
            err.println("INPUTFILE not defined on command line");
            return 4;
        }
        if (outputfilename == null) {
            outputfilename = getfilenoext() + ".textile";
        }
        if (rulesfilename == null && usepairedrules) {
            rulesfilename = getfilenoext() + ".rules";
        }
        if (usecommonrules) {
            commonrulesfilename = getfileparent() + "common.rules";
        }
        if (!new File(filename).canRead()) {
            err.println("INPUTFILE is not available to read");
            return 4;
        }
        if (rulesfilename != null && !new File(rulesfilename).canRead()) {
            err.println("Rules File is not available to read");
            return 4;
        }
        if (commonrulesfilename != null && !new File(commonrulesfilename).canRead()) {
            err.println("Rules File is not available to read");
            return 4;
        }
        return -1;
    }

    private String getfilenoext() {
        int pos = filename.lastIndexOf(".");
        return pos == -1 ? filename : filename.substring(0, pos);
    }

    private String getfileparent() {
        int pos = filename.lastIndexOf("/");
        return pos == -1 ? "" : filename.substring(0, pos + 1);
    }

    private int run() {
        try {
            try ( Reader rdr = getInputReader();  PrintWriter wtr = getOutputWriter()) {
                Html2Textile.convert(rdr, wtr, err, getRules());
                return 0;
            } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
                err.println("Exception: " + ex.getLocalizedMessage());
                return 4;
            }
        } catch (Throwable ex) {
            err.println("System Exception: " + ex.getLocalizedMessage());
            return 8;
        }
    }

    private Reader getInputReader() throws FileNotFoundException {
        return new InputStreamReader(getInputStream(filename));
    }

    private PrintWriter getOutputWriter() throws FileNotFoundException {
        return new PrintWriter(getOutputStream(outputfilename));
    }

    private List<InputStream> getRules() throws FileNotFoundException {
        List<InputStream> rules = new ArrayList<>();
        if (rulesfilename != null) {
            rules.add(getInputStream(rulesfilename));
        }
        if (commonrulesfilename != null) {
            rules.add(getInputStream(commonrulesfilename));
        }
        return rules;
    }
    
    private InputStream getInputStream(String filename) throws FileNotFoundException {
         return new FileInputStream(new File(filename));
    }
    
    private OutputStream getOutputStream(String filename) throws FileNotFoundException {
         return new FileOutputStream(new File(filename));
    }

    private String getHelp() {
        return """
               
            Html2Textile
            
            <command> <options> INPUTFILE
            
            where command is:
                
                "java -jar html2textile-n.n.n.jar" or its alias "html2textile"
                
            options are:
                
                -r  FILENAME    add this rule file (implies -xc and -xf)
                
                -xc             ignore the folder's common rules file (common.rule)
                
                -xf             ignore the INPUTFILE's pair rules file (<inputfilename without ext>.rules)
                
                -o              set the OUTPUTFILE (textile content).  If not present the OUTPUTFILE
                                defaults to the INPUTFILE's pair textile file (<inputfilename without ext>.textile)
                
            and INPUTFILE is the source html file (html fragment) which is to be converted.
        """;
    }
}
