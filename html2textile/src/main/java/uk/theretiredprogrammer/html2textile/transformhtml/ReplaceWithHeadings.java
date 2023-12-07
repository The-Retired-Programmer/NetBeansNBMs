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
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import uk.theretiredprogrammer.html2textile.rules.StyleAttribute;
import uk.theretiredprogrammer.html2textile.rules.Style;

public class ReplaceWithHeadings implements TransformHtmlItem {

    public ResumeAction testElementAndModify(Element element) throws IOException {
        if (element.getTagName().equals("p")) {
            return scanthroughotherformatting(element);
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }

    private ResumeAction scanthroughotherformatting(Element element) throws IOException {
        List<Element> elements = new ArrayList<>();
        elements.add(element);
        Element nextlevelelement = element;
        while (true) {
            Element nextlevel = DomHelper.getOnlyChildElementSkippingLine(nextlevelelement);
            if (nextlevel == null) {
                break;
            }
            if (!isHeaderReductionElement(nextlevel)) {
                break;
            }
            elements.add(nextlevel);
            nextlevelelement = nextlevel;
        }
        String headername = headerpatternmatch(elements);
        if (headername != null) {
            removefontsizestylerules(elements);
            Element h = DomHelper.createElement(headername, element);
            DomHelper.appendAttributes(h, element.getAttributes());
            DomHelper.appendChildren(h, nextlevelelement.getChildNodes());
            DomHelper.replaceNode(element, h);
        }
        return headername == null ? ResumeAction.RESUME_FROM_NEXT : ResumeAction.RESUME_FROM_PARENT;
    }

    private boolean isHeaderReductionElement(Element element) {
        String name = element.getTagName();
        return name.equals("p") || name.equals("strong") || name.equals("u") || name.equals("span");
    }

    private void removefontsizestylerules(List<Element> elements) throws IOException {
        for (var element : elements) {
            StyleAttribute style = new StyleAttribute();
            if (style.extract(element)) {
                style.removeStyleRuleIfName("font-size");
                style.setStyle(element);
            }
        }
    }

    private String headerpatternmatch(List<Element> elements) throws IOException {
        return switch (scanforfontsize(elements)) {
            case 1 ->
                "h4";

            case 2 ->
                "h3";

            default ->
                null;
        };
    }

    private int scanforfontsize(List<Element> elements) throws IOException {
        int fontgroup = 0;
        for (var element : elements) {
            int fntgrp = getFontSizeGroup(element);
            if (fntgrp != -1) {
                fontgroup = fntgrp;
            }
        }
        return fontgroup;
    }

    private int getFontSizeGroup(Element element) throws IOException {
        StyleAttribute style = new StyleAttribute();
        if (style.extract(element)) {
            Style sr = style.lookup("font-size");
            return sr == null ? -1 : switch (sr.getValue()) {
                case "13pt" ->
                    1;
                case "14pt" ->
                    1;
                case "15pt" ->
                    1;
                case "16pt" ->
                    2;
                case "17pt" ->
                    2;
                case "18pt" ->
                    2;
                case "12pt" ->
                    0;
                default ->
                    -1;
            };
        }
        return -1;
    }
}
