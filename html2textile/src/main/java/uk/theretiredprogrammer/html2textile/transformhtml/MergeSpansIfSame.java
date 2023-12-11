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
import uk.theretiredprogrammer.html2textile.rules.StyleAttribute;

public class MergeSpansIfSame implements TransformHtmlItem {

    @Override
    public ResumeAction testElementAndModify(Element element) throws IOException {
        if (element.getTagName().equals("span")) {
            Element nextsibling = DomHelper.nextSiblingIsElementSkippingLine(element,"span");
            if (nextsibling != null ) {
                StyleAttribute firstspanstyles = new StyleAttribute(element);
                StyleAttribute secondspanstyles = new StyleAttribute(nextsibling);
                if (firstspanstyles.isSame(secondspanstyles)) {
                    DomHelper.insertBeforeNode(nextsibling.getFirstChild(), element.getChildNodes());
                    DomHelper.removeNode(element);
                    return ResumeAction.RESUME_FROM_PREVIOUS;
                }
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
