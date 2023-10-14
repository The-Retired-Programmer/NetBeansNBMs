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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TransformHtml {

// STAGE 2 - 'Optimise' the html file structure to be:
//              acceptable to Textile limitations
//              optimise html constructs
//              remove usage patterns whish are redundent or poorly inserted by cut & paste of other formats
//
    private final Element root;

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
        transform(new BlankElementRemoval());
        transform(new RestuctureLeadingAndTrailingWhiteSpaceFromBracketingElements());
        transform(new BlockElementTrailingSpaceRemoval());
        transform(new ReplaceWithHeadings());
        transform(new ListConcatonation());
        transform(new RemoveTrailingBr());
        transform(new EmptyParaRemoval());
    }

    public void writeHtml(Writer output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(root.getOwnerDocument()), new StreamResult(output));
    }

    public Element getRoot() {
        return root;
    }

    void transform(DomModifications transformrules) {
        State state =new State();
        do {
            switch (transformrules.testElementAndModify(state.current)){
                case RESUME_FROM_ROOT -> state.root();
                //case RESUME_FROM_SELF - no state change
                case RESUME_FROM_PARENT -> state.parent();
                case RESUME_FROM_FIRST_SIBLING -> state.firstSibling();
                case RESUME_FROM_PREVIOUS -> state.previous();
                case RESUME_FROM_NEXT -> state.next();
            }
        } while (state.current != null);
    }

    private class State {

        private final Element root;
        private Element parent;
        private Element previous;
        private Element current;
        
        public State() {
            root = TransformHtml.this.root;
            root();
        }
            
        public final boolean root() {
            current = root;
            parent = null;
            previous = null;
            return current != null;
        }

        public boolean next() {
            previous = current;
            setCurrentToNext();
            setParent();
            return current != null;
        }

        public boolean previous() {
            current = previous;
            previous = null;
            setParent();
            return current != null;
        }
        
        public boolean firstSibling() {
            previous = parent;
            current = findFirstSiblingElement(current);
            setParent();
            return current != null;
        }

        public boolean parent() {
            previous = null;
            current = parent;
            setParent();
            return current != null;
        }

        private void setParent() {
            if (current == null) {
                parent = null;
            } else {
                Node p = current.getParentNode();
                parent = p.getNodeType() == ELEMENT_NODE ? (Element) p : null;
            }
        }
        
        private void setCurrentToNext() {
            Element parentofnextSiblings = current.hasChildNodes() ? current : parent;
            Element next = findNextSiblingElement(current.hasChildNodes() ? current.getFirstChild() : current.getNextSibling());
            while (next == null && parentofnextSiblings != null) {
                Node p = parentofnextSiblings.getParentNode();
                if (p.getNodeType() != ELEMENT_NODE) {
                    parentofnextSiblings = null;
                } else {
                    next = findNextSiblingElement(parentofnextSiblings.getNextSibling());
                    parentofnextSiblings = (Element) p;
                }
            }
            current = next;
        }
    
        private Element findNextSiblingElement(Node node) {
            while (node != null && node.getNodeType() != ELEMENT_NODE) {
                node = node.getNextSibling();
            }
            return (Element) node;
        }
        
        private Element findFirstSiblingElement(Element element) {
            return findNextSiblingElement(element.getParentNode().getFirstChild());
        }
    }
}
