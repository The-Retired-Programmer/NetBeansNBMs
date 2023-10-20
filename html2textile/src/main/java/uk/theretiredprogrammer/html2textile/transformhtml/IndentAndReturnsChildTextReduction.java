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

public class IndentAndReturnsChildTextReduction extends DomModifications {

    @Override
    public ResumeAction testElementAndModify(Element element) {
        Node previous = null;
        Node next = element.getFirstChild();
        while (next != null) {
            removeLeadingWhiteSpaceIfTextAfterElement(previous, next);
            previous = next;
            next = next.getNextSibling();
        } 
        Node following = null;
        next = element.getLastChild();
        while (next!= null) {
            removeTrailingWhiteSpaceIfTextBeforeElement(next, following);
            following = next;
            next = next.getPreviousSibling();
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
    
    private void removeLeadingWhiteSpaceIfTextAfterElement(Node previous, Node child) {
        if (previous == null || previous.getNodeType() == ELEMENT_NODE) {
            if (child != null && child.getNodeType() == TEXT_NODE) {
                String value = child.getNodeValue();
                String replace = value.replaceFirst("^\\n(\\s\\s\\s\\s)*(.*)", "$2");
                child.setNodeValue(replace);
            }
        }
    }
    
    private void removeTrailingWhiteSpaceIfTextBeforeElement(Node child, Node following) {
        if (following == null || following.getNodeType() == ELEMENT_NODE) {
            if (child != null && child.getNodeType() == TEXT_NODE) {
                child.setNodeValue(child.getNodeValue().replaceFirst("(.*)\\n(\\s\\s\\s\\s)*$","$1"));
            }
        }
    }

}
