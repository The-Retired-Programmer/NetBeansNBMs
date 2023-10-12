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
package uk.theretiredprogrammer.html2textile.textiletranslation;

import java.io.PrintWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import uk.theretiredprogrammer.util.UserReporting;

public class IgnoredTranslator extends TextileElementTranslator {

    public IgnoredTranslator(PrintWriter out) {
        super(out);
    }

    public String[] allowedAttributes() {
        return new String[0];
    }

    public void write(Element element, boolean isParentTerminatorContext, TextileTranslator translator) throws IOException {
        NamedNodeMap attributes = element.getAttributes();
        if (attributes.getLength() != 0) {
            String message = element.getTagName() + " ignored but has attributes: ";
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                message += attr.getNodeName() + "=" + attr.getNodeValue();
                if (i != attributes.getLength() - 1) {
                    message += ", ";
                }
            }
            UserReporting.warning("Html to Textile conversion", message);
        }
        translator.processChildren(element);
    }
}
