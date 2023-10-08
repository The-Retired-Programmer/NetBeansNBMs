/*
 * Copyright 2022-2023 Richard Linsdale.
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
package uk.theretiredprogrammer.html2textile;

import uk.theretiredprogrammer.html2textile.textiletranslation.TextileTranslator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.html2textile.tranformtext.TransformText;
import uk.theretiredprogrammer.html2textile.transformhtml.TransformHtml;

public class Html2Textile {

    public static void convert(Reader from, PrintWriter textilewriter) throws IOException, ParserConfigurationException, FileNotFoundException, SAXException, TransformerException {
        TransformText texttransformer = new TransformText(from);
        texttransformer.rootWrap("html");
        texttransformer.replace("&nbsp;", " ");
        try (Reader wrapped = texttransformer.transform()) {
            TransformHtml transformer = new TransformHtml(wrapped);
            transformer.transform();
            TextileTranslator translator = new TextileTranslator(transformer.getRoot(), textilewriter);
            translator.translate();
        }
    }
}
 