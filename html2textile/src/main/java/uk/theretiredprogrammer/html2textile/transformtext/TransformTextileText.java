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
package uk.theretiredprogrammer.html2textile.transformtext;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class TransformTextileText extends StringProxy {

//
// STAGE 4 - textual transforms
//          update the input (textile file)
//          
//
    private String[] lines;
    
    public void setInput(StringWriter input) {
        String textin = input.toString();
        lines = textin.split("\n");
    }

    public void save(PrintWriter output) {
        try (output) {
            for (String line : lines) {
                output.println(line);
            }
        }
    }

    public void transform() throws IOException {
        for (int i = 0; i < lines.length; i++) {
            lines[i] = applyRules(lines[i]);
        }
    }
}
