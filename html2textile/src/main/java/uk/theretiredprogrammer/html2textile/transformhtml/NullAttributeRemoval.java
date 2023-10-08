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

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NullAttributeRemoval extends DomModifications {

    public SubsequentWalkAction testElementAndModify(Element element, int level) {
        if (element.hasAttributes()) {
            List<String> namestoremove = new ArrayList<>();
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                if (attribute.getNodeValue().isEmpty()) {
                    namestoremove.add(attribute.getNodeName());
                }
            }
            for (var name : namestoremove) {
                element.removeAttribute(name);
            }
        }
        return SubsequentWalkAction.CONTINUE_WALK;
    }
}
