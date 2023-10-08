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
import org.w3c.dom.NodeList;
import uk.theretiredprogrammer.util.UserReporting;

public class ImgTranslator extends TextileElementTranslator {

    public ImgTranslator(PrintWriter out) {
        super(out);
    }

    private final static String ASSET_PREFIX = "https://exe-sailing-club.org/";

    public String[] allowedAttributes() {
        return new String[]{"style", "class", "id", "src", "alt"};
    }

    public void write(Element element, String name, NamedNodeMap attributes, NodeList children, TextileTranslator translator) throws IOException {
        out.write("!");
        writeClassStyleId(attributes);
        out.write(getURL(isAttribute(name, "src", attributes)));
        out.write("(" + isAttribute(name, "alt", attributes) + ")");
        out.write("!");
    }

    private String getURL(String srcvalue) {
        if (srcvalue.startsWith("assets")) {
            return ASSET_PREFIX + srcvalue;
        }
        if (srcvalue.startsWith("https://")) {
            return srcvalue;
        }
        UserReporting.error("Html to Textile conversion", "Unexpected src URL - will be used but needs to be checked (" + srcvalue + ")");
        return srcvalue;
    }

}
