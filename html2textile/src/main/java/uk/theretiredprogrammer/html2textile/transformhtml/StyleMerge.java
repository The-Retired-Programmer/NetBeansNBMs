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

import uk.theretiredprogrammer.html2textile.rules.Attributes;
import java.io.IOException;
import org.w3c.dom.Element;

public class StyleMerge implements TransformHtmlItem {

    public ResumeAction testElementAndModify(Element element) throws IOException {
        if (DomHelper.isBlockElement(element)) {
            Element span = DomHelper.getOnlyChildSpanElement(element);
            if (span != null) {
                mergeStyleAttributes(element, span);
                return ResumeAction.RESUME_FROM_SELF;
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }

    private void mergeStyleAttributes(Element parent, Element child) throws IOException {
        Attributes parentattributes = new Attributes();
        parentattributes.extract(parent);
        Attributes childattributes = new Attributes();
        childattributes.extract(child);
        parentattributes.merge(childattributes);
        parentattributes.replaceAttributes(parent);
        DomHelper.insertBeforeNode(child,child.getChildNodes());
        DomHelper.removeNode(child);
    }
}
