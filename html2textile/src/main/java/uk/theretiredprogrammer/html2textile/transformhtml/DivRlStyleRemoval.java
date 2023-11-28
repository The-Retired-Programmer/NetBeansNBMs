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

import uk.theretiredprogrammer.html2textile.rules.Style;
import java.io.IOException;
import org.w3c.dom.Element;

public class DivRlStyleRemoval implements TransformHtmlItem {

    public static String[] comparison = new String[]{
        "margin: 20px 20px 20px 20px;",
        "font-family: arial,helvetica,sans-serif;",
        "font-size: 12pt;",
        "line-height: 1.5em;",
        "color: #000000;"
    };

    public ResumeAction testElementAndModify(Element element) throws IOException {
        if (element.getTagName().equals("div")) {
            Style style = new Style();
            if (style.extractIfOnlyAttribute(element)) {
                if (style.isSame(comparison)) {
                    DomHelper.insertBeforeNode(element, element.getChildNodes());
                    DomHelper.removeNode(element);
                    return ResumeAction.RESUME_FROM_ROOT;
                }
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
