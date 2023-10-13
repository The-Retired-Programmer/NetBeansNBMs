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

public class ListConcatonation extends DomModifications {

    public SubsequentWalkAction testElementAndModify(Element element, int level) {
        if (checkListAndConcatonate("ul", element) == SubsequentWalkAction.RESTART_WALK_FROM_SELF) {
            return SubsequentWalkAction.RESTART_WALK_FROM_SELF;
        }
        return checkListAndConcatonate("ol", element);
    }

    private SubsequentWalkAction checkListAndConcatonate(String listtag, Element element) {
        if (element.getTagName().equals(listtag)) {
            Node sibling = element.getNextSibling();
            if (sibling != null && sibling.getNodeType() == ELEMENT_NODE && sibling.getNodeName().equals(listtag)) {
                if (!element.hasAttributes() && !sibling.hasAttributes()) {
                    removeElementMoveChildrenTo((Element) sibling, element);
                    return SubsequentWalkAction.RESTART_WALK_FROM_SELF;
                }
            }
        }
        return SubsequentWalkAction.CONTINUE_WALK;
    }
}
