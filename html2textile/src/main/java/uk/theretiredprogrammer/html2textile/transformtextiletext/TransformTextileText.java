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
package uk.theretiredprogrammer.html2textile.transformtextiletext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class TransformTextileText {

//
// STAGE 4 - textual transforms
//          update the input (textile file)
//          
//
    private final String[] lines;
    private final PrintWriter output;
    private final List<Rule>transformations = new ArrayList<>();

    public TransformTextileText(StringWriter input, PrintWriter output, List<InputStream> rules) throws IOException {
        lines = getLines(input);
        this.output = output;
        for (InputStream ruleset: rules) {
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

    public void save() {
        try (output) {
            for (String line : lines) {
                output.println(line);
            }
        }
    }

    private String[] getLines(StringWriter input) {
        String textin = input.toString();
        return textin.split("\n");
    }

    public void transform() throws IOException {
        for (int i = 0; i < lines.length; i++) {
            lines[i] = transform(lines[i]);
        }
    }

    private String transform(String line) {
        for (Rule rule: transformations) {
            line = line.replaceAll(rule.match, rule.result);
        }
        return line;
    }
    
    private class Rule {
        
        public final String match;
        public final String result;
        
        public Rule(String rule) {
            String[]parts = rule.split("<===>");
            match = parts[0];
            result = parts[1];
        }
    }
}
