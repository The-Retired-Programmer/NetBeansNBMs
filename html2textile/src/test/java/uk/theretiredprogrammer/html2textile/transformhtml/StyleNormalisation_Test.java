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
package uk.theretiredprogrammer.html2textile.transformhtml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class StyleNormalisation_Test {

    public StyleNormalisation_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/transformhtml/example_stylenormalisation.html");
        Reader in = new InputStreamReader(is);
        TransformHtml transformer = new TransformHtml(in);
        transformer.transform(new IndentAndReturnsRemoval());
        //
        transformer.transform(new StyleNormalisation());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String expected() {
        return """
               html
                   p style="text-align:center;"
                       span style="font-size:18pt;"
                   p
                       span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                   p
                       span style="font-size:12pt;font-family:arial,helvetica,sans-serif;"
                   div
                       span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                   div
                       span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                   p
                       span style="color:#000000;"
                           span style="font-size:10pt;"
                               span style="background-color:inherit;font-family:inherit;"
                   p
                       span style="font-family:arial,helvetica,sans-serif;"
                   p
                       span style="text-decoration:underline;"
                           span style="font-family:arial,helvetica,sans-serif;font-size:14pt;"
                   p dir="auto"
                       span style="text-decoration:underline;font-family:arial,helvetica,sans-serif;font-size:12pt;"
                   p
                       span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                   p
                       span style="text-decoration:underline;font-family:arial,helvetica,sans-serif;font-size:12pt;"
               """;
    }
}
