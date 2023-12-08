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
import uk.theretiredprogrammer.html2textile.rules.Style;

public class ImageWidthMapping implements TransformHtmlItem {

    public ResumeAction testElementAndModify(Element element) throws IOException {
        if (element.getTagName().equals("img")) {
            StyleAttribute styles = new StyleAttribute(element);
            Style style = styles.lookup("width");
            if (style != null) {
                int widthvalue = extractValue(style.getValue());
                if (widthvalue < 0) {
                    DomHelper.removeAttribute(element,"width");
                    return ResumeAction.RESUME_FROM_NEXT;
                }
                if (widthvalue <= 180) {
                    DomHelper.insertIntoStyleAttribute(element, "width:20%;");
                    DomHelper.removeAttribute(element,"width");
                    return ResumeAction.RESUME_FROM_NEXT;
                }
                if (widthvalue <= 450) {
                    DomHelper.insertIntoStyleAttribute(element, "width:50%;");
                    DomHelper.removeAttribute(element,"width");
                    return ResumeAction.RESUME_FROM_NEXT;
                }
                DomHelper.insertIntoStyleAttribute(element, "width:100%;");
                DomHelper.removeAttribute(element,"width");
                return ResumeAction.RESUME_FROM_NEXT;
            } else {
                String width = element.getAttribute("width");
                if (!width.isEmpty()) {
                    int widthvalue = extractValue(width);
                    if (widthvalue < 0) {
                        return ResumeAction.RESUME_FROM_NEXT;
                    }
                    if (widthvalue <= 180) {
                        DomHelper.insertIntoStyleAttribute(element, "width:20%;");
                        DomHelper.removeAttribute(element,"width");
                        return ResumeAction.RESUME_FROM_NEXT;
                    }
                    if (widthvalue <= 450) {
                        DomHelper.insertIntoStyleAttribute(element, "width:50%;");
                        DomHelper.removeAttribute(element,"width");
                        return ResumeAction.RESUME_FROM_NEXT;
                    }
                    DomHelper.insertIntoStyleAttribute(element, "width:100%;");
                    DomHelper.removeAttribute(element,"width");
                    return ResumeAction.RESUME_FROM_NEXT;
                }
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
    
    public int extractValue(String value) {
        String numeric = value.strip().replaceAll("\\D*(\\d+)(%|).*", "$1$2");
        if (numeric.endsWith("%")) {
            return -1;
        }
        return numeric.isEmpty() ? 0 : Integer.parseInt(numeric);
    }
}
