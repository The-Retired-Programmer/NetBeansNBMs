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

public class RestuctureLeadingAndTrailingWhiteSpaceFromBracketingElements extends DomModifications {

    public SubsequentWalkAction testElementAndModify(Element element, int level) {
        if (isBracketingElement(element)) {
            if (adjustWS(element)) {
                return SubsequentWalkAction.RESTART_WALK_FROM_PARENT;
            }
        }
        return SubsequentWalkAction.CONTINUE_WALK;
    }

    private boolean adjustWS(Element element) {
        boolean adjusted = false;
        adjusted = adjustLeadingWS(element) ? true : adjusted;
        return adjustTrailingWS(element) ? true : adjusted;
    }

    private boolean adjustLeadingWS(Element element) {
        Node firstchild = element.getFirstChild();
        if (firstchild != null && firstchild.getNodeType() == TEXT_NODE) {
            String text = firstchild.getNodeValue();
            String leadingstrippedtext = text.stripLeading();
            int textlength = text.length();
            int leadingstrippedtextlength = leadingstrippedtext.length();
            if (textlength != leadingstrippedtextlength) {
                int wslength = textlength - leadingstrippedtextlength;
                String remainingtext = text.substring(wslength);
                if (remainingtext.isEmpty()) {
                    removeNode(firstchild);
                } else {
                    firstchild.setNodeValue(remainingtext);
                }
                insertTextBefore(element, text.substring(0, wslength));
                return true;
            }
        }
        return false;
    }

    private boolean adjustTrailingWS(Element element) {
        Node lastchild = element.getLastChild();
        if (lastchild != null && lastchild.getNodeType() == TEXT_NODE) {
            String text = lastchild.getNodeValue();
            String trailingstrippedtext = text.stripTrailing();
            int textlength = text.length();
            int trailingstrippedtextlength = trailingstrippedtext.length();
            if (textlength != trailingstrippedtextlength) {
                String remainingtext = text.substring(0, trailingstrippedtextlength);
                if (remainingtext.isEmpty()) {
                    removeNode(lastchild);
                } else {
                    lastchild.setNodeValue(remainingtext);
                }
                insertTextAfter(element, text.substring(trailingstrippedtextlength));
                return true;
            }
        }
        return false;
    }
}
