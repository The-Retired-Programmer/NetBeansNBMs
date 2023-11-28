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

public class ImageWidthConcatonation implements TransformHtmlItem {

    public ResumeAction testElementAndModify(Element element) throws IOException {
        if (element.getTagName().equals("img")) {
            String width = element.getAttribute("width");
            if (!width.isEmpty()) {
                int widthvalue = DomHelper.extractValue(width);
                if (widthvalue <= 180) {
                    DomHelper.insertIntoStyleAttribute(element, "width:20%;");
                    DomHelper.removeAttribute(element,"width");
                    return ResumeAction.RESUME_FROM_SELF;
                }
                if (widthvalue <= 450) {
                    DomHelper.insertIntoStyleAttribute(element, "width:50%;");
                    DomHelper.removeAttribute(element,"width");
                    return ResumeAction.RESUME_FROM_SELF;
                }
                DomHelper.insertIntoStyleAttribute(element, "width:100%;");
                DomHelper.removeAttribute(element,"width");
                return ResumeAction.RESUME_FROM_SELF;
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
