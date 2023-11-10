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

public class ReplaceWithHeadings_Test extends TransformhtmlTest {

    public ReplaceWithHeadings_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation("replacewithheadings");
        transformer.transform(new StyleNormalisation());
        //
        transformer.transform(new ReplaceWithHeadings());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String expected() {
        return  """
                html
                    line number="1"
                    p
                        strong
                            "just bold"
                    line number="2"
                    p
                        u
                            strong
                                "just strong,u"
                    line number="3"
                    h4
                        "This is H4"
                    line number="4"
                    h3
                        "This is H3"
                    line number="5"
                    h4
                        "This is H4"
                    line number="6"
                    h3
                        "This is H3"
                    line number="7"
                    h4
                        "This is H4"
                    line number="8"
                    h3
                        "This is H3"
                    line number="9"
                    p style="font-size:14pt;"
                        strong
                            span style="font-size:18pt;"
                                u
                                    span style="font-size:12pt;"
                                        strong
                                            "just strong,u"
                    line number="10"
                    h4 style="text-align:center;"
                        "This is H4"
                """;
    }
}
