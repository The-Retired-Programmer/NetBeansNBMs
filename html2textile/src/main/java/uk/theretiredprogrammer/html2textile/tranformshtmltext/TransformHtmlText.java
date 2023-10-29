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
package uk.theretiredprogrammer.html2textile.tranformshtmltext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import uk.theretiredprogrammer.html2textile.RegexTransformationRuleSet;

public class TransformHtmlText {

//
// STAGE 1 - textual transforms
//          update the input (html or html fragment)
//          wrap in <html> tag - so is valid XML structure which can be loaded
//          replace any &nbsp; entities with a space
//
    private final BufferedReader original;
    private String rootname = "";
    private final RegexTransformationRuleSet ruleset;

    public TransformHtmlText(Reader original, File datainput, boolean ignoresystemrules) throws IOException {
        this.original = new BufferedReader(original);
        ruleset = new RegexTransformationRuleSet(datainput, "htmlrules", ignoresystemrules);
    }

    public TransformHtmlText(Reader original) throws IOException {
        this.original = new BufferedReader(original);
        ruleset = new RegexTransformationRuleSet("htmlrules");
    }

    public void rootWrap(String name) {
        this.rootname = name;
    }

    public Reader transform() throws IOException {
        StringWriter wrapped = new StringWriter();
        if (rootname.isEmpty()) {
            copylines(original, wrapped);
        } else {
            wrapped.write("<" + rootname + ">");
            copylines(original, wrapped);
            wrapped.write("</" + rootname + ">");
        }
        return new StringReader(ruleset.transform(wrapped.toString()));
    }

    private void copylines(BufferedReader from, Writer to) throws IOException {
        List<LineInfo> lines = getlines(from);
        if (lines.size() > 2) {
            for (int i = 1; i < lines.size() - 1; i++) {
                to.write(lines.get(i).getOutput(lines.get(i + 1).isLeadingElement()));
            }
        }
    }

    private List<LineInfo> getlines(BufferedReader from) throws IOException {
        List<LineInfo> lines = new ArrayList<>();
        lines.add(new LineInfo());
        String line;
        int linecounter = 1;
        while ((line = from.readLine()) != null) {
            if (!line.isBlank()) {
                lines.add(new LineInfo(linecounter, line));
            }
            linecounter++;
        }
        lines.add(new LineInfo());
        return lines;
    }

    private class LineInfo {

        private final boolean leadingelement;
        private final boolean trailingelement;
        private final int linenumber;
        private final String line;
        private final boolean donotoutput;

        public LineInfo() {
            leadingelement = true;
            trailingelement = true;
            donotoutput = true;
            linenumber = 0;
            line = "";
        }

        public LineInfo(int linenumber, String line) {
            this.linenumber = linenumber;
            donotoutput = false;
            String trimmedline = line.trim();
            leadingelement = trimmedline.startsWith("<");
            trailingelement = trimmedline.endsWith(">");
            this.line = trimmedline;
        }

        public boolean isLeadingElement() {
            return leadingelement;
        }

        public String getOutput(boolean followingleadingelement) {
            String terminator = trailingelement ? "" : (followingleadingelement ? "" : " ");
            return donotoutput ? "" : nextlinemarker(linenumber) + line + terminator;
        }

        private String nextlinemarker(int linecounter) {
            return "<line number=\"" + Integer.toString(linecounter) + "\"/>";
        }
    }
}
