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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actionssupport.UserReporting;

public class HtmlFragment2AsciiDoc {

    private BufferedReader fragmentreader;
    private AdocLineFormatting formatter;

    public void convert(FileObject input, boolean paragraphlayout) throws FileNotFoundException, IOException {
        fragmentreader = new BufferedReader(new InputStreamReader(input.getInputStream()));
        FileObject parent = input.getParent();
        FileObject target = parent.getFileObject(input.getName(), "adoc");
        if (target != null) {
            target.delete();
        }
        BufferedWriter adocwriter = new BufferedWriter(new OutputStreamWriter(parent.createAndOpen(input.getName() + ".adoc")));
        //
        formatter = new AdocLineFormatting(adocwriter, 80, paragraphlayout);
        initialisetagrules();
        elementstack.clear();
        parse();
        if (!elementstack.empty()) {
            throw new IOException("Error - missing </" + elementstack.pop() + ">");
        }
        formatter.close();
        fragmentreader.close();
    }

    private void parse() throws IOException {
        StringBuilder token = new StringBuilder();
        while (true) {
            token.delete(0, token.length());
            if (nextStringToken(token)) {
                //string terminated by eof;
                insertText(token);
                return;
            } else {
                // string terminated by <
                insertText(token);
                token.delete(0, token.length());
                if (nextElementToken(token)) {
                    insertElement(token);
                } else {
                    // syntax error (either not < or eof seen when scanning
                    throw new IOException("illegal element syntax");
                }
            }
        }
    }

    private boolean nextStringToken(StringBuilder token) throws IOException {
        while (true) {
            fragmentreader.mark(1);
            int c = fragmentreader.read();
            switch (c) {
                case -1:
                    return true;
                case '<':
                    fragmentreader.reset();
                    return false;
                default:
                    token.append((char) c);
            }
        }
    }

    private boolean nextElementToken(StringBuilder token) throws IOException {
        fragmentreader.mark(1);
        int c = fragmentreader.read();
        if (c != '<') {
            fragmentreader.reset();
            return false;
        }
        while (true) {
            fragmentreader.mark(1);
            c = fragmentreader.read();
            switch (c) {
                case -1:
                    return false;
                case '>':
                    return true;
                default:
                    token.append((char) c);
            }
        }
    }

    private void insertText(StringBuilder token) throws IOException {
        String text = token.toString();
        if (!text.isBlank()) {
            formatter.insert(text.trim());
        }
    }
    //
    private final Stack<String> elementstack = new Stack<>();
    private final Map<String, ElementTranslationRules> tagrules = new HashMap<>();
    private ElementTranslationRules unknowntagrules;
    private String tag;
    private String attributesstring;
    private final Map<String, String> attributes = new HashMap<>();

    private void insertElement(StringBuilder token) throws IOException {
        if (token.isEmpty()) {
            throw new IOException("bad syntax - empty element");
        }
        String elementstring = token.toString().trim();
        if (elementstring.charAt(0) == '/') {
            //this is an terminating element
            parsetag(elementstring.substring(1));
            endelement();
        } else {
            if (elementstring.charAt(elementstring.length() - 1) == '/') {
                // this is a closed element
                parsetagandattributes(elementstring.substring(0, elementstring.length() - 1));
                startendelement();
            } else {
                // must be an opening element
                parsetagandattributes(elementstring);
                startelement();
            }
        }
    }

    private void parsetagandattributes(String elementstring) throws IOException {
        int p = parsetag(elementstring);
        parseattributes((p == -1) ? "" : elementstring.substring(p).trim());
    }

    private int parsetag(String elementstring) {
        int p = elementstring.indexOf(' ');
        tag = (p == -1) ? elementstring : elementstring.substring(0, p);
        return p;
    }

    private void parseattributes(String attributesstring) throws IOException {
        attributes.clear();
        this.attributesstring = attributesstring;
        while (!attributesstring.isBlank()) {
            String[] attributeextract = extractattribute(attributesstring);
            attributes.put(attributeextract[0], attributeextract[1]);
            attributesstring = attributeextract[2];
        }
    }

