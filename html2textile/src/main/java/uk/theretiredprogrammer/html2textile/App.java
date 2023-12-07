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
import java.io.FileWriter;
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

    private ErrHandler err;
    private boolean intermediatehtml = false;
    private boolean htmlonly = false;
    private final List<String> inputfilenames = new ArrayList<>();

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
                case "-i" ->
                    intermediatehtml = true;
                case "-h" ->
                    htmlonly = true;
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
        try {
            try {
                return filename.endsWith(".transformed.html") ? convertTextileOnly(filename)
                        : (htmlonly ? convertHtmlOnly(filename)
                                : (intermediatehtml ? convertHtmlAndTextileSavingIntermediateHtml(filename)
                                        : convertHtmlAndTextile(filename)));
            } catch (IOException ioex) {
                err.exception("", ioex);
                return 4;
            }

        } catch (Throwable ex) {
            err.exception("", ex);
            return 8;
        }
    }

    private int convertHtmlAndTextileSavingIntermediateHtml(String filename) throws IOException {
        String intermediatefilename = getfilenoext(filename) + ".transformed.html";
        String outputfilename = getfilenoext(filename) + ".textile";
        try ( Reader rdr = getInputReader(filename);  PrintWriter wtr = getOutputWriter(outputfilename);  FileWriter ihtml = new FileWriter(intermediatefilename)) {
            Html2Textile h2t = new Html2Textile();
            h2t.convertor(rdr, wtr, err, new File(filename).getAbsoluteFile(), ihtml);
            return 0;
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
            err.error("Exception: " + ex.getLocalizedMessage());
            return 4;
        }
    }

    private int convertHtmlAndTextile(String filename) throws IOException {
        String outputfilename = getfilenoext(filename) + ".textile";
        try ( Reader rdr = getInputReader(filename);  PrintWriter wtr = getOutputWriter(outputfilename)) {
            Html2Textile h2t = new Html2Textile();
            h2t.convertor(rdr, wtr, err, new File(filename).getAbsoluteFile(), null);
            return 0;
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
            err.error("Exception: " + ex.getLocalizedMessage());
            return 4;
        }
    }

    private int convertHtmlOnly(String filename) throws IOException {
        String intermediatefilename = getfilenoext(filename) + ".transformed.html";
        try ( Reader rdr = getInputReader(filename);  FileWriter ihtml = new FileWriter(intermediatefilename)) {
            Html2Textile h2t = new Html2Textile();
            h2t.htmlonlyconvertor(rdr, ihtml, err, new File(filename).getAbsoluteFile());
            return 0;
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
            err.error("Exception: " + ex.getLocalizedMessage());
            return 4;
        }
    }

    private int convertTextileOnly(String filename) throws IOException {
        String outputfilename = getintermediatefilenoext(filename) + ".textile";
        try ( Reader rdr = getInputReader(filename);  PrintWriter wtr = getOutputWriter(outputfilename)) {
            Html2Textile h2t = new Html2Textile();
            h2t.textileonlyconvertor(rdr, wtr, err, new File(filename).getAbsoluteFile());
            return 0;
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
            err.error("Exception: " + ex.getLocalizedMessage());
            return 4;
        }
    }

    private String getfilenoext(String filename) throws IOException {
        if (filename.endsWith(".fragment.html")) {
            int pos = filename.lastIndexOf(".fragment.html");
            if (pos != -1) {
                return filename.substring(0, pos);
            }
        }
        throw new IOException("Input to HTML phase must have an .fragment.html extension");
    }

    private String getintermediatefilenoext(String filename) throws IOException {
        if (filename.endsWith(".transformed.html")) {
            int pos = filename.lastIndexOf(".transformed.html");
            if (pos != -1) {
                return filename.substring(0, pos);
            }
        }
        throw new IOException("Input to Textile phase must have an .transformed.html extension)");
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
               
                -i     output intermediate html (if running both phases)
                
                -h     run html phase only
                                
                initial state without any options is no intermediate html,dont run html phase only  
                
            and INPUTFILES are the source file(s): html OR html fragments (*_fragment.html) - html phase input only
                    
        """;
}
