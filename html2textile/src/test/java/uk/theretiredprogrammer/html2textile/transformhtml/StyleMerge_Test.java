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

public class StyleMerge_Test extends TransformhtmlTest {

    public StyleMerge_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation("stylemerge");
        transformer.transform(new StyleNormalisation());
        //
        transformer.transform(new StyleMerge());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String expected() {
        return """
               html
                   line number="1"
                   p dir="auto"
                   line number="2"
                   p dir="auto"
                       "abc"
                   line number="3"
                   p
                       span dir="auto"
                           "abc"
                       "def"
                   line number="4"
                   p dir="auto"
                       "abc"
                       strong
                           "def"
                       "ghi"
                   line number="6"
                   p dir="auto"
                       "abc"
                   line number="7"
                   p dir="auto"
                       "abc"
                   line number="8"
                   p dir="auto"
                       "abc"
                   line number="9"
                   p dir="auto"
                       "abc"
                   line number="10"
                   p dir="auto" dir2="manual"
                       "abc"
                   line number="12"
                   p style="font-size:14pt;"
                       "abc"
                   line number="13"
                   p style="font-size:14pt;"
                       "abc"
                   line number="14"
                   p style="font-size:14pt;"
                       "abc"
                   line number="15"
                   p style="font-size:18pt;"
                       "abc"
                   line number="16"
                   p style="font-size:14pt;text-align:center;"
                       "abc"
                   line number="18"
                   p style="font-size:18pt;text-align:center;"
                       "abc"
               """;
    }
}
