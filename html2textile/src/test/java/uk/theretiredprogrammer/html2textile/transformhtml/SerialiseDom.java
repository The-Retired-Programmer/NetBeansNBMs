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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

public class SerialiseDom {
    
    private static final String INDENTTEXT = "    ";
    //
   private final StringBuilder sb;
   private int indentlevel;
   
    public static String serialise(Element root) {
        return new SerialiseDom().getSerial(root);
    }
    
    private SerialiseDom() {
        sb = new StringBuilder();
        indentlevel = 0;
    }
    
    private String getSerial(Element root) {
        elementSerialise(root);
        return sb.toString();
    }
    
    private void elementSerialise(Element element) {
        sb.append(INDENTTEXT.repeat(indentlevel));
        sb.append(element.getTagName());
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            sb.append(' ');
            sb.append(attribute.getNodeName());
            sb.append("=\"");
            sb.append(attribute.getNodeValue());
            sb.append('"');
        }
        sb.append('\n');
        //
        indentlevel++;
        childrenSerialise(element);
        indentlevel--;
    }
    
    private void childrenSerialise(Element parent) {
        Node child = parent.getFirstChild();
        while (child != null) {
            switch (child.getNodeType()) {
                case TEXT_NODE -> {
                    sb.append(INDENTTEXT.repeat(indentlevel));
                    sb.append("\"");
                    sb.append(child.getNodeValue()
                            .replace("\n", "\\n").replace("\t", "\\t")
                            .replace("\r", "\\r").replace("\"", "\\\"")
                    );
                    sb.append("\"\n");
                }
                case ELEMENT_NODE -> elementSerialise((Element) child);
            }
            child = child.getNextSibling();
        }
    }
}
