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

import java.io.IOException;
import org.w3c.dom.Element;
import uk.theretiredprogrammer.html2textile.RegexTransformationRuleSet;
import uk.theretiredprogrammer.html2textile.RegexTransformationRuleSet.TransformationAction;

public class ElementRulesProcessing extends DomModifications {

    private final boolean ignoresystemrules;
    private final RegexTransformationRuleSet ruleset;

    public ElementRulesProcessing(RegexTransformationRuleSet ruleset, boolean ignoresystemrules) throws IOException {
        this.ruleset = ruleset;
        this.ignoresystemrules = ignoresystemrules;
    }

    public ResumeAction testElementAndModify(Element element) {
        TransformationAction action = ruleset.getAction(element.getTagName(), "HTML_ELEMENT_PROCESSING", ignoresystemrules);
        switch (action.type) {
            case REMOVE -> {
                removeNode(element);
                return ResumeAction.RESUME_FROM_PARENT;
            }
            case REMOVEELEMENT -> {
                insertBeforeNode(element, element.getChildNodes());
                removeNode(element);
                return ResumeAction.RESUME_FROM_PARENT;
            }
            case REPLACE -> {
                Element newelement = createElement(action.replacement, element);
                appendAttributes(newelement, element.getAttributes());
                appendChildren(newelement, element.getChildNodes());
                replaceNode(element, newelement);
                return ResumeAction.RESUME_FROM_PARENT;
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
