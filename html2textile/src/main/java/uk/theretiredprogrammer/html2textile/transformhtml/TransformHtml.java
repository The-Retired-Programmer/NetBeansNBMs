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
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.html2textile.transformhtml.DomModifications.Outcome;

public class TransformHtml {

// STAGE 2 - 'Optimise' the html file structure to be:
//              acceptable to Textile limitations
//              optimise html constructs
//              remove usage patterns whish are redundent or poorly inserted by cut & paste of other formats
//
    public static Element transform(Reader input) throws IOException, SAXException, ParserConfigurationException {
        TransformHtml transformer = new TransformHtml(input);
        transformer.transform(new DivRlStyleRemoval());
        return transformer.root;
    }

    final Element root;

    public TransformHtml(Reader input) throws IOException, ParserConfigurationException, SAXException {
        root = newInstance().newDocumentBuilder().parse(new InputSource(input)).getDocumentElement();
    }

    private int level = 0;
    
    void transform(DomModifications transformrules) {
        Node next = root;
        level = 0;
        while ((next = processNode(next, transformrules, level))!= null) {}
    }
    
    
    private Node processNode(Node node, DomModifications transformrules, int level) {
        Node parentnode = node.getParentNode();
        switch (node.getNodeType()) {
            case TEXT_NODE:
                return nextSiblingNode(node, parentnode, transformrules.testTextAndModify(node, level));
            case ELEMENT_NODE:
                return nextChildNode(node, parentnode, transformrules.testElementAndModify((Element) node, level));
            default:
                return nextSiblingNode(node, parentnode, Outcome.CONTINUE_SWEEP);
        }
        
    }
    
    private Node nextSiblingNode(Node node, Node parentnode, Outcome outcome) {
        switch (outcome) {
            case RESTART_SWEEP_FROM_ROOT:
                level = 0;
                return root;
            case RESTART_SWEEP_FROM_PARENT:
                level--;
                return parentnode;
            case CONTINUE_SWEEP:
            default:
                return findSiblingNode(node);
        }
    }
    
    private Node findSiblingNode(Node node) {
        Node next;
        while ((next = node.getNextSibling()) == null) {
            node = node.getParentNode();
            level--;
            if(level == 0 ||node == null) {
                return null;
            }
        }
        return next;
    }
    
    private Node nextChildNode(Node node, Node parentnode, Outcome outcome) {
        switch (outcome) {
            case RESTART_SWEEP_FROM_ROOT:
                level = 0;
                return root;
            case RESTART_SWEEP_FROM_PARENT:
                level--;
                return parentnode;
            case CONTINUE_SWEEP:
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
