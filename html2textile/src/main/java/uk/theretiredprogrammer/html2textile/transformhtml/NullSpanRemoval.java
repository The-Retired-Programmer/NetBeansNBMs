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

import org.w3c.dom.Element;

public class NullSpanRemoval extends DomModifications {

    public ResumeAction testElementAndModify(Element element) {
        if (element.getTagName().equals("span")) {
            String stylevalue = getOnlyAttribute(element, "style");
            if (stylevalue != null && stylevalue.isBlank()) {
                insertBeforeNode(element, element.getChildNodes());
                removeNode(element);
                return ResumeAction.RESUME_FROM_PARENT;
            }
            if (!element.hasAttributes()) {
                insertBeforeNode(element, element.getChildNodes());
                removeNode(element);
                return ResumeAction.RESUME_FROM_PARENT;
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
