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

import java.io.PrintWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.theretiredprogrammer.util.UserReporting;

public abstract class TextileElementTranslator {

    final PrintWriter out;
    
    TextileElementTranslator(PrintWriter out) {
        this.out = out;
    }

    public static TextileElementTranslator factory(String name, PrintWriter out) {
        return switch (name.toLowerCase()) {
            case "html" ->
                new IgnoredTranslator(out);
            case "div" ->
                new DivTranslator(out);
            case "p" ->
                new PTranslator(out);
            case "span" ->
                new SpanTranslator(out);
            case "strong" ->
                new StrongTranslator(out);
            case "b" ->
                new StrongTranslator(out);
            case "sup" ->
                new SupTranslator(out);
            case "sub" ->
                new SubTranslator(out);
            case "br" ->
                new BrTranslator(out);
            case "img" ->
                new ImgTranslator(out);
            case "a" ->
                new ATranslator(out);
            case "h1" ->
                new HxTranslator(out);
            case "h2" ->
                new HxTranslator(out);
            case "h3" ->
                new HxTranslator(out);
            case "h4" ->
                new HxTranslator(out);
            case "h5" ->
                new HxTranslator(out);
            case "h6" ->
                new HxTranslator(out);
            case "ul" ->
                new UlTranslator(out);
            case "ol" ->
                new OlTranslator(out);
            case "li" ->
                new LiTranslator(out);
            case "table" ->
                new TableTranslator(out);
            case "tbody" ->
                new IgnoredTranslator(out);
            case "tr" ->
                new TrTranslator(out);
            case "td" ->
                new TdTranslator(out);
            case "u" ->
                new UTranslator(out);
            default ->
                new UnknownTranslator(out);
        };
    }

    public abstract String[] allowedAttributes();

    public abstract void write(Element element, String name, NamedNodeMap attributes, NodeList children, TextileTranslator translator) throws IOException;

    void checkNoAttributes(NamedNodeMap attributes) throws IOException {
        for (int i = 0; i < attributes.getLength(); i++) {
            UserReporting.warning("Html to Textile conversion", "Unexpected attribute observed - will be ignored (" + attributes.item(i).getNodeName() + ")");
        }
    }

    String isAttribute(String elementName, String attributeName, NamedNodeMap attributes) {
        Node attribute = attributes.getNamedItem(attributeName);
        if (attribute == null) {
            UserReporting.error("Html to Textile conversion", "Error: Expected attribute not present (" + attributeName + " in " + elementName + ")");
            return "**MISSING " + attributeName + " ATTRIBUTE**";
        }
        return attribute.getNodeValue();
    }

    void writeClassStyleId(NamedNodeMap attributes) throws IOException {
        checkAttributes(attributes, allowedAttributes());
        Node classAttribute = attributes.getNamedItem("class");
        Node idAttribute = attributes.getNamedItem("id");
        if (classAttribute != null || idAttribute != null) {
            out.write("(");
            if (classAttribute != null) {
                out.write(classAttribute.getNodeValue());
            }
            if (idAttribute != null) {
                out.write("#" + idAttribute.getNodeValue());
            }
            out.write(")");
        }
        Node styleAttribute = attributes.getNamedItem("style");
        if (styleAttribute != null) {
            out.write("{" + styleAttribute.getNodeValue() + "}");
        }
    }

    private void checkAttributes(NamedNodeMap attributes, String[] allowedAttributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            boolean match = false;
            String attributeName = attributes.item(i).getNodeName();
            for (String allowedAttribute : allowedAttributes) {
                if (attributeName.equals(allowedAttribute)) {
                    match = true;
                }
            }
            if (!match) {
                UserReporting.error("Html to Textile conversion", "Unexpected attribute observed - will be ignored (" + attributeName + ")");
            }
        }
    }
}
