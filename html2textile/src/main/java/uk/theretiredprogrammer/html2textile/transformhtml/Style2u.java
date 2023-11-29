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
import uk.theretiredprogrammer.html2textile.rules.Style;
import uk.theretiredprogrammer.html2textile.rules.StyleRule;

public class Style2u implements TransformHtmlItem {

    public ResumeAction testElementAndModify(Element element) throws IOException {
        Style style = new Style();
        style.extract(element);
        StyleRule findme = new StyleRule("text-decoration", "underline");
        if (style.contains(findme)) {
            style.removeStyleRule(findme);
            style.setStyle(element);
            Element strong = DomHelper.createElement("u", element);
            DomHelper.appendChildren(strong, element.getChildNodes());
            DomHelper.appendChild(element, strong);
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
