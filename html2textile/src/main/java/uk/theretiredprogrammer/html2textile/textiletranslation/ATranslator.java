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

public class ATranslator extends TextileElementTranslator {

    private final static String RELATIVE_PREFIX = "https://exe-sailing-club.org/";

    public ATranslator(PrintWriter out, PrintWriter err) {
        super(out, err);
    }

    public String[] allowedAttributes() {
        return new String[]{"style", "class", "id", "href"};
    }

    public void write(Element element, boolean isParentTerminatorContext, TextileTranslator translator) throws IOException {
        out.write("\"");
        writeClassStyleId(element);
        translator.processChildren(element);
        out.write("\":");
        out.write(getURL(getAttribute(element, "href")));
        if (!isParentTerminatorContext) {
            out.write(" ");
        }
    }

    private String getURL(String hrefvalue) {
        return hrefvalue.startsWith("https://") || hrefvalue.startsWith("mailto:") || hrefvalue.startsWith("tel:")
                ? hrefvalue : RELATIVE_PREFIX + hrefvalue;
    }
}
