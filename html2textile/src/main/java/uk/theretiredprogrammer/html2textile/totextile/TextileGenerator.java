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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.theretiredprogrammer.util.UserReporting;

public abstract class TextileGenerator {

    public static TextileGenerator factory(String name) {
        switch (name.toLowerCase()) {
            case "html":
                return new IgnoredGenerator();
            case "div":
                return new DivGenerator();
            case "p":
                return new PGenerator();
            case "span":
                return new SpanGenerator();
            case "strong":
                return new StrongGenerator();
            case "b":
                return new StrongGenerator();
            case "sup":
                return new SupGenerator();
            case "sub":
                return new SubGenerator();
            case "br":
                return new BrGenerator();
            case "img":
                return new ImgGenerator();
            case "a":
                return new AGenerator();
            case "h1":
                return new HxGenerator();
            case "h2":
                return new HxGenerator();
            case "h3":
                return new HxGenerator();
            case "h4":
                return new HxGenerator();
            case "h5":
                return new HxGenerator();
            case "h6":
                return new HxGenerator();
            case "ul":
                return new UlGenerator();
            case "ol":
                return new OlGenerator();
            case "li":
                return new LiGenerator();
            case "table":
                return new TableGenerator();
            case "tbody":
                return new IgnoredGenerator();
            case "tr":
                return new TrGenerator();
            case "td":
                return new TdGenerator();
        }
        return new UnknownGenerator();
    }

    public abstract String[] allowedAttributes();

    public abstract void write(Element element, String name, NamedNodeMap attributes, NodeList children, BufferedWriter out) throws IOException;

    void checkNoAttributes(NamedNodeMap attributes, BufferedWriter out) throws IOException {
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

    void writeClassStyleId(NamedNodeMap attributes, BufferedWriter out) throws IOException {
        checkAttributes(attributes, allowedAttributes(), out);
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

    private void checkAttributes(NamedNodeMap attributes, String[] allowedAttributes, BufferedWriter out) {
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
