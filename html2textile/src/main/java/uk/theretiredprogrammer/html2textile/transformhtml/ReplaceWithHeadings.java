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

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ReplaceWithHeadings implements TransformHtmlItem {

    public ResumeAction testElementAndModify(Element element) {
        if (element.getTagName().equals("p")) {
            return scanthroughotherformatting(element);
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }

    private ResumeAction scanthroughotherformatting(Element element) {
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
            removefontsizestylerule(element);
            Element h = DomHelper.createElement(headername, element);
            DomHelper.appendAttributes(h, element.getAttributes());
            DomHelper.appendChildren(h,  nextlevelelement.getChildNodes());
            DomHelper.replaceNode(element, h);
        }
        return headername == null ? ResumeAction.RESUME_FROM_NEXT : ResumeAction.RESUME_FROM_PARENT;
    }

    private boolean isHeaderReductionElement(Element element) {
        String name = element.getTagName();
        return name.equals("p") || name.equals("strong") || name.equals("u") || name.equals("span");
    }

    private void removefontsizestylerule(Element element) {
        Node style = element.getAttributeNode("style");
        if (style == null) {
            return;
        }
        String newrules = "";
        String[] rules = style.getNodeValue().strip().split(";");
        for (String rule : rules) {
            if (!rule.isBlank()) {
                if (!rule.startsWith("font-size:")) {
                    newrules = newrules + rule + ";";
                }
            }
        }
        if (newrules.isBlank()) {
            DomHelper.removeAttribute(element, "style");
        } else {
            DomHelper.replaceAttribute(element, new Attribute("style", newrules));
        }
    }

    private String headerpatternmatch(List<Element> elements) {
        return switch (scanforfontsize(elements)) {
            case 1 -> "h4";
                
            case 2 -> "h3";
                
            default -> null;
        };
    }

    private int scanforfontsize(List<Element> elements) {
        int fontgroup = 0;
        for (var element : elements) {
            int fntgrp = getFontSizeGroup(element);
            if (fntgrp != -1) {
                fontgroup = fntgrp;
            }
        }
        return fontgroup;
    }

    private int getFontSizeGroup(Element element) {
        int fontgroup = -1;
        Node style = element.getAttributeNode("style");
        if (style != null) {
            String[] stylerules = style.getNodeValue().split(";");
            for (String rule : stylerules) {
                if (!rule.isBlank()) {
                    String[] pair = rule.split(":");
                    if (pair[0].strip().equals("font-size")) {
                        switch (pair[1].strip()) {
                            case "13pt":
                            case "14pt":
                            case "15pt":
                                fontgroup = 1;
                                break;
                            case "16pt":
                            case "17pt":
                            case "18pt":
                                fontgroup = 2;
                                break;
                            case "12pt":
                            default:
                                fontgroup = 0;
                                break;
                        }
                    }
                }
            }
        }
        return fontgroup;
    }
}
