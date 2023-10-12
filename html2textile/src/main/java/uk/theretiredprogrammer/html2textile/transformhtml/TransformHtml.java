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
import java.io.Reader;
import java.io.Writer;
import javax.xml.XMLConstants;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.html2textile.transformhtml.DomModifications.SubsequentWalkAction;

public class TransformHtml {

// STAGE 2 - 'Optimise' the html file structure to be:
//              acceptable to Textile limitations
//              optimise html constructs
//              remove usage patterns whish are redundent or poorly inserted by cut & paste of other formats
//
    private final Element root;
    private int level = 0;

    public TransformHtml(Reader input) throws IOException, ParserConfigurationException, SAXException {
        root = newInstance().newDocumentBuilder().parse(new InputSource(input)).getDocumentElement();
    }

    public void transform() {
        transform(new IndentAndReturnsRemoval());
        transform(new StyleNormalisation());
        transform(new DivRlStyleRemoval());
        transform(new DivReduction());
        transform(new StyleReduction());
        transform(new Style2u());
        transform(new Style2strong());
        transform(new NullSpanRemoval());
        transform(new StyleMerge());
        transform(new NullAttributeRemoval());
        transform(new ElementTrailingSpaceRemoval());
        transform(new ReplaceWithHeadings());
        transform(new BlankElementRemoval());
    }

    public void writeHtml(Writer output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(root.getOwnerDocument()), new StreamResult(output));
    }

    public String getSerialisedDOM() {
        DomSerialisation rules = new DomSerialisation();
        transform(rules);
        return rules.getContent();
    }

    public Element getRoot() {
        return root;
    }

    void transform(DomModifications transformrules) {
        Node next = root;
        level = 0;
        while ((next = processNode(next, transformrules, level)) != null) {
        }
    }

    private Node processNode(Node node, DomModifications transformrules, int level) {
        Node parentnode = node.getParentNode();
        return switch (node.getNodeType()) {
            case TEXT_NODE ->
                nextSiblingNode(node, parentnode, transformrules.testTextAndModify(node, level));
            case ELEMENT_NODE ->
                nextChildNode(node, parentnode, transformrules.testElementAndModify((Element) node, level));
            default ->
                nextSiblingNode(node, parentnode, SubsequentWalkAction.CONTINUE_WALK);
        };
    }

    private Node nextSiblingNode(Node node, Node parentnode, SubsequentWalkAction SubsequentWalkAction) {
        switch (SubsequentWalkAction) {
            case RESTART_WALK_FROM_ROOT:
                level = 0;
                return root;
            case RESTART_WALK_FROM_PARENT:
                level--;
                return parentnode;
            case CONTINUE_WALK:
            default:
                return findSiblingNode(node);
        }
    }

    private Node findSiblingNode(Node node) {
        Node next;
        while ((next = node.getNextSibling()) == null) {
            node = node.getParentNode();
            level--;
            if (level == 0 || node == null) {
                return null;
            }
        }
        return next;
    }

    private Node nextChildNode(Node node, Node parentnode, SubsequentWalkAction SubsequentWalkAction) {
        switch (SubsequentWalkAction) {
            case RESTART_WALK_FROM_ROOT:
                level = 0;
                return root;
            case RESTART_WALK_FROM_PARENT:
                level--;
                return parentnode;
            case CONTINUE_WALK:
            default:
                Node next = node.getFirstChild();
                if (next == null) {
                    return findSiblingNode(node);
                } else {
                    level++;
                    return next;
                }
        }
    }
}
