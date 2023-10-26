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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import static uk.theretiredprogrammer.html2textile.Html2Textile.IGNORE_ALL_SYSTEM_RULES;
import static uk.theretiredprogrammer.html2textile.Html2Textile.IGNORE_HTML_SYSTEM_RULES;
import static uk.theretiredprogrammer.html2textile.Html2Textile.IGNORE_NO_SYSTEM_RULES;
import static uk.theretiredprogrammer.html2textile.Html2Textile.IGNORE_STYLE_SYSTEM_RULES;
import static uk.theretiredprogrammer.html2textile.Html2Textile.IGNORE_TEXTILE_SYSTEM_RULES;

public class App {

    private final PrintWriter err = new PrintWriter(System.err);
    private int ignoresystemrules = IGNORE_NO_SYSTEM_RULES;
    private String outputfilename = null;
    private String filename = null;

    public App() {
    }

    public void go(String[] args) {
        err.print("Html2Textile: ");
        for ( String arg: args){
            err.print(arg+" ");
        }
        err.println();
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
            err.println(help);
            return 0;
        }
        int i = 0;

        while (i < l) {
            switch (args[i]) {
                
                case "-x" ->
                    ignoresystemrules = IGNORE_ALL_SYSTEM_RULES;
                case "-xh" ->
                    ignoresystemrules |= IGNORE_HTML_SYSTEM_RULES;
                case "-xs" ->
                    ignoresystemrules |= IGNORE_STYLE_SYSTEM_RULES;
                case "-xt" ->
                    ignoresystemrules |= IGNORE_TEXTILE_SYSTEM_RULES;
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
        if (!new File(filename).canRead()) {
            err.println("INPUTFILE is not available to read");
            return 4;
        }
        return -1;
    }

    private String getfilenoext() {
        int pos = filename.lastIndexOf(".");
        return pos == -1 ? filename : filename.substring(0, pos);
    }

    private int run() {
        try {
            try ( Reader rdr = getInputReader();  PrintWriter wtr = getOutputWriter()) {
                Html2Textile.convert(rdr, wtr, err, new File(filename).getAbsoluteFile(),ignoresystemrules);
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

    private InputStream getInputStream(String filename) throws FileNotFoundException {
        return new FileInputStream(new File(filename));
    }

    private OutputStream getOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(new File(filename));
    }

    private String help = """
            Html2Textile
            
            <command> <options> INPUTFILE
            
            where command is:
                
                "java -jar html2textile-n.n.n.jar" or its alias "html2textile"
                
            options are:
               
                -x              do not use any of the system rules files
                
                -xh             do not use the Html system rules file
                                
                -xs             do not use the Style system rules file
                                
                -xt             do not use the Textile system rules file
                                
                -o              set the OUTPUTFILE (textile content).  If not present the OUTPUTFILE
                                defaults to the INPUTFILE's pair textile file (<inputfilename without ext>.textile)
                
            and INPUTFILE is the source html file (html fragment) which is to be converted.
        """;
}
