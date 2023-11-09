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

    public RegexTransformationRuleSet(File datainput, String rulesext, boolean ignoresystemrules) throws FileNotFoundException, IOException {
        loadrules(datainput, rulesext, ignoresystemrules);
    }

    public RegexTransformationRuleSet(String rulesext) throws FileNotFoundException, IOException {
        loadrulesfile(getsystemrulesfile(rulesext));
    }

    private void loadrules(File datainput, String rulesext, boolean ignoresystemrules) throws FileNotFoundException, IOException {
        File parent = datainput.getParentFile();
        //
        loadrulesfile(getownrulesfile(parent, datainput, rulesext));
        InputStream is = getsharedrulesfile(parent, rulesext);
        loadrulesfile(is != null ? is : getsharedrulesfile(parent.getParentFile(), rulesext));
        if (!ignoresystemrules) {
            loadrulesfile(getsystemrulesfile(rulesext));
        }
    }

    private InputStream getownrulesfile(File folder, File own, String rulesext) throws FileNotFoundException {
        String name = own.getName();
        String[] nameparts = name.split("\\.");
        return getrulesfile(folder, nameparts[0], rulesext);
    }

    private InputStream getsharedrulesfile(File folder, String rulesext) throws FileNotFoundException {
        return getrulesfile(folder, "shared", rulesext);
    }

    private InputStream getsystemrulesfile(String rulesext) throws FileNotFoundException {
        return this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/system." + rulesext);

    }

    private InputStream getrulesfile(File folder, String name, String rulesext) throws FileNotFoundException {
        File[] rulefile = folder.listFiles((dir, fname) -> fname.equals(name + "." + rulesext));
        return rulefile.length == 1 ? new FileInputStream(rulefile[0]) : null;
    }

    private void loadrulesfile(InputStream ruleset) throws IOException {
        if (ruleset != null) {
            try ( BufferedReader rulesreader = new BufferedReader(new InputStreamReader(ruleset))) {
                String line;
                while ((line = rulesreader.readLine()) != null) {
                    if (!(line.startsWith("#") || line.isBlank())) {
                        transformations.add(new Rule(line));
                    }
                }
            }
        }
    }

    public String transform(String line) {
        for (Rule rule : transformations) {
            line = rule.isregex ? line.replaceAll(rule.match, rule.replacement) : line.replace(rule.match, rule.replacement);
        }
        return line;
    }

    private class Rule {

        public final String match;
        public final String replacement;
        public final boolean isregex;

        public Rule(String rule) throws IOException {
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
