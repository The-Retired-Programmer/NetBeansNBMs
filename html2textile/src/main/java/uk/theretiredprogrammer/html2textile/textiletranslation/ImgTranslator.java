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
import uk.theretiredprogrammer.html2textile.ErrHandler;

public class ImgTranslator extends TextileElementTranslator {

    public ImgTranslator(PrintWriter out, ErrHandler err) {
        super(out, err);
    }

    private final static String ASSET_PREFIX = "https://files.exe-sailing-club.org/";

    public String[] allowedAttributes() {
        return new String[]{"style", "class", "id", "src", "alt", "height"};
    }

    public void write(Element element, boolean isParentTerminatorContext, TextileTranslator translator) throws IOException {
        out.write("!");
        writeClassStyleId(element);
        out.write(getURL(element));
        out.write("(" + getAlt(element) + ")");
        out.write("!");
    }

    private String getURL(Element element) {
        String srcvalue = getAttribute(element, "src");
        if (srcvalue.startsWith("assets")) {
            return ASSET_PREFIX + srcvalue.substring(7);
        }
        if (srcvalue.startsWith("https://") || srcvalue.startsWith("http://")) {
            return srcvalue;
        }
        err.warning("Warning: unexpected src URL - will be used but needs to be checked", element);
        return srcvalue;
    }

    private String getAlt(Element element) {
        String altvalue = getAttribute(element, "alt");
        if (altvalue.isEmpty()) {
            err.error("alt attribute missing", element);
        }
        return altvalue;
    }
}