    @SuppressWarnings("empty-statement")
    private String[] extractattribute(String attributesstring) throws IOException {
        String name = "";
        int index = 0;
        attributesstring = attributesstring.trim();
        char c = attributesstring.charAt(index++);
        if (!Character.isUnicodeIdentifierStart(c)) {
            throw new IOException("malformed attribute: " + attributesstring);
        }
        name = name + c;
        while (true) {
            c = attributesstring.charAt(index++);
            if (!Character.isUnicodeIdentifierPart(c)) {
                break;
            }
            name = name + c;
        }
        String val = "";
        while (attributesstring.charAt(index++) != '"');
        while ((c = attributesstring.charAt(index++)) != '"') {
            val = val + c;
        }
        return new String[]{name, val, attributesstring.substring(index).trim()};
    }

    private void startelement() throws IOException {
        elementstack.push(tag);
        gettagrules().execStarttranslation();
    }

    private void endelement() throws IOException {
        String expected = elementstack.pop();
        if (!expected.equals(tag)) {
            throw new IOException("Error - incorrect structured tags - expected: " + expected + "; found: " + tag);
        }
        gettagrules().execEndtranslation();
    }

    private void startendelement() throws IOException {
        gettagrules().execStartEndtranslation();
    }

    private ElementTranslationRules gettagrules() {
        var rules = tagrules.get(tag);
        if (rules == null) {
            UserReporting.warning("Unknow tag found: " + tag + "; unable the import correctly");
            rules = unknowntagrules;
        }
        return rules;
    }

    //  tag rule definitions
    private void initialisetagrules() {
        //insert into tagrules for each tag
        inserttagrule("p", this::nothing, this::paraend);
        inserttagrule("div", this::nothing, this::nothing); // could do better in future
        inserttagrule("span", this::nothing, this::nothing);  // assumes that span usages are standard and so can be ignored
        inserttagrule("img", this::imagestart, this::nothing);
        inserttagrule("a", this::linkstart, this::linkend);
        inserttagrule("strong", this::strong, this::strong);
        inserttagrule("b", this::strong, this::strong);
        inserttagrule("br", this::linebreak, this::nothing);
        inserttagrule("hr", this::hrule, this::nothing);
        inserttagrule("ul", this::ulstart, this::ulend);
        inserttagrule("li", this::listitem, this::nothing);
        inserttagrule("table", this::tablestart, this::tableend);
        inserttagrule("tbody", this::nothing, this::nothing);
        inserttagrule("tr",this::trstart, this::nothing);
        inserttagrule("td", this::tdstart, this::nothing);
        inserttagrule("h1", this::h1start, this::newlineifrequired);
        inserttagrule("h2", this::h2start, this::newlineifrequired);
        inserttagrule("h3", this::h3start, this::newlineifrequired);
        inserttagrule("h4", this::h4start, this::newlineifrequired);
        inserttagrule("h5", this::h5start, this::newlineifrequired);
        inserttagrule("h6", this::h6start, this::newlineifrequired);
        // define unknown tagrules
        unknowntagrules = new ElementTranslationRules(this::undefinedstarttag, this::undefinedendtag);
    }

    private void inserttagrule(String tag, Runnable starttranslation, Runnable endtranslation) {
        tagrules.put(tag, new ElementTranslationRules(starttranslation, endtranslation));
    }

    private void nothing() {
    }
    
    private void newlineifrequired() {
        formatter.newlineifrequired();
    }

    private void paraend() {
        formatter.newlineifrequired();
        formatter.newline();
    }

    private void linebreak() {
        formatter.insertnobreaks(" +");
        formatter.newline();
    }
    
    private void hrule() {
        formatter.newlineifrequired();
        formatter.insertnobreaks("'''");
        formatter.newline();
    }
    
    private void ulstart() {
        formatter.newlineifrequired();
        formatter.newline();
    }
    
    private void ulend() {
        formatter.newlineifrequired();
        formatter.newline();
    }
    
    private void listitem() {
        formatter.newlineifrequired();
        formatter.insertnobreaks("*  ");
    }
    
    private void tablestart() {
        formatter.newlineifrequired();
        formatter.insertnobreaks("[cols=\"\"]");
        formatter.newline();
        formatter.insertnobreaks("|===");
    }
    
