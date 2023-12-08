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
package uk.theretiredprogrammer.html2textile.rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import uk.theretiredprogrammer.html2textile.transformhtml.AttributeRulesProcessing;
import uk.theretiredprogrammer.html2textile.transformhtml.ElementRulesProcessing;
import uk.theretiredprogrammer.html2textile.transformhtml.StyleAttributesRulesProcessing;
import uk.theretiredprogrammer.html2textile.transformhtml.StylesToClassRulesProcessing;
import uk.theretiredprogrammer.html2textile.transformhtml.URLRulesProcessing;
import uk.theretiredprogrammer.html2textile.transformtext.TransformHtmlText;
import uk.theretiredprogrammer.html2textile.transformtext.TransformTextileText;

public class Rules {

    public static void create(File datainput) throws FileNotFoundException, IOException {
        initialiserules();
        File parent = datainput.getParentFile();
        loadrulesfile(getownrulesfile(parent, datainput));
        if (noinheritance) {
            return;
        }
        loadrulesfile(getsharedrulesfile(parent));
        if (noinheritance) {
            return;
        }
        loadrulesfile(getsharedrulesfile(parent.getParentFile()));
        if (noinheritance) {
            return;
        }
        loadrulesfile(getsystemrulesfile());
    }

    public static void create() throws FileNotFoundException, IOException {
        initialiserules();
        loadrulesfile(getsystemrulesfile());
    }

    //  intended for testing purposes
    public static void create(Reader alternative) throws FileNotFoundException, IOException {
        initialiserules();
        loadrulesfile(alternative);
    }

    private static InputStream getownrulesfile(File folder, File own) throws FileNotFoundException {
        String name = own.getName();
        String[] nameparts = name.split("\\.");
        return getrulesfile(folder, nameparts[0]);
    }

    private static InputStream getsharedrulesfile(File folder) throws FileNotFoundException {
        return getrulesfile(folder, "shared");
    }

    private static InputStream getsystemrulesfile() throws FileNotFoundException {
        return Rules.class.getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/system.rules");
    }

    private static InputStream getrulesfile(File folder, String name) throws FileNotFoundException {
        File[] rulefile = folder.listFiles((dir, fname) -> fname.equals(name + ".rules"));
        return rulefile.length == 1 ? new FileInputStream(rulefile[0]) : null;
    }

    private static void loadrulesfile(Reader ruleset) throws IOException {
        if (ruleset != null) {
            try ( BufferedReader rulesreader = new BufferedReader(ruleset)) {
                loadrulesfile(rulesreader);
            }
        }
    }

    private static void loadrulesfile(InputStream ruleset) throws IOException {
        if (ruleset != null) {
            try ( BufferedReader rulesreader = new BufferedReader(new InputStreamReader(ruleset))) {
                loadrulesfile(rulesreader);
            }
        }
    }

    private static boolean noinheritance = false;

    private static void loadrulesfile(BufferedReader rulesreader) throws IOException {
        RuleSet rulesset = null;
        String line;
        while ((line = rulesreader.readLine()) != null) {
            line = line.strip();
            if (!(line.startsWith("#") || line.isBlank())) {
                if (line.startsWith("{")) {
                    parserulescommands(line);
                } else {
                    if (line.startsWith("[")) {
                        int pos = line.indexOf(']');
                        rulesset = get(pos == -1 ? line.substring(1) : line.substring(1, pos));
                    } else {
                        if (rulesset == null) {
                            throw new IOException("Missing rules set name");
                        } else {
                            rulesset.parseAndInsertRule(line);
                        }
                    }
                }
            }
        }
    }

    private static void parserulescommands(String command) throws IOException {
        switch (command) {
            case "{NO INHERITANCE}" ->
                noinheritance = true;
            default ->
                throw new IOException("Bad Rules Command: " + command);
        }
    }

    private static RuleSet get(String name) throws IOException {
        return switch (name) {
            case "HTML_PREPROCESSING" ->
                transformhtmltext;
            case "TEXTILE_POSTPROCESSING" ->
                transformtextiletext;
            case "HTML_STYLE_PROCESSING" ->
                stylerulesprocessing;
            case "HTML_FINAL_STYLE_PROCESSING" ->
                stylerulesprocessing;
            case "HTML_ELEMENT_PROCESSING" ->
                elementrulesprocessing;
            case "HTML_FINAL_ELEMENT_PROCESSING" ->
                finalelementrulesprocessing;
            case "HTML_ATTRIBUTE_PROCESSING" ->
                attributerulesprocessing;
            case "HTML_URL_PROCESSING" ->
                urlrulesprocessing;
            case "HTML_STYLE_TO_CLASS_PROCESSING" ->
                styletoclassrulesprocessing;
            default ->
                throw new IOException("Unknown rules set name: " + name);
        };
    }

    private static void initialiserules() {
        transformhtmltext = new TransformHtmlText();
        transformtextiletext = new TransformTextileText();
        stylerulesprocessing = new StyleAttributesRulesProcessing();
        finalstylerulesprocessing = new StyleAttributesRulesProcessing();
        elementrulesprocessing = new ElementRulesProcessing();
        finalelementrulesprocessing = new ElementRulesProcessing();
        attributerulesprocessing = new AttributeRulesProcessing();
        urlrulesprocessing = new URLRulesProcessing();
        styletoclassrulesprocessing = new StylesToClassRulesProcessing();
        noinheritance = false;
    }

    private static TransformHtmlText transformhtmltext;
    private static TransformTextileText transformtextiletext;
    private static StyleAttributesRulesProcessing stylerulesprocessing;
    private static StyleAttributesRulesProcessing finalstylerulesprocessing;
    private static ElementRulesProcessing elementrulesprocessing;
    private static ElementRulesProcessing finalelementrulesprocessing;
    private static AttributeRulesProcessing attributerulesprocessing;
    private static URLRulesProcessing urlrulesprocessing;
    private static StylesToClassRulesProcessing styletoclassrulesprocessing;

    public static TransformHtmlText get_HTML_PREPROCESSING() {
        return transformhtmltext;
    }

    public static TransformTextileText get_TEXTILE_POSTPROCESSING() {
        return transformtextiletext;
    }

    public static StyleAttributesRulesProcessing get_HTML_STYLE_PROCESSING() {
        return stylerulesprocessing;
    }

    public static StyleAttributesRulesProcessing get_HTML_FINAL_STYLE_PROCESSING() {
        return finalstylerulesprocessing;
    }

    public static ElementRulesProcessing get_HTML_ELEMENT_PROCESSING() {
        return elementrulesprocessing;
    }

    public static ElementRulesProcessing get_HTML_FINAL_ELEMENT_PROCESSING() {
        return finalelementrulesprocessing;
    }

    public static AttributeRulesProcessing get_HTML_ATTRIBUTE_PROCESSING() {
        return attributerulesprocessing;
    }

    public static URLRulesProcessing get_HTML_URL_PROCESSING() {
        return urlrulesprocessing;
    }

    public static StylesToClassRulesProcessing get_HTML_STYLE_TO_CLASS_PROCESSING() {
        return styletoclassrulesprocessing;
    }
}
