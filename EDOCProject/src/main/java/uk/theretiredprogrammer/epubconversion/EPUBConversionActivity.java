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
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actionssupport.UserReporting;
import uk.theretiredprogrammer.activity.Activity;
import uk.theretiredprogrammer.activity.ActivityIO;

public class EPUBConversionActivity extends Activity {

    private final FileObject hintsfo;
    private final FileObject xsltfo;
    private final FileObject fromfile;
    private final FileObject outfolder;

    public EPUBConversionActivity(FileObject projectdir, FileObject epubfile,
            FileObject fromfile, FileObject outfolder, ActivityIO activityio) {
        super(activityio);
        this.hintsfo = epubfile.getParent().getFileObject(epubfile.getName(), "hints");
        this.xsltfo = projectdir.getFileObject("transform.xsl");
        this.fromfile = fromfile;
        this.outfolder = outfolder;
    }

    @Override
    public void onActivity() {
        conversion(fromfile, createOutput());
    }

    private Writer createOutput() {
        try {
            String outfilename = fromfile.getName() + ".html";
            FileObject out = outfolder.getFileObject(outfilename);
            if (out != null) {
                out.delete();
            }
            return new OutputStreamWriter(outfolder.createAndOpen(outfilename));
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage("EPUB", "Failed to setup output file", ex);
            return null;
        }
    }

    private void conversion(FileObject input, Writer output) {
        if (output == null) {
            return;
        }
        String document;
        try {
            document = input.asText();
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage(activityio.iotabname, "failure while converting document- reading .xhtml file", ex);
            return;
        }
        if (hintsfo != null) {
            UserReporting.info(activityio.iotabname, "Apply Hints");
            try {
                document = regex(document, hintsfo);
            } catch (IOException ex) {
                UserReporting.exceptionWithMessage(activityio.iotabname, "failure while converting document- when applying hints", ex);
                return;
            }
        } else {
            UserReporting.warning(activityio.iotabname, "Hints file is missing - please create before attemping to Convert");
            return;
        }
        if (xsltfo == null) {
            UserReporting.info(activityio.iotabname, "Apply Standard Transformation");
            try ( StringWriter xsltoutputwtr = new StringWriter();  Reader xsltrdr = new InputStreamReader(this.getClass().getResourceAsStream("transform.xsl"))) {
                XSLT.transform(activityio.iotabname, new StringReader(document), xsltoutputwtr, xsltrdr);
                document = xsltoutputwtr.toString();
            } catch (IOException ex) {
                UserReporting.exceptionWithMessage(activityio.iotabname, "failure while converting document- when applying transformation", ex);
                return;
            }
        } else {
            UserReporting.info(activityio.iotabname, "Apply Custom Transformation");
            try ( StringWriter xsltoutputwtr = new StringWriter();  Reader xsltrdr = new InputStreamReader(xsltfo.getInputStream())) {
                XSLT.transform(activityio.iotabname, new StringReader(document), xsltoutputwtr, xsltrdr);
                document = xsltoutputwtr.toString();
            } catch (IOException ex) {
                UserReporting.exceptionWithMessage(activityio.iotabname, "failure while converting document- when applying transformation", ex);
                return;
            }
        }
        try (output) {
            output.write(document);
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage(activityio.iotabname, "failure while converting document - write to file phase", ex);
        }
    }

    private String regex(String input, FileObject regexlinesfo) throws IOException {
        String document = input;
        for (String line : regexlinesfo.asLines()) {
            line = line.trim();
            if (!(line.isBlank() || line.startsWith("#"))) {
                if (line.endsWith("==>")) {
                    document = RegularExpression.transform(activityio.iotabname, document, line.substring(0, line.length() - 3).trim(), "");
                } else {
                    String[] segments = line.split("==>");
                    if (segments.length != 2) {
                        UserReporting.warning(activityio.iotabname, "regex definition line does not parse: " + line);
                    } else {
                        segments[0] = segments[0].trim();
                        segments[1] = segments[1].trim();
                        document = RegularExpression.transform(activityio.iotabname, document, segments[0], segments[1]);
                    }
                }
            }
        }
        return document;
    }
}
