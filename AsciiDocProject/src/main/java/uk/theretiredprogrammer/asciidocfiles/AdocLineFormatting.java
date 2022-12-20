/*
 * Copyright 2022 Richard Linsdale.
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
package uk.theretiredprogrammer.asciidocfiles;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import uk.theretiredprogrammer.actionssupport.UserReporting;

public class AdocLineFormatting {

    private final StringBuilder linebuffer;
    private final boolean paragraphlayout;
    private final int maxlinelength;
    private final BufferedWriter writer;

    public AdocLineFormatting(BufferedWriter writer, int maxlinelength, boolean paragraphlayout) {
        linebuffer = new StringBuilder(maxlinelength + 1);
        this.maxlinelength = maxlinelength;
        this.paragraphlayout = paragraphlayout;
        this.writer = writer;
        inithtmlentitymap();
    }

    public void insert(String text) {
        String[] words = text.split(" ");
        for (String word : words) {
            word = word.trim();
            if ((!paragraphlayout) && word.endsWith(".")) {
                insertnobreaks(word);
                newlineifrequired();
            } else {
                insertnobreaks(word + " ");
            }
        }
    }

    public void insertnobreaks(String text) {
        text = substitutehtmlentities(text);
        int l = text.length();
        if (linebuffer.length() + l > maxlinelength) {
            writeline();
            if (l > maxlinelength) {
                writeline(text);
            } else {
                linebuffer.append(text);
            }
        } else {
            linebuffer.append(text);
        }
    }

    private String substitutehtmlentities(String text) {
        int p = 0;
        while (true) {
            p = text.indexOf('&', p);
            if (p == -1) {
                return text;
            }
            int q = text.indexOf(';', p);
            if (q == -1) {
                UserReporting.warning("Error - malformed html entity in: " + text);
                return text;
            }
            String entityname = text.substring(p + 1, q).trim();
            String adocname = htmltoadocentitymap.get(entityname);
            if (adocname == null) {
                UserReporting.warning("Error - unknown html entity: " + entityname);
                return text;
            }
            text = text.substring(0, p) + "{" + adocname + "}" + text.substring(q + 1);
            p = q + 1;
        }
    }

    private final Map<String, String> htmltoadocentitymap = new HashMap<>();

    private void inithtmlentitymap() {
        htmltoadocentitymap.put("nbsp", "nbsp");
        htmltoadocentitymap.put("amp", "amp");
    }

    public void newlineifrequired() {
        if (!linebuffer.isEmpty()) {
            writeline();
        }
    }

    public void newline() {
        writeline();
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public void close() throws IOException {
        if (!linebuffer.isEmpty()) {
            writeline();
        }
        writer.close();
    }

    private void writeline() {
        linebuffer.append('\n');
        writeline(linebuffer.toString());
        linebuffer.delete(0, linebuffer.length());
    }

    private void writeline(String text) {
        try {
            writer.write(text);
        } catch (IOException ex) {
            UserReporting.exception(ex);
        }
    }
}
