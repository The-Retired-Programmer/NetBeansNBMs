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

import java.io.File;
import uk.theretiredprogrammer.html2textile.textiletranslation.TextileTranslator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.html2textile.tranformshtmltext.TransformHtmlText;
import uk.theretiredprogrammer.html2textile.transformhtml.TransformHtml;
import uk.theretiredprogrammer.html2textile.transformtextiletext.TransformTextileText;

public class Html2Textile {

    // ignore system rules
    public final static int IGNORE_TEXTILE_SYSTEM_RULES = 1;
    public final static int IGNORE_STYLE_SYSTEM_RULES = 2;
    public final static int IGNORE_HTML_SYSTEM_RULES = 4;
    //
    public final static int IGNORE_NO_SYSTEM_RULES = 0;
    public final static int IGNORE_ALL_SYSTEM_RULES = IGNORE_TEXTILE_SYSTEM_RULES | IGNORE_STYLE_SYSTEM_RULES | IGNORE_HTML_SYSTEM_RULES;

    public static void convert(Reader from, PrintWriter textilewriter, ErrHandler err, File inputfile) throws IOException, ParserConfigurationException, FileNotFoundException, SAXException, TransformerException {
        new Html2Textile().converter(from, textilewriter, err, inputfile, IGNORE_NO_SYSTEM_RULES);
    }

    public static void convert(Reader from, PrintWriter textilewriter, ErrHandler err, File inputfile, int ignoresystemrules) throws IOException, ParserConfigurationException, FileNotFoundException, SAXException, TransformerException {
        new Html2Textile().converter(from, textilewriter, err, inputfile, ignoresystemrules);
    }

    private Html2Textile() {
    }

    public void converter(Reader from, PrintWriter textilewriter, ErrHandler err, File inputfile, int ignoresystemrules) throws IOException, ParserConfigurationException, FileNotFoundException, SAXException, TransformerException {
        TransformHtmlText texttransformer = new TransformHtmlText(from, inputfile, (ignoresystemrules & IGNORE_HTML_SYSTEM_RULES) > 0);
        texttransformer.rootWrap("html");
        StringWriter swriter = new StringWriter();
        try ( Reader wrapped = texttransformer.transform();  PrintWriter textileout = new PrintWriter(swriter)) {
            TransformHtml transformer = new TransformHtml(wrapped);
            transformer.transform(inputfile, (ignoresystemrules & IGNORE_STYLE_SYSTEM_RULES) > 0);
            //
            TextileTranslator translator = new TextileTranslator(transformer.getRoot(), textileout, err);
            translator.translate();
        }
        TransformTextileText textiletransformer = new TransformTextileText(swriter, textilewriter, inputfile, (ignoresystemrules & IGNORE_TEXTILE_SYSTEM_RULES) > 0);
        textiletransformer.transform();
        textiletransformer.save();
    }
}
