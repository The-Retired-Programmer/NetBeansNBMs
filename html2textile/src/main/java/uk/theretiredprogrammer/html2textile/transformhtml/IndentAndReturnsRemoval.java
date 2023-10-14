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

public class IndentAndReturnsRemoval extends DomModifications {

    @Override
    public ResumeAction testElementAndModify(Element element) {
        Node child = element.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();
            if (child.getNodeType() == TEXT_NODE && isFilterable(child.getNodeValue())) {
                removeNode(child);
            }
            child = next;
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }

    private boolean isFilterable(String text) {
        return text.contains("\n") || text.contains("\r")
                ? text.replace("\n", " ").replace("\r", " ").replace("\t", " ").strip().equals("")
                : false;
    }
}
