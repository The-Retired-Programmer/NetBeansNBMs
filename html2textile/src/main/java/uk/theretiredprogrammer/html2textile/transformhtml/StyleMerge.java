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

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class StyleMerge extends DomModifications {

    public ResumeAction testElementAndModify(Element element) {
        if (isBlockElement(element)) {
            Element span = getOnlyChildSpanElement(element);
            if (span != null) {
                mergeAttributes(element, span);
                return ResumeAction.RESUME_FROM_SELF;
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }

    private void mergeAttributes(Element parent, Element child) {
        Map<String, String> attributes = new HashMap<>();
        Map<String, String> stylerules = new HashMap<>();
        loadAttributes(parent, attributes, stylerules);
        loadAttributes(child, attributes, stylerules);
        replaceAttributes(parent, attributes, stylerules);
        removeElement(child);
    }

    private void loadAttributes(Element element, Map<String, String> attributesmap, Map<String, String> stylerulesmap) {
        if (!element.hasAttributes()) {
            return;
        }
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            String name = attribute.getNodeName();
            if (name.equals("style")) {
                loadStyleRules(attribute.getNodeValue(), stylerulesmap);
            } else {
                attributesmap.put(name, attribute.getNodeValue());
            }
        }
    }

    private void loadStyleRules(String style, Map<String, String> stylerulesmap) {
        String[] rules = style.split(";");
        for (String rule : rules) {
            String[] pair = rule.split(":");
            stylerulesmap.put(pair[0], pair[1]);
        }
    }

    private void replaceAttributes(Element element, Map<String, String> attributesmap, Map<String, String> stylerulesmap) {
        clearAttributes(element);
        String styleattributevalue = createStyleAttributeValue(stylerulesmap);
        if (!styleattributevalue.isBlank()) {
            element.setAttribute("style", createStyleAttributeValue(stylerulesmap));
        }
        for (var entry : attributesmap.entrySet()) {
            element.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    private void clearAttributes(Element element) {
        if (element.hasAttributes()) {
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                String name = attribute.getNodeName();
                element.removeAttribute(name);
            }
        }
    }

    private String createStyleAttributeValue(Map<String, String> stylerulesmap) {
        String value = "";
        for (var entry : stylerulesmap.entrySet()) {
            value = value + entry.getKey() + ":" + entry.getValue() + ";";
        }
        return value;
    }
}
