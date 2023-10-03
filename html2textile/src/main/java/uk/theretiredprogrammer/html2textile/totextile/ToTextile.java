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
package uk.theretiredprogrammer.html2textile.totextile;

import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;
import org.w3c.dom.NodeList;
import uk.theretiredprogrammer.util.UserReporting;

public class ToTextile {

    // STAGE 3 - Translate the html file to Textile markup.
    public static void translate(Element element, BufferedWriter out) throws IOException {
        TextileGenerator generator = TextileGenerator.factory(element.getTagName());
        generator.write(element, element.getTagName(), element.getAttributes(), element.getChildNodes(), out);
    }

    static void writeAttributes(NamedNodeMap attributes, BufferedWriter out) throws IOException {
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            out.write(" " + attribute.getNodeName() + "=\"" + attribute.getNodeValue() + "\" ");
        }
    }

    static void processChildren(NodeList children, BufferedWriter out) throws IOException {
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == TEXT_NODE) {
                String text = child.getNodeValue();
                if (!isFilterable(text)) {
                    out.write(applytailfilter(text));
                }
            }
            if (child.getNodeType() == ELEMENT_NODE) {
                translate((Element) child, out);
            }
        }
    }

    private static boolean isFilterable(String text) {
        return text.contains("\n")
                ? text.replace("\n", " ").replace("\t", " ").strip().equals("")
                : false;
    }

    private static String applytailfilter(String text) {
        boolean nlseen = false;
        for (int i = text.length() - 1; i > -1; i--) {
            char c = text.charAt(i);
            if (c == '\n') {
                nlseen = true;
            }
            if (!(c == '\n' || c == ' ' || c == '\t')) {
                return nlseen ? text.substring(0, i + 1) + "\n" : text;
            }
        }
        return nlseen ? "\n" : "";
    }

    static int findlistdepth(Element element) {
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

    static boolean isparentlisttypeOL(Element element) {
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
        UserReporting.warning("Html to Textile conversion", "Cannot find parent Ul/OL for this list element");
        return false;
    }
}
