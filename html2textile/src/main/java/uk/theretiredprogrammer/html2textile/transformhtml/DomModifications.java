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
    void insertIntoStyle(Element element, String rule) {
        String style = element.getAttribute("style");
        element.setAttribute("style", style == null ? rule : style + rule);
    }

    void mergeElementsRemovingChild(Element element, Element child) {
        mergeAttributes(element, child);
        removeElement(child);
    }

    void mergeElementsRemovingElement(Element element, Element child) {
        mergeAttributes(element, child);
        moveAttributes(element, child);
        removeElement(element);
    }

    private void mergeAttributes(Element parent, Element child) {
        if (child.hasAttributes()) {
            NamedNodeMap attributes = child.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                String attrname = attr.getNodeName();
                switch (attrname) {
                    case "class" -> {
                        if (parent.hasAttribute(attrname)) {
                            parent.setAttribute(attrname, parent.getAttribute(attrname) + ' ' + attr.getNodeValue());
                        } else {
                            parent.setAttribute(attrname, attr.getNodeValue());
                        }
                    }
                    case "style" -> {
                        if (parent.hasAttribute(attrname)) {
                            parent.setAttribute(attrname, parent.getAttribute(attrname) + attr.getNodeValue());
                        } else {
                            parent.setAttribute(attrname, attr.getNodeValue());
                        }
                    }
                    default ->
                        parent.setAttribute(attrname, attr.getNodeValue());
                }
            }
        }
    }

    private void moveAttributes(Element from, Element to) {
        clearAttributes(to);
        copyAttributes(from, to);
    }

    private void clearAttributes(Element element) {
        while (element.hasAttributes()) {
            NamedNodeMap attributes = element.getAttributes();
            attributes.removeNamedItem(attributes.item(0).getNodeName());
        }
    }

    private void copyAttributes(Element from, Element to) {
        if (from.hasAttributes()) {
            NamedNodeMap attributes = from.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                to.setAttribute(attr.getNodeName(), attr.getNodeValue());
            }
        }
    }

    void removeElement(Element element) {
        Node parent = element.getParentNode();
        if (element.hasChildNodes()) {
            Node child = element.getFirstChild();
            while (child != null) {
                Node nextchild = child.getNextSibling();
                parent.insertBefore(child, element);
                child = nextchild;
            }
        }
        parent.removeChild(element);
    }

    void removeElementMoveChildrenTo(Element toremove, Element toinsert) {
        if (toremove.hasChildNodes()) {
            Node child = toremove.getFirstChild();
            while (child != null) {
                Node nextchild = child.getNextSibling();
                toinsert.appendChild(child);
                child = nextchild;
            }
        }
        toremove.getParentNode().removeChild(toremove);
    }

    void replaceElement(Element element, String newname) {
        Element newElement = createElementNode(element.getOwnerDocument(), newname, element.getAttributes(), element.getChildNodes());
        Node parent = element.getParentNode();
        parent.insertBefore(newElement, element);
        parent.removeChild(element);
    }

    void replaceElement(Element element, String newname, Element elementwithchildren) {
        Element newElement = createElementNode(element.getOwnerDocument(), newname, element.getAttributes(), elementwithchildren.getChildNodes());
        Node parent = element.getParentNode();
        parent.insertBefore(newElement, element);
        parent.removeChild(element);
    }

    void insertChildElement(Element element, String newname) {
        Element newElement = createElementNode(element.getOwnerDocument(), newname, null, element.getChildNodes());
        element.appendChild(newElement);
    }

    private Element createElementNode(Document doc, String tagname, NamedNodeMap attributes, NodeList children) {
        Element element = doc.createElement(tagname);
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                element.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
            }
        }
        if (children != null) {
            if (children.getLength() != 0) {
                Node child = children.item(0);
                while (child != null) {
                    Node nextchild = child.getNextSibling();
                    element.appendChild(child);
                    child = nextchild;
                }
            }
        }
        return element;
    }

    void insertTextBefore(Node element, String text) {
        element.getParentNode().insertBefore(element.getOwnerDocument().createTextNode(text), element);
    }

    void insertTextAfter(Node element, String text) {
        element.getParentNode().insertBefore(element.getOwnerDocument().createTextNode(text), element.getNextSibling());
    }

    void removeNode(Node node) {
        node.getParentNode().removeChild(node);
    }

    void removeAttribute(Element element, String attributename) {
        element.removeAttribute(attributename);
    }

    void replaceAttributeValue(Element element, String attributename, String newvalue) {
        element.setAttribute(attributename, newvalue);
    }
}
