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
package uk.theretiredprogrammer.html2textile.transformhtml;

import java.io.File;
import java.io.IOException;
import org.w3c.dom.Element;
import uk.theretiredprogrammer.html2textile.RegexTransformationRuleSet;

public abstract class DomModificationsWithRules extends DomModifications {

    private final RegexTransformationRuleSet ruleset;

    public DomModificationsWithRules(File datainput, String rulesname, boolean ignoresystemrules) throws IOException {
        ruleset = new RegexTransformationRuleSet(datainput, rulesname, ignoresystemrules);
    }

    public DomModificationsWithRules(String rulesname) throws IOException {
        ruleset = new RegexTransformationRuleSet(rulesname);
    }

    public abstract ResumeAction testElementAndModify(Element element, RegexTransformationRuleSet ruleset) throws IOException;

    public ResumeAction testElementAndModify(Element element) throws IOException {
        return testElementAndModify(element, ruleset);
    }
}
