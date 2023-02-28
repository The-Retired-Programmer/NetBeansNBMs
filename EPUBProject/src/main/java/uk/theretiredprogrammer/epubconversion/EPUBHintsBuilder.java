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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.util.UserReporting;

public class EPUBHintsBuilder {

    public static void create(FileObject epubfile, FileObject stylesheet, String iotabname) {
        try {
            try ( PrintWriter out = createHintsFile(epubfile)) {
                generateHints(epubfile, stylesheet, out);
            }
        } catch (IOException ex) {
            UserReporting.exceptionWithMessage(iotabname, "Failure while creating hints file content", ex);
        }
    }

    private static PrintWriter createHintsFile(FileObject epubfile) throws IOException {
        String outfilename = epubfile.getName() + ".hints";
        FileObject out = epubfile.getParent().getFileObject(outfilename);
        if (out != null) {
            String oldoutfilename = epubfile.getName() + ".old_hints";
            FileObject oldout = epubfile.getParent().getFileObject(oldoutfilename);
            if (oldout != null) {
                oldout.delete();
            }
            try ( FileLock outlock = out.lock()) {
                out.rename(outlock, epubfile.getName(), "old_hints");
            }
        }
        return new PrintWriter(new OutputStreamWriter(epubfile.getParent().createAndOpen(outfilename)));
    }

    private static void generateHints(FileObject epubfile, FileObject stylesheet, PrintWriter out) throws IOException {
        out.println("xmlns=\"http://www.w3.org/1999/xhtml\" ==> ");
        out.println("xmlns:epub=\"http://www.idpf.org/2007/ops\" ==>");
        Extractor lex = new Extractor(stylesheet.asText().replace('\n', ' '));
        while (true) {
            String name = lex.nextClass();
            if (name == null) {
                return;
            }
            if (name.startsWith("para")) {
                writeHint(out, name, getParaStyle(lex));
            } else if (name.startsWith("span")) {
                writeHint(out, name, getSpanStyle(lex));
            } else if (name.startsWith("body")) {
                getBodyStyle(lex);
            } else if (name.startsWith("frame")) {
                writeHint(out, name, getFrameStyle(lex), getImage(epubfile, stylesheet));
            } else if (name.startsWith("cellTable")) {
                writeHint(out, name, getCellTableStyle(lex));
            } else if (name.startsWith("rowTable")) {
                writeHint(out, name, getRowTableStyle(lex));
            } else if (name.startsWith("table")) {
                writeHint(out, name, getTableStyle(lex));
            } else {
                UserReporting.warning("EPUB", "unknown style class type -  " + name);
            }
        }
    }

    private static void writeHint(PrintWriter out, String classname, String style) {
        if (style.isBlank()) {
            out.println("class=\"" + classname + "\" ==>");
        } else {
            out.println("class=\"" + classname + "\" ==> style=\"" + style + "\"");
        }
    }

    private static void writeHint(PrintWriter out, String classname, String style, String imagefile) {
        if (style.isBlank()) {
            out.println("class=\"" + classname + "\" ==> use=\"" + imagefile + "\"");
        } else {
            out.println("class=\"" + classname + "\" ==> style=\"" + style + "\" use=\"" + imagefile + "\"");
        }
    }

    private static String getParaStyle(Extractor lex) throws IOException {
        List<CSSRule> rules = new ArrayList<>();
        while (true) {
            CSSRule rule = lex.nextRule();
            if (rule == null) {
                return HintsAnalyser.analyseParaRules(rules);
            }
        rules.add(rule);
        }
    }

    private static String getSpanStyle(Extractor lex) throws IOException {
        List<CSSRule> rules = new ArrayList<>();
        while (true) {
            CSSRule rule = lex.nextRule();
            if (rule == null) {
                return HintsAnalyser.analyseSpanRules(rules);
            }
            rules.add(rule);
        }
    }

