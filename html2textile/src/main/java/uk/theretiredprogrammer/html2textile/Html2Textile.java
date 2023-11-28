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

import uk.theretiredprogrammer.html2textile.rules.Rules;
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
import uk.theretiredprogrammer.html2textile.transformtext.TransformHtmlText;
import uk.theretiredprogrammer.html2textile.transformhtml.TransformHtml;
import uk.theretiredprogrammer.html2textile.transformtext.TransformTextileText;

public class Html2Textile {

    public static void convert(Reader from, PrintWriter textilewriter, ErrHandler err, File inputfile) throws IOException, ParserConfigurationException, FileNotFoundException, SAXException, TransformerException {
        new Html2Textile().converter(from, textilewriter, err, inputfile);
    }

    private Html2Textile() {
    }

    public void converter(Reader from, PrintWriter textilewriter, ErrHandler err, File inputfile) throws IOException, ParserConfigurationException, FileNotFoundException, SAXException, TransformerException {
        Rules.create(inputfile,err);
        TransformHtmlText texttransformer = Rules.get_HTML_PREPROCESSING();
        texttransformer.setReader(from);
        texttransformer.rootWrap("html");
        StringWriter swriter = new StringWriter();
        try ( Reader wrapped = texttransformer.transform();
                PrintWriter textileout = new PrintWriter(swriter)) {
            TransformHtml htmltransformer = new TransformHtml(wrapped);
            htmltransformer.transform();
            //
            TextileTranslator translator = new TextileTranslator(htmltransformer.getRoot(), textileout, err);
            translator.translate();
        }
        TransformTextileText textiletransformer = Rules.get_TEXTILE_POSTPROCESSING();
        textiletransformer.setInput(swriter);
        textiletransformer.transform();
        textiletransformer.save(textilewriter);
    }
}
