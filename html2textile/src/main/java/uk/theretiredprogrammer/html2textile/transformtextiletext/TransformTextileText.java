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
    private final List<String>transformations = new ArrayList<>();

    public TransformTextileText(StringWriter input, PrintWriter output) {
        lines = getLines(input);
        this.output = output;
    }
    
    public void setTransforms(InputStream definitions) throws IOException{
        try (BufferedReader definitionreader = new BufferedReader(new InputStreamReader(definitions))) {
            String line;
            while ((line=definitionreader.readLine()) != null) {
                if (!(line.startsWith("#") || line.isBlank()) ){
                    transformations.add(line);
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
        for (String rule: transformations) {
            String[]parts = rule.split("<===>");
            line = line.replaceAll(parts[0], parts[1]);
        }
        return line;
//        return line.replaceAll("^h(\\d)\\{text-align:center;\\}\\. (.*)$","h$1=. $2");
    }
}
