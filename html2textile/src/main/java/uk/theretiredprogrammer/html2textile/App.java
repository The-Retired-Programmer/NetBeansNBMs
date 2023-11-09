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
import static uk.theretiredprogrammer.html2textile.Html2Textile.IGNORE_ALL_SYSTEM_RULES;
import static uk.theretiredprogrammer.html2textile.Html2Textile.IGNORE_HTML_SYSTEM_RULES;
import static uk.theretiredprogrammer.html2textile.Html2Textile.IGNORE_NO_SYSTEM_RULES;
import static uk.theretiredprogrammer.html2textile.Html2Textile.IGNORE_STYLE_SYSTEM_RULES;
import static uk.theretiredprogrammer.html2textile.Html2Textile.IGNORE_TEXTILE_SYSTEM_RULES;

public class App {

    private ErrHandler err;
    private int systemrulescontrols = IGNORE_NO_SYSTEM_RULES;
    private final List<String> inputfilenames = new ArrayList<>();

    public App() {
    }

    public void go(String[] args) {
        System.exit(goInner(args));
    }

    public int goInner(String[] args) {
        int rc;
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            err = new ErrHandler((s) -> errwriter.println(s));
            if ((rc = extractFromCLI(args)) >= 0) {
                return rc;
            }
            for (String filename : inputfilenames) {
                err.info("\nHtml2Textile " + filename);
                if ((rc = run(filename)) > 0) {
                    return rc;
                }
            }
            return rc;
        }
    }

    private int extractFromCLI(String[] args) {
        int l = args.length;
        if (l == 0) {
            err.info(help);
            return 0;
        }
        int i = 0;

        while (i < l) {
            switch (args[i]) {

                case "-x" ->
                    systemrulescontrols = IGNORE_ALL_SYSTEM_RULES;
                case "-xh" ->
                    systemrulescontrols |= IGNORE_HTML_SYSTEM_RULES;
                case "-xs" ->
                    systemrulescontrols |= IGNORE_STYLE_SYSTEM_RULES;
                case "-xt" ->
                    systemrulescontrols |= IGNORE_TEXTILE_SYSTEM_RULES;
                default -> {
                    while (i < l) {
                        inputfilenames.add(args[i++]);
                    }
                }
            }
            i++;
        }
        if (inputfilenames.isEmpty()) {
            err.error("INPUTFILE(S) not defined on command line");
            return 4;
        }
        return -1;
    }

    private int run(String filename) {
        String outputfilename = getfilenoext(filename) + ".textile";
        try {
            try ( Reader rdr = getInputReader(filename);  PrintWriter wtr = getOutputWriter(outputfilename)) {
                Html2Textile.convert(rdr, wtr, err, new File(filename).getAbsoluteFile(), systemrulescontrols);
                return 0;
            } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
                err.error("Exception: " + ex.getLocalizedMessage());
                return 4;
            }
        } catch (Throwable ex) {
            err.exception("", ex);
            return 8;
        }
    }

    private String getfilenoext(String filename) {
        int pos = filename.lastIndexOf(".");
        return pos == -1 ? filename : filename.substring(0, pos);
    }

    private Reader getInputReader(String filename) throws FileNotFoundException {
        return new InputStreamReader(getInputStream(filename));
    }

    private PrintWriter getOutputWriter(String filename) throws FileNotFoundException {
        return new PrintWriter(getOutputStream(filename));
    }

    private InputStream getInputStream(String filename) throws FileNotFoundException {
        return new FileInputStream(new File(filename));
    }

    private OutputStream getOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(new File(filename));
    }

    private final String help = """
        
        Html2Textile
            
            <command> <options> INPUTFILES...
            
            where command is:
                
                "java -jar html2textile-n.n.n.jar" or its alias "html2textile"
                
            options are:
               
                -x              do not use any of the system rules files
                
                -xh             do not use the Html system rules file
                                
                -xs             do not use the Style system rules file
                                
                -xt             do not use the Textile system rules file
                
            and INPUTFILES is the source html files (html fragment) which are to be converted.
        """;
}
