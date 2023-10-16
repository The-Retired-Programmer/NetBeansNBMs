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
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.html2textile.tranformhtmltext.TransformHtmlText;
import uk.theretiredprogrammer.html2textile.transformhtml.TransformHtml;
import uk.theretiredprogrammer.html2textile.transformtextiletext.TransformTextileText;

public class Html2Textile {
    
    public static void convert(Reader from, PrintWriter textilewriter, PrintWriter err, List<InputStream> rules) throws IOException, ParserConfigurationException, FileNotFoundException, SAXException, TransformerException {
        new Html2Textile().converter(from,textilewriter,err, rules);
    }
    
    
    private Html2Textile() {
    }

    public void converter(Reader from, PrintWriter textilewriter, PrintWriter err, List<InputStream> rules) throws IOException, ParserConfigurationException, FileNotFoundException, SAXException, TransformerException {
        TransformHtmlText texttransformer = new TransformHtmlText(from);
        texttransformer.rootWrap("html");
        texttransformer.replace("&nbsp;", " ");
        texttransformer.replace("&lsquo;", "'");
        texttransformer.replace("&rsquo;", "'");
        StringWriter swriter = new StringWriter();
        try ( Reader wrapped = texttransformer.transform(); PrintWriter textileout = new PrintWriter(swriter)) {
            TransformHtml transformer = new TransformHtml(wrapped);
            transformer.transform();
            TextileTranslator translator = new TextileTranslator(transformer.getRoot(), textileout, err);
            translator.translate();
        }
        rules.add(this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/transformtextiletext/rules"));
        TransformTextileText textiletransformer = new TransformTextileText(swriter, textilewriter, rules);
        textiletransformer.transform();
        textiletransformer.save();
    }
}
