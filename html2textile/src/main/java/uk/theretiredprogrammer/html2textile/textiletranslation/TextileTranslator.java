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
package uk.theretiredprogrammer.html2textile.textiletranslation;

import java.io.PrintWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;
import uk.theretiredprogrammer.html2textile.ErrHandler;

public class TextileTranslator {

    private final PrintWriter out;
    private final Element root;
    private final ErrHandler err;

    // STAGE 3 - Translate the html file to Textile markup.
    public TextileTranslator(Element root, PrintWriter out, ErrHandler err) {
        this.out = out;
        this.root = root;
        this.err = err;
    }

    public void translate() throws IOException {
        translate(root);
    }

    void translate(Element element) throws IOException {
        TextileElementTranslator translator = TextileElementTranslator.factory(element, out, err);
        translator.write(element, false, this);
    }

    private void translate(Element element, boolean isParentTerminatorContext) throws IOException {
        TextileElementTranslator translator = TextileElementTranslator.factory(element, out, err);
        translator.write(element, isParentTerminatorContext, this);
    }

    void writeAttributes(Element element) throws IOException {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            out.write(" " + attribute.getNodeName() + "=\"" + attribute.getNodeValue() + "\" ");
        }
    }

    void processChildren(Element element) throws IOException {
        Node child = element.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == TEXT_NODE) {
                out.write(child.getNodeValue());
            }
            if (child.getNodeType() == ELEMENT_NODE) {
                translate((Element) child);
            }
            child = child.getNextSibling();
        }
    }

    void processChildrenInTerminatorContext(Element element) throws IOException {
        Node child = element.getFirstChild();
        while (child != null) {
            Node nextchild = child.getNextSibling();
            if (child.getNodeType() == TEXT_NODE) {
                out.write(child.getNodeValue());
            }
            if (child.getNodeType() == ELEMENT_NODE) {
                translate((Element) child, nextchild == null);
            }
            child = nextchild;
        }
    }

    int findlistdepth(Element element) {
        int count = 0;
        Node node = element;
        while (node != null) {
            if (node.getNodeType() == ELEMENT_NODE) {
                String name = node.getNodeName().toLowerCase();
                if (name.equals("ul") || name.equals("ol")) {
                    count++;
                }
            }
            node = node.getParentNode();
        }
        return count;
    }

    boolean isparentlisttypeOL(Element element) {
        Node node = element.getParentNode();
        while (node != null) {
            if (node.getNodeType() == ELEMENT_NODE) {
                String name = node.getNodeName().toLowerCase();
                if (name.equals("ul")) {
                    return false;
                }
                if (name.equals("ol")) {
                    return true;
                }
            }
            node = node.getParentNode();
        }
        err.error("Cannot find parent Ul/OL for this list element", element);
        return false;
    }
}
