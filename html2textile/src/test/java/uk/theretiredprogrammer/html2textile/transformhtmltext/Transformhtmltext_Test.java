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
package uk.theretiredprogrammer.html2textile.transformhtmltext;

import uk.theretiredprogrammer.html2textile.transformhtml.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.html2textile.rules.Rules;
import uk.theretiredprogrammer.html2textile.transformtext.TransformHtmlText;

public class Transformhtmltext_Test {

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        Rules.create();
        TransformHtml transformer;
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/transformhtml/example_transformhtmltext.html");
        Reader from = new InputStreamReader(is);
        TransformHtmlText texttransformer = Rules.get_HTML_PREPROCESSING();
        texttransformer.setReader(from);
        texttransformer.rootWrap("html");
        try ( Reader transformed = texttransformer.transform()) {
            transformer = new TransformHtml(transformed);
        }
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String expected() {
        return """
               html
                   line number="1"
                   img
                   line number="2"
                   b
                       "text"
                   line number="4"
                   img
               """;
    }
}
