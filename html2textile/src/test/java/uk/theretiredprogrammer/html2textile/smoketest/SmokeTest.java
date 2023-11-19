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
import uk.theretiredprogrammer.html2textile.rules.Rules;
import uk.theretiredprogrammer.html2textile.transformtext.TransformHtmlText;
import uk.theretiredprogrammer.html2textile.transformhtml.TransformHtml;
import uk.theretiredprogrammer.html2textile.transformtext.TransformTextileText;
import uk.theretiredprogrammer.html2textile.textiletranslation.TextileTranslator;

public class SmokeTest {

    public SmokeTest() {
    }

    public void transformation(
            String inputresourcefilename,
            String outputhtmlfilename,
            String outputtextilefilename,
            String expected
    ) throws IOException, ParserConfigurationException, SAXException, URISyntaxException, TransformerException {
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            String s2;
            Rules.parse();
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/transformhtml/" + inputresourcefilename);
            TransformHtmlText texttransformer = Rules.get_HTML_PREPROCESSING();
            texttransformer.setReader(new InputStreamReader(is));
            texttransformer.rootWrap("html");
            try ( Reader wrapped = texttransformer.transform()) {
                TransformHtml transformer = new TransformHtml(wrapped);
                transformer.transform();
                transformer.writeHtml(new FileWriter("/home/richard/" + outputhtmlfilename));
                //
                StringWriter swriter = new StringWriter();
                try ( PrintWriter textileout = new PrintWriter(swriter)) {
                    TextileTranslator translator = new TextileTranslator(transformer.getRoot(), textileout, err);
                    translator.translate();
                }
                //
                StringWriter s2writer = new StringWriter();
                PrintWriter out = new PrintWriter(s2writer);
                TransformTextileText textiletransformer = Rules.get_TEXTILE_POSTPROCESSING();
                textiletransformer.setInput(swriter);
                textiletransformer.transform();
                textiletransformer.save(out);

                PrintWriter out2 = new PrintWriter(new FileWriter("/home/richard/" + outputtextilefilename));
                s2 = s2writer.toString();
                try (out2) {
                    out2.print(s2);
                }
            }
            if (expected == null) {
                System.out.println(s2);
            } else {
                String[] expectedlines = expected.split("\n");
                String[] actuallines = s2.split("\n");
                assertEquals(expectedlines.length, actuallines.length);
                for (int i = 0; i < expectedlines.length; i++) {
                    assertEquals(expectedlines[i].stripTrailing(), actuallines[i].stripTrailing(), "line equality failed on line " + (i + 1));
                }
            }
        }
    }
}
