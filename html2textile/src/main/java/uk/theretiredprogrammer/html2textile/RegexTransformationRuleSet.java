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

    public final static String RULE_PARTS_SEPARATOR = "<===>";

    private final List<Rule> transformations = new ArrayList<>();

    public RegexTransformationRuleSet(File datainput, String rulesext, boolean ignoresystemrules) throws FileNotFoundException, IOException {
        loadrules(getRuleSources(datainput, rulesext, ignoresystemrules));
    }
    
    public RegexTransformationRuleSet(String rulesext) throws FileNotFoundException, IOException {
        loadrules(getRuleSources(rulesext));
    }

    private List<InputStream> getRuleSources(File datainput, String rulesext, boolean ignoresystemrules) throws FileNotFoundException {
        List<InputStream> rules = new ArrayList<>();
        //
        File parent = datainput.getParentFile();
        String name = datainput.getName();
        String[] nameparts = name.split("\\.");
        File[] rulefiles = parent.listFiles((dir, fname) -> fname.equals(nameparts[0] + "." + rulesext));
        if (rulefiles.length == 1) {
            rules.add(new FileInputStream(rulefiles[0]));
        }
        // 
        rulefiles = parent.listFiles((dir, fname) -> fname.equals("shared." + rulesext));
        if (rulefiles.length == 1) {
            rules.add(new FileInputStream(rulefiles[0]));
        }
        //
        if (!ignoresystemrules) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/system." + rulesext);
            if (is != null) {
                rules.add(is);
            }
        }
        return rules;
    }
    
    private List<InputStream> getRuleSources(String rulesext) throws FileNotFoundException {
        List<InputStream> rules = new ArrayList<>();
        //
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/system." + rulesext);
        if (is != null ) {
            rules.add(is);
        }
        return rules;
    }

    private void loadrules(List<InputStream> rules) throws IOException {
        for (InputStream ruleset : rules) {
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
            line = line.replaceAll(rule.match, rule.result);
        }
        return line;
    }

    private class Rule {

        public final String match;
        public final String result;

        public Rule(String rule) {
            if (rule.contains(RULE_PARTS_SEPARATOR)){
            String[] parts = rule.split(RULE_PARTS_SEPARATOR);
            match = parts[0];
            result = parts.length==1?"" :parts[1];
            } else {
                match = rule;
                result = "";
            }
        }
    }
}
