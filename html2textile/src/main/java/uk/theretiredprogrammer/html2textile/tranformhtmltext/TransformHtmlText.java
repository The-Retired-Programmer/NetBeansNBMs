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
package uk.theretiredprogrammer.html2textile.tranformhtmltext;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import uk.theretiredprogrammer.html2textile.RegexTransformationRuleSet;

public class TransformHtmlText {

//
// STAGE 1 - textual transforms
//          update the input (html or html fragment)
//          wrap in <html> tag - so is valid XML structure which can be loaded
//          replace any &nbsp; entities with a space
//
    private final Reader original;
    private String rootname = "";
    private final RegexTransformationRuleSet ruleset;

    public TransformHtmlText(Reader original, File datainput, boolean ignoresystemrules) throws IOException {
        this.original = original;
        ruleset = new RegexTransformationRuleSet(datainput, "htmlrules", ignoresystemrules);
    }
    
    public TransformHtmlText(Reader original) throws IOException {
        this.original = original;
        ruleset = new RegexTransformationRuleSet("htmlrules");
    }

    public void rootWrap(String name) {
        this.rootname = name;
    }

    public Reader transform() throws IOException {
        StringWriter wrapped = new StringWriter();
        if (rootname.isEmpty()) {
            original.transferTo(wrapped);
        } else {
            wrapped.write("<" + rootname + ">\n");
            original.transferTo(wrapped);
            wrapped.write("</" + rootname + ">\n");
        }
        return new StringReader(ruleset.transform(wrapped.toString()));
    }
}
