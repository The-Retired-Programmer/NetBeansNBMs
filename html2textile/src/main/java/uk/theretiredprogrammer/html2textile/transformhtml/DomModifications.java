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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import org.w3c.dom.NodeList;

public abstract class DomModifications {

    public enum SubsequentWalkAction {
        RESTART_WALK_FROM_ROOT, RESTART_WALK_FROM_SELF, RESTART_WALK_FROM_PARENT, CONTINUE_WALK
    };

    public abstract SubsequentWalkAction testElementAndModify(Element element, int level);

    public SubsequentWalkAction testTextAndModify(Node textnode, int level) {
        return SubsequentWalkAction.CONTINUE_WALK;
    }

    // ===========================================================================
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

    Element getOnlyChildElement(Element element) {
        NodeList children = element.getChildNodes();
        if (children.getLength() != 1) {
            return null;
        }
        Node child = children.item(0);
        return child.getNodeType() == ELEMENT_NODE ? (Element) child : null;
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
                    || name.equals("html") || name.equals("div");
        } else {
            return false;
        }
    }

    boolean isBracketingElement(Element element) {
        String name = element.getTagName();
        return name.equals("strong") || name.equals("u") || name.equals("span") || name.equals("sub")
                || name.equals("sup") || name.equals("b") || name.equals("a");
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
