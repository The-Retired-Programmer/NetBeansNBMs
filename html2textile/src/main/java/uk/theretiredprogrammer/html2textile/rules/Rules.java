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
import uk.theretiredprogrammer.html2textile.transformhtml.StyleRulesProcessing;
import uk.theretiredprogrammer.html2textile.transformtext.TransformHtmlText;
import uk.theretiredprogrammer.html2textile.transformtext.TransformTextileText;

public class Rules {

    public static void create(File datainput) throws FileNotFoundException, IOException {
        initialiserules();
        File parent = datainput.getParentFile();
        loadrulesfile(getownrulesfile(parent, datainput), false);
        loadrulesfile(getsharedrulesfile(parent), false);
        loadrulesfile(getsharedrulesfile(parent.getParentFile()), false);
        loadrulesfile(getsystemrulesfile(), true);
    }

    public static void create() throws FileNotFoundException, IOException {
        initialiserules();
        loadrulesfile(getsystemrulesfile(), true);
    }
    
    //  intended for testing purposes
    public static void create(Reader alternative) throws FileNotFoundException, IOException {
        initialiserules();
        loadrulesfile(alternative, true);
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
    
    private static void loadrulesfile(Reader ruleset, boolean issystem) throws IOException {
        if (ruleset != null) {
            try ( BufferedReader rulesreader = new BufferedReader(ruleset)) {
                loadrulesfile(issystem, rulesreader);
            }
        }
    }

    private static void loadrulesfile(InputStream ruleset, boolean issystem) throws IOException {
        if (ruleset != null) {
            try ( BufferedReader rulesreader = new BufferedReader(new InputStreamReader(ruleset))) {
                loadrulesfile(issystem, rulesreader);
            }
        }
    }
    
    private static void loadrulesfile(boolean issystem, BufferedReader rulesreader) throws IOException {
        RuleSet rulesset = null;
        String line;
        while ((line = rulesreader.readLine()) != null) {
            if (!(line.startsWith("#") || line.isBlank())) {
                if (line.startsWith("[")) {
                    int pos = line.indexOf(']');
                    rulesset = get(pos == -1 ? line.substring(1) : line.substring(1, pos));
                } else {
                    if (rulesset == null) {
                        throw new IOException("Missing rules set name");
                    } else {
                        rulesset.parseAndInsertRule(line, issystem);
                    }
                }
            }
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
            case "HTML_ELEMENT_PROCESSING" ->
                elementrulesprocessing;
            case "HTML_ATTRIBUTE_PROCESSING" ->
                attributerulesprocessing;
            default ->
                throw new IOException("Unknown rules set name: " + name);
        };
    }

    private static void initialiserules() {
        transformhtmltext = new TransformHtmlText();
        ignore_HTML_PREPROCESSING_systemrules = false;
        transformtextiletext = new TransformTextileText();
        ignore_TEXTILE_POSTPROCESSING_systemrules = false;
        stylerulesprocessing = new StyleRulesProcessing();
        ignore_HTML_STYLE_PROCESSING_systemrules = false;
        elementrulesprocessing = new ElementRulesProcessing();
        ignore_HTML_ELEMENT_PROCESSING_systemrules = false;
        attributerulesprocessing = new AttributeRulesProcessing();
        ignore_HTML_ATTRIBUTE_PROCESSING_systemrules = false;
    }

    private static TransformHtmlText transformhtmltext;
    private static boolean ignore_HTML_PREPROCESSING_systemrules;
    private static TransformTextileText transformtextiletext;
    private static boolean ignore_TEXTILE_POSTPROCESSING_systemrules;
    private static StyleRulesProcessing stylerulesprocessing;
    private static boolean ignore_HTML_STYLE_PROCESSING_systemrules;
    private static ElementRulesProcessing elementrulesprocessing;
    private static boolean ignore_HTML_ELEMENT_PROCESSING_systemrules;
    private static AttributeRulesProcessing attributerulesprocessing;
    private static boolean ignore_HTML_ATTRIBUTE_PROCESSING_systemrules;

    public static void ignore_ALL_SystemRules() {
        ignore_HTML_PREPROCESSING_systemrules = true;
        ignore_TEXTILE_POSTPROCESSING_systemrules = true;
        ignore_HTML_STYLE_PROCESSING_systemrules = true;
        ignore_HTML_ELEMENT_PROCESSING_systemrules = true;
        ignore_HTML_ATTRIBUTE_PROCESSING_systemrules = true;
    }

    public static void ignore_HTML_PREPROCESSING_SystemRules() {
        ignore_HTML_PREPROCESSING_systemrules = true;
    }

    public static TransformHtmlText get_HTML_PREPROCESSING() {
        transformhtmltext.ignoreSystemRules(ignore_HTML_PREPROCESSING_systemrules);
        return transformhtmltext;
    }

    public static void ignore_TEXTILE_POSTPROCESSING_SystemRules() {
        ignore_TEXTILE_POSTPROCESSING_systemrules = true;
    }

    public static TransformTextileText get_TEXTILE_POSTPROCESSING() {
        transformtextiletext.ignoreSystemRules(ignore_TEXTILE_POSTPROCESSING_systemrules);
        return transformtextiletext;
    }

    public static void ignore_HTML_STYLE_PROCESSING_SystemRules() {
        ignore_HTML_STYLE_PROCESSING_systemrules = true;
    }

    public static StyleRulesProcessing get_HTML_STYLE_PROCESSING() {
        stylerulesprocessing.ignoreSystemRules(ignore_HTML_STYLE_PROCESSING_systemrules);
        return stylerulesprocessing;
    }

    public static void ignore_HTML_ELEMENT_PROCESSING_SystemRules() {
        ignore_HTML_ELEMENT_PROCESSING_systemrules = true;
    }

    public static ElementRulesProcessing get_HTML_ELEMENT_PROCESSING() {
        elementrulesprocessing.ignoreSystemRules(ignore_HTML_ELEMENT_PROCESSING_systemrules);
        return elementrulesprocessing;
    }

    public static void ignore_HTML_ATTRIBUTE_PROCESSING_SystemRules() {
        ignore_HTML_ELEMENT_PROCESSING_systemrules = true;
    }

    public static AttributeRulesProcessing get_HTML_ATTRIBUTE_PROCESSING() {
        attributerulesprocessing.ignoreSystemRules(ignore_HTML_ATTRIBUTE_PROCESSING_systemrules);
        return attributerulesprocessing;
    }
}
