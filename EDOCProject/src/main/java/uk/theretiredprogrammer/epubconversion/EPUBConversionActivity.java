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

    private final FileObject preregexfo;
    private final FileObject xsltfo;
    private final FileObject regexfo;
    private final FileObject projectdir;

    public EPUBConversionActivity(FileObject projectdir, ActivityIO activityio) {
        super(activityio);
        this.projectdir = projectdir;
        this.preregexfo = projectdir.getFileObject("preregex.txt");
        this.xsltfo = projectdir.getFileObject("transform.xsl");
        this.regexfo = projectdir.getFileObject("regex.txt");
    }

    @Override
    public void onActivity() {
        FileObject sectionsfolder = projectdir.getFileObject("OEBPS/sections");
        if (sectionsfolder == null) {
            UserReporting.warning("EPUB", "Cannot access sections - folder structure incorrect");
            return;
        }
        for (var fo : sectionsfolder.getChildren()) {
            if (fo.hasExt("xhtml")) {
                conversion(fo, createOutput(fo));
            }
        }
    }

    private Writer createOutput(FileObject in) {
        try {
            FileObject folder = in.getParent();
            String outfilename = in.getName() + ".html";
            FileObject out = folder.getFileObject(outfilename);
            if (out != null) {
                out.delete();
            }
            return new OutputStreamWriter(folder.createAndOpen(outfilename));
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
            UserReporting.exceptionWithMessage(activityio.iotabname, "failure while converting document- read from file phase", ex);
            return;
        }
        if (preregexfo != null) {
            UserReporting.info("EPUB", "Running Regex pre phase");
            try {
                document = regex(document, preregexfo);
            } catch (IOException ex) {
                UserReporting.exceptionWithMessage(activityio.iotabname, "failure while converting document- first regex phase", ex);
                return;
            }
        }
        if (xsltfo != null) {
            UserReporting.info("EPUB", "Running XSLT phase");
            try ( StringWriter xsltoutputwtr = new StringWriter();  Reader xsltrdr = new InputStreamReader(xsltfo.getInputStream())) {
                XSLT.transform(activityio.iotabname, new StringReader(document), xsltoutputwtr, xsltrdr);
                document = xsltoutputwtr.toString();
            } catch (IOException ex) {
                UserReporting.exceptionWithMessage(activityio.iotabname, "failure while converting document- xslt phase", ex);
                return;
            }
        }
        if (regexfo != null) {
            UserReporting.info("EPUB", "Running Regex post phase");
            try {
                document = regex(document, regexfo);
            } catch (IOException ex) {
                UserReporting.exceptionWithMessage(activityio.iotabname, "failure while converting document- second regex phase", ex);
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
        return document;
    }
}
