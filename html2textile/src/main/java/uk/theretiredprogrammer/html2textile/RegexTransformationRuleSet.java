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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RegexTransformationRuleSet {

    private final List<Rule> transformations = new ArrayList<>();

    public RegexTransformationRuleSet(File datainput) throws FileNotFoundException, IOException {
        loadrules(datainput);
    }

    public RegexTransformationRuleSet() throws FileNotFoundException, IOException {
        loadrulesfile(getsystemrulesfile(), true);
    }

    private void loadrules(File datainput) throws FileNotFoundException, IOException {
        File parent = datainput.getParentFile();
        //
        loadrulesfile(getownrulesfile(parent, datainput), false);
        InputStream is = getsharedrulesfile(parent);
        loadrulesfile(is != null ? is : getsharedrulesfile(parent.getParentFile()), false);
        loadrulesfile(getsystemrulesfile(), true);
    }

    private InputStream getownrulesfile(File folder, File own) throws FileNotFoundException {
        String name = own.getName();
        String[] nameparts = name.split("\\.");
        return getrulesfile(folder, nameparts[0]);
    }

    private InputStream getsharedrulesfile(File folder) throws FileNotFoundException {
        return getrulesfile(folder, "shared");
    }

    private InputStream getsystemrulesfile() throws FileNotFoundException {
        return this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/system.rules");

    }

    private InputStream getrulesfile(File folder, String name) throws FileNotFoundException {
        File[] rulefile = folder.listFiles((dir, fname) -> fname.equals(name + ".rules"));
        return rulefile.length == 1 ? new FileInputStream(rulefile[0]) : null;
    }

    private void loadrulesfile(InputStream ruleset, boolean issystem) throws IOException {
        if (ruleset != null) {
            try ( BufferedReader rulesreader = new BufferedReader(new InputStreamReader(ruleset))) {
                String sectionkey = "UNDEFINED";
                String line;
                while ((line = rulesreader.readLine()) != null) {
                    if (!(line.startsWith("#") || line.isBlank())) {
                        if (line.startsWith("[")) {
                            int pos = line.indexOf(']');
                            sectionkey = pos == -1 ? line.substring(1) : line.substring(1, pos);
                        } else {
                            transformations.add(new Rule(line, issystem, sectionkey));
                        }
                    }
                }
            }
        }
    }

    public String transform(String line, String sectionkey, boolean ignoresystemrules) {
        for (Rule rule : transformations) {
            if (sectionkey.equals(rule.sectionkey) && (ignoresystemrules ? (!rule.issystem) : true)) {
                line = rule.isregex ? line.replaceAll(rule.match, rule.replacement) : line.replace(rule.match, rule.replacement);
            }
        }
        return line;
    }

    private class Rule {

        public final String match;
        public final String replacement;
        public final boolean isregex;
        public final boolean issystem;
        public final String sectionkey;

        public Rule(String rule, boolean issystem, String sectionkey) throws IOException {
            this.issystem = issystem;
            this.sectionkey = sectionkey;
            rule = rule.trim();
            if (rule.startsWith("REMOVE PATTERN ")) {
                match = trimquotes(rule.substring(14).trim());
                replacement = "";
                isregex = true;
                return;
            }
            if (rule.startsWith("REMOVE ")) {
                match = trimquotes(rule.substring(6).trim());
                replacement = "";
                isregex = false;
                return;
            }
            if (rule.startsWith("REPLACE PATTERN ")) {
                int withpos = rule.indexOf(" WITH ");
                if (withpos == -1) {
                    throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE PATTERN \" rule - " + rule);
                }
                match = trimquotes(rule.substring(15, withpos + 1).trim());
                replacement = trimquotes(rule.substring(withpos + 5).trim());
                isregex = true;
                return;
            }
            if (rule.startsWith("REPLACE ")) {
                int withpos = rule.indexOf(" WITH ");
                if (withpos == -1) {
                    throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE \" rule - " + rule);
                }
                match = trimquotes(rule.substring(7, withpos + 1).trim());
                replacement = trimquotes(rule.substring(withpos + 5).trim());
                isregex = false;
                return;
            }
            throw new IOException("Bad Rule definition: unknown command - " + rule);
        }

        private String trimquotes(String string) {
            if (string.length() > 2) {
                if (string.startsWith("'") && string.endsWith("'")) {
                    return string.substring(1, string.length() - 1);
                }
                if (string.startsWith("\"") && string.endsWith("\"")) {
                    return string.substring(1, string.length() - 1);
                }
            }
            return string;
        }
    }
}
