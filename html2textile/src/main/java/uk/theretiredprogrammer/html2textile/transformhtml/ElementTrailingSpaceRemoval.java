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
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

public class ElementTrailingSpaceRemoval extends DomModifications {
    
    public SubsequentWalkAction testElementAndModify(Element element, int level){
        if (isTrailingSpaceRemovalElement(element)) {
            Node trailingNode = element.getLastChild();
            if (trailingNode != null && trailingNode.getNodeType() == TEXT_NODE && trailingNode.getNodeValue().isBlank()) {
                removeNode(trailingNode);
            }
        }
        return SubsequentWalkAction.CONTINUE_WALK;
    }
    
    private boolean isTrailingSpaceRemovalElement(Node node) {
        if (isBlockElement(node)) {
            return true;
        }
        if (node.getNodeType() == ELEMENT_NODE) {
            String name = node.getNodeName();
            return name.equals("strong") || name.equals("u") || name.equals("span") || name.equals("sub")
                    || name.equals("sup") || name.equals("td")|| name.equals("b") || name.equals("a");
        } else {
            return false;
        }
    }
}
