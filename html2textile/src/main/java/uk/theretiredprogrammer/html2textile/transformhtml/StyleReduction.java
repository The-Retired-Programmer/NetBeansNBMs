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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import uk.theretiredprogrammer.html2textile.RegexTransformationRuleSet;

public class StyleReduction extends DomModificationsWithRules {

    public StyleReduction(File input, String name, boolean ignoresystemrules) throws IOException {
        super(input, name, ignoresystemrules);
    }
    
    public StyleReduction(String name) throws IOException {
        super(name);
    }

    public ResumeAction testElementAndModify(Element element, RegexTransformationRuleSet ruleset) {
        NamedNodeMap attributes = element.getAttributes();
        Node style = attributes.getNamedItem("style");
        if (style != null) {
            style.setNodeValue(ruleset.transform(style.getNodeValue()));
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
