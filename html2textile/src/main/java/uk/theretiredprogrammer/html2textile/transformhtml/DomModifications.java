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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;
import org.w3c.dom.NodeList;

public abstract class DomModifications {

    public enum ResumeAction {
        RESUME_FROM_ROOT, RESUME_FROM_SELF, RESUME_FROM_PARENT, RESUME_FROM_NEXT, RESUME_FROM_PREVIOUS, RESUME_FROM_FIRST_SIBLING
    };

    public abstract ResumeAction testElementAndModify(Element element) throws IOException;

    // ===========================================================================
    int extractValue(String value) {
        String numeric = value.replaceAll("\\D*(\\d+).*", "$1");
        return numeric.isEmpty() ? 0 : Integer.parseInt(numeric);
    }

    String getOnlyAttribute(Element element, String attributename) {
        NamedNodeMap attributes = element.getAttributes();
        Node attribute = attributes.getNamedItem(attributename);
        return attributes.getLength() == 1 && attribute != null ? attribute.getNodeValue() : null;
    }

    Element getOnlyChildSpanElement(Element element) {
        NodeList children = element.getChildNodes();
        if (children.getLength() != 1) {
            return null;
        }
        Node child = children.item(0);
        return child.getNodeType() == ELEMENT_NODE && child.getNodeName().equals("span")
                ? (Element) child : null;
    }
    
