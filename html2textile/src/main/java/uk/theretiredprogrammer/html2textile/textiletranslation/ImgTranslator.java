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

    public String[] allowedAttributes() {
        return new String[]{"style", "class", "id", "src", "alt", "height"};
    }

    public void write(Element element, boolean isParentTerminatorContext, TextileTranslator translator) throws IOException {
        out.write("!");
        writeClassStyleId(element);
        String url = getURL(element);
        out.write(url);
        out.write("(" + getAlt(element, url) + ")");
        out.write("!");
    }

    private String getURL(Element element) throws IOException {
        String srcvalue = element.getAttribute("src");
        if (srcvalue.isEmpty()) {
            throw new IOException("Missing src attribute in img element");
        }
        return srcvalue;
    }

    private String getAlt(Element element, String url) {
        String altvalue = element.getAttribute("alt");
        if (altvalue.isEmpty()) {
            altvalue = geturlname(url);
            err.warning("alt attribute missing - has been set to \"" + altvalue + "\"", element);
        }
        return altvalue;
    }

    private String geturlname(String url) {
        int dotpos = url.lastIndexOf('.');
        if (dotpos == -1) {
            dotpos = url.length();
        }
        int slashpos = url.lastIndexOf('/', dotpos);
        return url.substring(++slashpos, dotpos);
    }
}