    private static String getBodyStyle(Extractor lex) throws IOException {
        List<CSSRule> rules = new ArrayList<>();
        while (true) {
            CSSRule rule = lex.nextRule();
            if (rule == null) {
                return HintsAnalyser.analyseBodyRules(rules);
            }
            rules.add(rule);
        }
    }

    private static String getFrameStyle(Extractor lex) throws IOException {
        List<CSSRule> rules = new ArrayList<>();
        while (true) {
            CSSRule rule = lex.nextRule();
            if (rule == null) {
                return HintsAnalyser.analyseFrameRules(rules);
            }
            rules.add(rule);
        }
    }

    private static String getImage(FileObject epubfile, FileObject stylesheet) throws IOException {
        FileObject epubimagesdir = stylesheet.getParent().getParent().getFileObject("images");
        if (epubimagesdir == null) {
            throw new IOException("Cannot find the images folder in the expanded EPUB file");
        }
        FileObject[] images = epubimagesdir.getChildren();
        if (images.length == 0) {
            throw new IOException("Error - no images are present");
        }
        FileObject epubparentfolder = epubfile.getParent();
        FileObject defaultimage = epubparentfolder.getFileObject(epubfile.getName(), "JPG");
        if (defaultimage == null) {
            defaultimage = epubparentfolder.getFileObject(epubfile.getName(), "jpg");
            if (defaultimage == null) {
                defaultimage = epubparentfolder.getFileObject(epubfile.getName(), "png");
            }
        }
        String imagefilename;
        if (defaultimage == null) {
            UserReporting.warning("EPUB", "Can't find a default image for use in this page");
            imagefilename = "missing.image";
        } else {
            imagefilename = defaultimage.getNameExt();
        }
        return imagefilename;
    }

    private static String getCellTableStyle(Extractor lex) throws IOException {
        List<CSSRule> rules = new ArrayList<>();
        while (true) {
            CSSRule rule = lex.nextRule();
            if (rule == null) {
                return HintsAnalyser.analyseCellTableRules(rules);
            }
            rules.add(rule);
        }
    }

    private static String getRowTableStyle(Extractor lex) throws IOException {
        List<CSSRule> rules = new ArrayList<>();
        while (true) {
            CSSRule rule = lex.nextRule();
            if (rule == null) {
                return HintsAnalyser.analyseRowTableRules(rules);
            }
            rules.add(rule);
        }
    }

    private static String getTableStyle(Extractor lex) throws IOException {
        List<CSSRule> rules = new ArrayList<>();
        while (true) {
            CSSRule rule = lex.nextRule();
            if (rule == null) {
                return HintsAnalyser.analyseTableRules(rules);
            }
            rules.add(rule);
        }
    }

    private static class Extractor {

        private final String cssdocument;
        private int extractpoint;

        public Extractor(String cssdocument) {
            this.cssdocument = cssdocument;
            extractpoint = 0;
        }

        public String nextClass() throws IOException {
            int p = cssdocument.indexOf('{', extractpoint);
            if (p == -1) {
                return null;
            }
            String name = cssdocument.substring(extractpoint, p).trim();
            if (name.startsWith(".")) {
                extractpoint = p + 1;
                return name.substring(1);
            }
            throw new IOException("stylesheet contains definitions other than classes: " + name);
        }

        public CSSRule nextRule() throws IOException {
            int p = cssdocument.indexOf('}', extractpoint);
            if (p == -1) {
                throw new IOException("malconstructed class, no closing bracket found '}'");
            }
            int q = cssdocument.indexOf(';', extractpoint);
            if (q == -1) {
                return null;
            }
            if (q < p) { // rule
                int r = cssdocument.indexOf(":", extractpoint);
                if (r == -1 || q <= r) {
                    throw new IOException("malconstructed rule, no separator found ':' prior to ';'");
                }
                String key = cssdocument.substring(extractpoint, r).trim();
                String value = cssdocument.substring(r + 1, q).trim();
                extractpoint = q + 1;
                return new CSSRule(key, value);
            } else {
                extractpoint = p + 1;
                return null;
            }
        }
    }
}