    private void tableend() {
        formatter.newlineifrequired();
        formatter.insertnobreaks("|===");
        formatter.newline();
    }
    
    private void trstart() {
       formatter.newlineifrequired(); 
       formatter.newline(); 
    }
    
    private void tdstart() {
        formatter.newlineifrequired();
        formatter.insert("|");
    }
    
    private void h1start() {
        formatter.newlineifrequired();
        formatter.insertnobreaks("= ");
    }

    private void h2start() {
        formatter.newlineifrequired();
        formatter.insertnobreaks("== ");
    }

    private void h3start() {
        formatter.newlineifrequired();
        formatter.insertnobreaks("=== ");
    }

    private void h4start() {
        formatter.newlineifrequired();
        formatter.insertnobreaks("==== ");
    }

    private void h5start() {
        formatter.newlineifrequired();
        formatter.insertnobreaks("===== ");
    }

    private void h6start() {
        formatter.newlineifrequired();
        formatter.insertnobreaks("====== ");
    }

    private void strong() {
        formatter.insertnobreaks("**");
    }

    private void imagestart() {
        String adocattributes = "";
        adocattributes = insertnamedadocattribute(adocattributes, "alt", "alt");
        adocattributes = insertnamedadocattribute(adocattributes, "width", "width");
        adocattributes = insertnamedadocattribute(adocattributes, "height", "height");
        adocattributes = insertnamedadocattribute(adocattributes, "class", "role");
        adocattributes = insertnamedadocattributefromstyle(adocattributes, "float:", "float");

        formatter.newlineifrequired();
        formatter.insertnobreaks("image::" + getattribute("src") + "[" + adocattributes + "]");
        formatter.newline();
    }

    private void linkstart() {
        formatter.insertnobreaks("link:" + getattribute("href") + "[");
    }

    private void linkend() {
        formatter.insertnobreaks("]");
    }

    private void undefinedstarttag() {
        formatter.insertnobreaks(attributes.isEmpty() ? "<<" + tag + ">>" : "<<" + tag + " " + attributesstring + ">>");
    }

    private void undefinedendtag() {
        formatter.insertnobreaks("<</" + tag + ">>");
    }

    private String getattribute(String attributekey) {
        String attr = attributes.get(attributekey);
        return attr != null ? attr.trim() : "";
    }

    private String insertnamedadocattribute(String adocattributes, String attributekey, String adocattributekey) {
        String prefix = adocattributes.isBlank() ? "" : ",";
        String attr = attributes.get(attributekey);
        if (attr != null) {
            attr = attr.trim();
            if (!attr.isEmpty()) {
                return adocattributes + prefix + adocattributekey + "=" + attr;
            }
        }
        return adocattributes;
    }

    private String insertnamedadocattributefromstyle(String adocattributes, String styleattributekey, String adocattributekey) {
        String prefix = adocattributes.isBlank() ? "" : ",";
        String attr = attributes.get("style");
        if (attr != null) {
            attr = attr.trim();
            if (!attr.isEmpty()) {
                String styleattributevalue = extractfromstylestring(attr, styleattributekey);
                if (styleattributevalue != null) {
                    return adocattributes + prefix + adocattributekey + "=\"" + styleattributevalue + "\"";
                }
            }
        }
        return adocattributes;
    }

    private String extractfromstylestring(String style, String key) {
        int p = style.indexOf(key);
        if (p == -1) {
            return null;
        }
        p += key.length();
        int q = style.indexOf(';', p);
        return q == -1 ? style.substring(p).trim() : style.substring(p, q).trim();
    }

    private class ElementTranslationRules {

        private final Runnable starttranslation;
        private final Runnable endtranslation;

        public ElementTranslationRules(Runnable starttranslation, Runnable endtranslation) {
            this.starttranslation = starttranslation;
            this.endtranslation = endtranslation;
        }

        public void execStarttranslation() {
            starttranslation.run();
        }

        public void execEndtranslation() {
            endtranslation.run();
        }

        public void execStartEndtranslation() {
            starttranslation.run(); // needs improvement so it becomes a single nobreak object
            endtranslation.run();
        }
    }
}
