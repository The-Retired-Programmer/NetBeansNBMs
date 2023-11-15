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

public class Style2u extends DomModifications {

    public ResumeAction testElementAndModify(Element element) {
        String style = element.getAttribute("style");
        if (!style.isBlank()) {
            if (removeStyleItem(element, style, "text-decoration:underline;")) {
                Element u = createElement("u", element);
                appendChildren(u, element.getChildNodes());
                appendChild(element, u);
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }

    private boolean removeStyleItem(Element element, String style, String removeme) {
        int here = style.indexOf(removeme);
        if (here != -1) {
            replaceAttribute(element,new Attribute("style", style.substring(0, here) + style.substring(here + removeme.length())));
            return true;
        }
        return false;
    }
}
