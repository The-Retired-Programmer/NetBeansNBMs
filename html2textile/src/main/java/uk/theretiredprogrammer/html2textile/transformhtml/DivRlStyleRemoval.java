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

public class DivRlStyleRemoval extends DomModifications {

    public SubsequentWalkAction testElementAndModify(Element element, int level) {
        if (element.getTagName().equals("div")
                && "margin:20px20px20px20px;font-family:arial,helvetica,sans-serif;font-size:12pt;line-height:1.5em;color:#000000;"
                        .equals(getOnlyAttribute(element, "style"))) {
            removeElement(element);
            return SubsequentWalkAction.RESTART_WALK_FROM_ROOT;
        } else {
            return SubsequentWalkAction.CONTINUE_WALK;
        }
    }
}