    boolean hasChildNodesSkippingLine(Element element) {
        if (element.hasChildNodes()) {
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == TEXT_NODE) {
                    return true;
                }
                if (child.getNodeType() == ELEMENT_NODE && (!child.getNodeName().equals("line"))) {
                    return true;
                }
            }
        }
        return false;
    }

    Element getOnlyChildElementSkippingLine(Element element) {
        if (!element.hasChildNodes()) {
            return null;
        }
        NodeList children = element.getChildNodes();
        int elementcount = 0;
        Element el = null;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == ELEMENT_NODE) {
                if (!child.getNodeName().equals("line")) {
                    elementcount++;
                    el = (Element) child;
                }
            }
        }
        return elementcount == 1 ? el : null;
    }

    boolean areAllChildrenBlockElements(Element element) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (!isBlockElement(child)) {
                return false;
            }
        }
        return true;
    }

    public boolean isBlockElement(Node node) {
        if (node.getNodeType() == ELEMENT_NODE) {
            String name = node.getNodeName();
            return name.equals("p") || name.equals("ul") || name.equals("ol") || name.equals("li")
                    || name.equals("h1") || name.equals("h2") || name.equals("h3")
                    || name.equals("h4") || name.equals("h5") || name.equals("h6")
                    || name.equals("table") || name.equals("tbody") || name.equals("tr")
                    || name.equals("html") || name.equals("div") || name.equals("line");
        } else {
            return false;
        }
    }
    
    public boolean isMergableBlockElement(Node node) {
        if (node.getNodeType() == ELEMENT_NODE) {
            String name = node.getNodeName();
            return name.equals("p") || name.equals("div") 
                    || name.equals("h1") || name.equals("h2") || name.equals("h3")
                    || name.equals("h4") || name.equals("h5") || name.equals("h6");
                    
        } else {
            return false;
        }
    }

    boolean isBracketingElement(Element element) {
        String name = element.getTagName();
        return name.equals("strong") || name.equals("u") || name.equals("span") || name.equals("sub")
                || name.equals("sup") || name.equals("b") || name.equals("a");
    }

    Element nextSiblingIsElementSkippingLine(Element element, String name) {
        Node next = element;
        do {
            next = next.getNextSibling();
            if (next == null || next.getNodeType() != ELEMENT_NODE) {
                return null;
            }
            if (next.getNodeName().equals(name)) {
                return (Element) next;
            }
        } while (next.getNodeName().equals("line"));
        return null;
    }
    
    Element nextElementSiblingSkippingLine(Element element) {
        Node next = element;
        while ((next = next.getNextSibling())!= null) {
            if (next.getNodeType() == ELEMENT_NODE) {
                if (!next.getNodeName().equals("line")) {
                    return (Element) next;
                }
            }
        }
        return null;
    }

    boolean nextSiblingIsText(Element element) {
        Node next = element.getNextSibling();
        return next != null && next.getNodeType() == TEXT_NODE;
    }

    //
    // Dom manipulation methods
    //
    
    Element createElement(String tagname, Element anydocumentelement) {
        return anydocumentelement.getOwnerDocument().createElement(tagname);
    }
    
    void appendChildren(Element parent, NodeList children) {
        if (children != null) {
            if (children.getLength() != 0) {
                Node child = children.item(0);
                while (child != null) {
                    Node nextchild = child.getNextSibling();
                    parent.appendChild(child);
                    child = nextchild;
                }
            }
        }
    }
    
    void appendChild(Node parent, Node child) {
        parent.appendChild(child);
    }
    
    void insertBeforeNode(Node node, Node insert) {
        node.getParentNode().insertBefore(insert, node);
    }
    
    void insertBeforeNode(Node node, NodeList inserts) {
        Node parent = node.getParentNode();
        Node child = inserts.item(0);
        while (child != null) {
            Node nextchild = child.getNextSibling();
            parent.insertBefore(child, node);
            child = nextchild;
        }
    }

    void insertAfterNode(Node node, Node insert) {
        node.getParentNode().insertBefore(insert, node.getNextSibling());
    }

    void insertBeforeNode(Node node, String insert) {
        insertBeforeNode(node, node.getOwnerDocument().createTextNode(insert));
    }

    void insertAfterNode(Node node, String insert) {
        insertAfterNode(node, node.getOwnerDocument().createTextNode(insert));
    }
    
    void replaceNode(Node oldnode, Node newnode) {
        Node parent = oldnode.getParentNode();
        parent.insertBefore(newnode, oldnode);
        parent.removeChild(oldnode);
    }

    void removeNode(Node node) {
        node.getParentNode().removeChild(node);
    }
    
     void appendAttributes(Element element, Attribute[] attributes) {
        for (Attribute attribute : attributes) {
            element.setAttribute(attribute.name, attribute.value);
        }
    }
    
    void appendAttributes(Element element, NamedNodeMap attributes) {
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                element.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
            }
        }
    }
    
    void insertIntoStyleAttribute(Element element, String rule) {
        String style = element.getAttribute("style");
        element.setAttribute("style", style == null ? rule : style + rule);
    }


    void removeAttribute(Element element, String attributename) {
        element.removeAttribute(attributename);
    }
    
    void removeAttributes(Element element) {
        while (element.hasAttributes()) {
            NamedNodeMap attributes = element.getAttributes();
            attributes.removeNamedItem(attributes.item(0).getNodeName());
        }
    }

    void replaceAttribute(Element element, Attribute replacement) {
        element.setAttribute(replacement.name, replacement.value);
    }
    
    
    void mergeAttributes(Element child, Element target) {
        if (child.hasAttributes()) {
            NamedNodeMap attributes = child.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                String attrname = attr.getNodeName();
                switch (attrname) {
                    case "class" -> {
                        if (target.hasAttribute(attrname)) {
                            target.setAttribute(attrname, target.getAttribute(attrname) + ' ' + attr.getNodeValue());
                        } else {
                            target.setAttribute(attrname, attr.getNodeValue());
                        }
                    }
                    case "style" -> {
                        if (target.hasAttribute(attrname)) {
                            target.setAttribute(attrname, target.getAttribute(attrname) + attr.getNodeValue());
                        } else {
                            target.setAttribute(attrname, attr.getNodeValue());
                        }
                    }
                    default ->
                        target.setAttribute(attrname, attr.getNodeValue());
                }
            }
        }
    }
    
    
    public class Attribute {
        
        public final String name;
        public final String value;
        
        public Attribute(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
