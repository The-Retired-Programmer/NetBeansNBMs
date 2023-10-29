/*
 * Copyright 2023 richard linsdale.
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
package uk.theretiredprogrammer.html2textile.smoketest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.html2textile.ErrHandler;
import uk.theretiredprogrammer.html2textile.textiletranslation.TextileTranslator;
import uk.theretiredprogrammer.html2textile.tranformshtmltext.TransformHtmlText;
import uk.theretiredprogrammer.html2textile.transformhtml.SerialiseDom;
import uk.theretiredprogrammer.html2textile.transformhtml.TransformHtml;
import uk.theretiredprogrammer.html2textile.transformtextiletext.TransformTextileText;

public class SmokeTest {

    public SmokeTest() {
    }

    public void transformation(
            String inputresourcefilename,
            String outputhtmlfilename,
            String outputtextilefilename,
            String expected,
            boolean noTextileRules
    ) throws IOException, ParserConfigurationException, SAXException, URISyntaxException, TransformerException {
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            String serialised;
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/transformhtml/" + inputresourcefilename);
            TransformHtmlText texttransformer = new TransformHtmlText(new InputStreamReader(is));
            texttransformer.rootWrap("html");
            try ( Reader wrapped = texttransformer.transform()) {
                TransformHtml transformer = new TransformHtml(wrapped);
                transformer.transform();
                transformer.writeHtml(new FileWriter("/home/richard/" + outputhtmlfilename));
                serialised = SerialiseDom.serialise(transformer.getRoot());
                //
                StringWriter swriter = new StringWriter();
                try ( PrintWriter textileout = new PrintWriter(swriter)) {
                    TextileTranslator translator = new TextileTranslator(transformer.getRoot(), textileout, err);
                    translator.translate();
                }
                PrintWriter out = new PrintWriter(new FileWriter("/home/richard/" + outputtextilefilename));
                TransformTextileText textiletransformer = new TransformTextileText(swriter, out);
                if (!noTextileRules) {
                    textiletransformer.transform();
                }
                textiletransformer.save();
            }
            if (expected == null) {
                System.out.println(serialised);
            } else {
                assertEquals(expected, serialised);
            }
        }
    }
}
