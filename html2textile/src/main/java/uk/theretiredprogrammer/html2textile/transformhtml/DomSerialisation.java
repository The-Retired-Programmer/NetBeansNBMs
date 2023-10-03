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

public class DomSerialisation extends DomModifications {
    
    public static String run(TransformHtml transformer){
        DomSerialisation rules = new DomSerialisation();
        transformer.transform(rules);
        return rules.getContent();
    }

    private final StringBuilder sb = new StringBuilder();

    private static final String INDENTTEXT = "    ";

    @Override
    public Outcome testElementAndModify(Element element, int level){
        write(INDENTTEXT.repeat(level));
        write(element.getTagName());
        
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            write(' ');
            write(attribute.getNodeName());
            write("=\"");
            write(attribute.getNodeValue());
            write("\"");
        }
        write('\n');
        return Outcome.CONTINUE_SWEEP;
    }

    @Override
    public Outcome testTextAndModify(Node textnode, int level) {
        write(INDENTTEXT.repeat(level));
        write("\"");
        write(textnode.getNodeValue()
                .replace("\n", "\\n").replace("\t", "\\t")
                .replace("\r", "\\r").replace("\"", "\\\"")
        );
        write("\"\n");
        return Outcome.CONTINUE_SWEEP;
    }

    public String getContent() {
        return sb.toString();
    }

    private void write(String string) {
        sb.append(string);
    }

    private void write(char c) {
        sb.append(c);
    }
}
