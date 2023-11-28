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
package uk.theretiredprogrammer.html2textile.rules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class Attributes {
    
    private final List<Attribute> attributes = new ArrayList<>();
    private final Style style= new Style();
    private String classstring = "";
    
    public Attributes extract(Element element) throws IOException {
        NamedNodeMap atts = element.getAttributes();
        for (int i = 0; i < atts.getLength(); i++) {
            Node attribute = atts.item(i);
            String name = attribute.getNodeName();
            String value = attribute.getNodeValue();
            if (name.equals("style")) {
                style.extract(attribute.getNodeValue());
            } else {
                if (name.equals("class")) {
                    classstring = value;
                } else {
                    attributes.add(new Attribute(name,value));
                }
            }
        }
        return this;
    }
    
    public Attributes merge(Attributes child) {
        attributes.addAll(child.attributes);
        if (!child.classstring.isBlank()) {
            classstring = classstring+" "+child.classstring;
        }
        style.insertStyle(child.style);
        return this;
    }
    
    public Attributes replaceAttributes(Element element) {
        clearAttributes(element);
        style.setStyle(element);
        if (!classstring.isBlank()) {
            element.setAttribute("class", classstring);
        }
        for (var attribute: attributes) {
            attribute.set(element);
        }
        return this;
    }
    
    private void clearAttributes(Element element) {
        if (element.hasAttributes()) {
            NamedNodeMap atts = element.getAttributes();
            for (int i = 0; i < atts.getLength(); i++) {
                Node attribute = atts.item(i);
                String name = attribute.getNodeName();
                element.removeAttribute(name);
            }
        }
    }
}
