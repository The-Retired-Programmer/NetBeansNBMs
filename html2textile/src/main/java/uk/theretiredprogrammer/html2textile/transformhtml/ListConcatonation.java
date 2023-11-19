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

public class ListConcatonation implements TransformHtmlItem {

    public ResumeAction testElementAndModify(Element element) {
        if (checkListAndConcatonate("ul", element) == ResumeAction.RESUME_FROM_SELF) {
            return ResumeAction.RESUME_FROM_SELF;
        }
        return checkListAndConcatonate("ol", element);
    }

    private ResumeAction checkListAndConcatonate(String listtag, Element element) {
        if (element.getTagName().equals(listtag)) {
            Element sibling = DomHelper.nextElementSiblingSkippingLine(element);
            if (sibling != null && sibling.getTagName().equals(listtag)) {
                if (!element.hasAttributes() && !sibling.hasAttributes()) {
                    DomHelper.appendChildren(element, sibling.getChildNodes());
                    DomHelper.removeNode(sibling);
                    return ResumeAction.RESUME_FROM_SELF;
                }
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
