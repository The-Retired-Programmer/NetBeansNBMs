/*
 * Copyright 2023 richard linsdale.
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
package uk.theretiredprogrammer.epubconversion;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Document;
import uk.theretiredprogrammer.util.UserReporting;

public class EPUBConvertor {

    public static void convertHTML(FileObject projectdir, FileObject epubfile,
            FileObject input, FileObject outfolder, String iotabname) {
        FileObject hintsfo = epubfile.getParent().getFileObject(epubfile.getName(), "hints");
        FileObject xsltfo = projectdir.getFileObject("transform.xsl");
        Writer output = createOutput(input, outfolder, iotabname);
        if (output == null) {
            return;
        }
        String document;
        try {
            document = input.asText();
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage(iotabname, "failure while converting document - reading .xhtml file", ex);
            return;
        }
        if (hintsfo != null) {
            UserReporting.info(iotabname, "Apply Hints");
            try {
                document = regex(document, hintsfo, iotabname);
            } catch (IOException ex) {
                UserReporting.exceptionWithMessage(iotabname, "failure while converting document- when applying hints", ex);
                return;
            }
        } else {
            UserReporting.warning(iotabname, "Hints file is missing - please create before attemping to Convert");
            return;
        }
        if (xsltfo == null) {
            UserReporting.info(iotabname, "Apply Standard Transformation");
            try ( StringWriter xsltoutputwtr = new StringWriter();  Reader xsltrdr = new InputStreamReader(EPUBConvertor.class.getResourceAsStream("transform.xsl"))) {
                xslttransform(iotabname, new StringReader(document), xsltoutputwtr, xsltrdr);
                document = xsltoutputwtr.toString();
            } catch (IOException ex) {
                UserReporting.exceptionWithMessage(iotabname, "failure while converting document- when applying transformation", ex);
                return;
            }
        } else {
            UserReporting.info(iotabname, "Apply Custom Transformation");
            try ( StringWriter xsltoutputwtr = new StringWriter();  Reader xsltrdr = new InputStreamReader(xsltfo.getInputStream())) {
                xslttransform(iotabname, new StringReader(document), xsltoutputwtr, xsltrdr);
                document = xsltoutputwtr.toString();
            } catch (IOException ex) {
                UserReporting.exceptionWithMessage(iotabname, "failure while converting document- when applying transformation", ex);
                return;
            }
        }
        try (output) {
            output.write(document);
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage(iotabname, "failure while converting document - write to file phase", ex);
        }
    }

    private static Writer createOutput(FileObject fromfile, FileObject outfolder, String iotabname) {
        try {
            String outfilename = fromfile.getName() + ".html";
            FileObject out = outfolder.getFileObject(outfilename);
            if (out != null) {
                out.delete();
            }
            return new OutputStreamWriter(outfolder.createAndOpen(outfilename));
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Failed to setup output file", ex);
            return null;
        }
    }

    private static String regex(String input, FileObject regexlinesfo, String iotabname) throws IOException {
        String document = input;
        for (String line : regexlinesfo.asLines()) {
            line = line.trim();
            if (!(line.isBlank() || line.startsWith("#"))) {
                if (line.endsWith("==>")) {
                    document = regextransform(document, line.substring(0, line.length() - 3).trim(), "");
                } else {
                    String[] segments = line.split("==>");
                    if (segments.length != 2) {
                        UserReporting.warning(iotabname, "regex definition line does not parse: " + line);
                    } else {
                        segments[0] = segments[0].trim();
                        segments[1] = segments[1].trim();
                        document = regextransform(document, segments[0], segments[1]);
                    }
                }
            }
        }
        return document;
    }
    
    private static String regextransform(String from, String finding, String replacing) {
        Pattern pattern = Pattern.compile(finding);
        Matcher matcher = pattern.matcher(from);
        return matcher.replaceAll(replacing);
    }
    
    private static void xslttransform(String iotabname, Reader from, Writer to, Reader using) {
        try {
            try (from; using; to) {
                Transformer tr;
                tr = TransformerFactory.newInstance().newTransformer(new StreamSource(using));
                DOMResult dr = new DOMResult();
                tr.transform(new StreamSource(from), dr);
                //
                tr = TransformerFactory.newInstance().newTransformer();
                tr.transform(new DOMSource((Document) dr.getNode()), new StreamResult(to));
            }
        } catch (TransformerException | IOException ex) {
            UserReporting.exceptionWithMessage(iotabname, "XSLT transform failed", ex);
        }
    }
}
