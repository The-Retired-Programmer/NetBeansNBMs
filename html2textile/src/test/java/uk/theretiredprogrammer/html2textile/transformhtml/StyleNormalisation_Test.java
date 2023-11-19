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
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;


public class StyleNormalisation_Test extends TransformhtmlTest {

    public StyleNormalisation_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation("stylenormalisation");
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
                   line number="1"
                   p style="text-align:center;"
                       span style="font-size:18pt;"
                   line number="2"
                   p
                       span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                   line number="3"
                   p
                       span style="font-size:12pt;font-family:arial,helvetica,sans-serif;"
                   line number="4"
                   div
                       span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                   line number="5"
                   div
                       span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                   line number="6"
                   p
                       span style="color:#000000;"
                           span style="font-size:10pt;"
                               span style="background-color:inherit;font-family:inherit;"
                   line number="7"
                   p
                       span style="font-family:arial,helvetica,sans-serif;"
                   line number="8"
                   p
                       span style="text-decoration:underline;"
                           span style="font-family:arial,helvetica,sans-serif;font-size:14pt;"
                   line number="9"
                   p dir="autox"
                       span style="text-decoration:underline;font-family:arial,helvetica,sans-serif;font-size:12pt;"
                   line number="10"
                   p
                       span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                   line number="11"
                   p
                       span style="text-decoration:underline;font-family:arial,helvetica,sans-serif;font-size:12pt;"
               """;
    }
}
