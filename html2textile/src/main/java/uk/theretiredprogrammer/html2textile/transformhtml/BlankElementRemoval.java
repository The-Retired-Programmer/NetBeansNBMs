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
import static org.w3c.dom.Node.TEXT_NODE;
import org.w3c.dom.NodeList;

public class BlankElementRemoval extends DomModifications {

    public SubsequentWalkAction testElementAndModify(Element element, int level) {
        if (isBlankElementRemovalElement(element)) {
            if (isNoChildOrOnlyChildBlankText(element)) {
                removeElement(element);
                return SubsequentWalkAction.RESTART_WALK_FROM_PARENT;
            }
        }
        return SubsequentWalkAction.CONTINUE_WALK;
    }

    private boolean isBlankElementRemovalElement(Element element) {
        String name = element.getTagName();
        return name.equals("strong") || name.equals("u") || name.equals("span") || name.equals("sub")
                || name.equals("sup") || name.equals("b");
    }

    private boolean isNoChildOrOnlyChildBlankText(Element element) {
        NodeList children = element.getChildNodes();
        if (children.getLength() == 0) {
            return true;
        }
        if (children.getLength() == 1) {
            Node child = children.item(0);
            return child.getNodeType() == TEXT_NODE && child.getNodeValue().isBlank();
        }
        return false;
    }
}
