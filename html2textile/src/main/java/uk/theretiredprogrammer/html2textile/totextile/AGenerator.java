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
package uk.theretiredprogrammer.html2textile.totextile;

import java.io.BufferedWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class AGenerator extends TextileGenerator {

    private final static String RELATIVE_PREFIX = "https://exe-sailing-club.org/";

    public String[] allowedAttributes() {
        return new String[]{"style", "class", "id", "href"};
    }

    public void write(Element element, String name, NamedNodeMap attributes, NodeList children, BufferedWriter out) throws IOException {
        out.write("\"");
        writeClassStyleId(attributes, out);
        ToTextile.processChildren(children, out);
        out.write("\":");
        out.write(getURL(isAttribute(name, "href", attributes)));
        out.write(" ");
    }

    private String getURL(String hrefvalue) {
        return hrefvalue.startsWith("https://") || hrefvalue.startsWith("mailto:") || hrefvalue.startsWith("tel:")
                ? hrefvalue : RELATIVE_PREFIX + hrefvalue;
    }
}
