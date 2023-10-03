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

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public abstract class DomModifications {
    
    public enum Outcome { RESTART_SWEEP_FROM_ROOT, RESTART_SWEEP_FROM_PARENT, CONTINUE_SWEEP };
    
    public abstract Outcome testElementAndModify(Element element, int level);
    
    public Outcome testTextAndModify(Node textnode, int level) { return Outcome.CONTINUE_SWEEP;}
    
    // ===========================================================================
    
    
    
    String getOnlyAttribute(Element element, String attributename) {
        NamedNodeMap attributes = element.getAttributes();
        Node attribute = attributes.getNamedItem(attributename);
        return attributes.getLength()==1 && attribute != null? attribute.getNodeValue():null;
    }
    
    void removeElement(Element element) {
        Node parent = element.getParentNode();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            int type= child.getNodeType();
            String name = child.getNodeName();
            String value = child.getNodeValue();
            parent.insertBefore(child, element);
        }
        parent.removeChild(element);
    }
    
    void removeNode(Node node) {
        node.getParentNode().removeChild(node);
    }
}
