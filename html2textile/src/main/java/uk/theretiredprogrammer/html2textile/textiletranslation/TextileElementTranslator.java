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
import uk.theretiredprogrammer.html2textile.ErrHandler;
import uk.theretiredprogrammer.html2textile.rules.Style;

public abstract class TextileElementTranslator {

    final PrintWriter out;
    final ErrHandler err;

    TextileElementTranslator(PrintWriter out, ErrHandler err) {
        this.out = out;
        this.err = err;
    }

    public static TextileElementTranslator factory(Element element, PrintWriter out, ErrHandler err) {
        return switch (element.getTagName().toLowerCase()) {
            case "line" ->
                new LineHandler(out, err);
            case "html" ->
                new IgnoredTranslator(out, err);
            case "div" ->
                new DivTranslator(out, err);
            case "p" ->
                new PTranslator(out, err);
            case "span" ->
                new SpanTranslator(out, err);
            case "strong" ->
                new StrongTranslator(out, err);
            case "b" ->
                new StrongTranslator(out, err);
            case "em" ->
                new EmTranslator(out, err);
            case "i" ->
                new EmTranslator(out, err);
            case "sup" ->
                new SupTranslator(out, err);
            case "sub" ->
                new SubTranslator(out, err);
            case "br" ->
                new BrTranslator(out, err);
            case "hr" ->
                new HrTranslator(out, err);
            case "img" ->
                new ImgTranslator(out, err);
            case "a" ->
                new ATranslator(out, err);
            case "h1" ->
                new HxTranslator(out, err);
            case "h2" ->
                new HxTranslator(out, err);
            case "h3" ->
                new HxTranslator(out, err);
            case "h4" ->
                new HxTranslator(out, err);
            case "h5" ->
                new HxTranslator(out, err);
            case "h6" ->
                new HxTranslator(out, err);
            case "ul" ->
                new UlTranslator(out, err);
            case "ol" ->
                new OlTranslator(out, err);
            case "li" ->
                new LiTranslator(out, err);
            case "table" ->
                new TableTranslator(out, err);
            case "colgroup" ->
                new ColgroupTranslator(out, err);
            case "col" ->
                new ColTranslator(out, err);
            case "thead" ->
                new TheadTranslator(out, err);
            case "th" ->
                new ThTranslator(out, err);
            case "tfoot" ->
                new TfootTranslator(out, err);
            case "tbody" ->
                new TbodyTranslator(out, err);
            case "tr" ->
                new TrTranslator(out, err);
            case "td" ->
                new TdTranslator(out, err);
            case "u" ->
                new UTranslator(out, err);
            case "button" ->
                new IgnoredTranslator(out, err);
            case "abbr" ->
                new AbbrTranslator(out, err);
            default ->
                new UnknownTranslator(out, err);
        };
    }

    public abstract String[] allowedAttributes();

    public abstract void write(Element element, boolean isParentTerminatorContext, TextileTranslator translator) throws IOException;

    void bracket(String bracket, Element element, boolean isParentTerminatorContext, TextileTranslator translator) throws IOException {
        out.write(bracket);
        checkNoAttributes(element);
        translator.processChildrenInTerminatorContext(element);
        out.write(bracket);
    }
    
    void bracketplus(String bracket, Element element, boolean isParentTerminatorContext, TextileTranslator translator) throws IOException {
        out.write('[');
        bracket(bracket, element, isParentTerminatorContext, translator);
        out.write(']');
    }

    void checkNoAttributes(Element element) throws IOException {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            err.warning("unexpected attribute observed - will be ignored (" + attributes.item(i).getNodeName() + ")", element);
        }
    }

    void writeClassStyleId(Element element) throws IOException {
        checkAttributes(element, allowedAttributes());
        String classAttribute = element.getAttribute("class");
        String idAttribute = element.getAttribute("id");
        if (!classAttribute.isEmpty() || !idAttribute.isEmpty()) {
            out.write("(");
            if (!classAttribute.isEmpty()) {
                out.write(classAttribute);
            }
            if (!idAttribute.isEmpty()) {
                out.write("#" + idAttribute);
            }
            out.write(")");
        }
        Style style = new Style();
        if (style.extract(element)) {
            out.write("{" + style.toString() + "}");
        }
    }
    
    void writeTextAlignment(Element element) {
        out.write(getTextAlignmentSymbol(element));
    }
    
    private String getTextAlignmentSymbol(Element element) {
        return switch (element.getAttribute("text-align")) {
                    case "left" -> "<";
                    case "center" -> "=";
                    case "right" -> ">";
                    case "justify" -> "<>";
                    default -> "";
                };
    }

    void checkAttributes(Element element, String[] allowedAttributes) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            boolean match = false;
            String attributeName = attributes.item(i).getNodeName();
            for (String allowedAttribute : allowedAttributes) {
                if (attributeName.equals(allowedAttribute)) {
                    match = true;
                }
            }
            if (!match) {
                err.warning("Unexpected attribute observed - will be ignored (" + attributeName + ")", element);
            }
        }
    }
}
