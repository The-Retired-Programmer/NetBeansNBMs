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

public class RemoveTrailingBr extends DomModifications {

    public ResumeAction testElementAndModify(Element element) {
        if (isBlockElement(element)) {
            Node trailingNode = element.getLastChild();
            if (trailingNode != null && trailingNode.getNodeType() == ELEMENT_NODE && trailingNode.getNodeName().equals("br")) {
                removeNode(trailingNode);
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
