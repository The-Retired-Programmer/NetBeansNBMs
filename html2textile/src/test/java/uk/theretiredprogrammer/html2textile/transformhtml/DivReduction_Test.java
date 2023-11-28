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
import java.io.StringReader;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;


public class DivReduction_Test extends TransformhtmlTest {

    public DivReduction_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        //
        transformer.transform(new DivReduction());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }
    
    private String rules() {
        return  """
                """;
    }

    private String input() {
        return  """
                <div><span style="font-size: 12pt;"><strong>bold text</strong></span></div>
                <div><span style="font-size: 12pt;">Plain text.</span></div>
                <div> </div>
                <div>
                    <ul>
                        <li><span style="font-size: 12pt;">Item1</span></li>
                        <li><span style="font-size: 12pt;">Item2</span></li>
                        <li><span style="font-size: 12pt;">Item3</span></li>
                    </ul>
                </div>
                <div style="margin: 50px 0 0 100px; padding: 0; float: right; width: 20%;">
                    <p style="text-align: center; line-height: 12pt;"><img style="width: 100%; margin: 0; padding: 0;" alt="img" src="somewhere.jpg" /><br />caption</p>
                </div>
                <div>
                    <p>para1</p>
                    <p>para2</p>
                    <p>para3</p>
                </div>
                <div>
                    <p>para1</p>
                    <b>bold text</b>
                    <p>para3</p>
                </div>
                """;
    }


    private String expected() {
        return """
            html
                line number="1"
                p
                    span style="font-size: 12pt;"
                        strong
                            "bold text"
                line number="2"
                p
                    span style="font-size: 12pt;"
                        "Plain text."
                line number="3"
                p
                    " "
                line number="4"
                line number="5"
                ul
                    line number="6"
                    li
                        span style="font-size: 12pt;"
                            "Item1"
                    line number="7"
                    li
                        span style="font-size: 12pt;"
                            "Item2"
                    line number="8"
                    li
                        span style="font-size: 12pt;"
                            "Item3"
                    line number="9"
                line number="10"
                line number="11"
                line number="12"
                p style="margin: 50px 0 0 100px; padding: 0; float: right; width: 20%; text-align: center; line-height: 12pt; "
                    img alt="img" src="somewhere.jpg" style="width: 100%; margin: 0; padding: 0;"
                    br
                    "caption"
                line number="13"
                line number="14"
                line number="15"
                p
                    "para1"
                line number="16"
                p
                    "para2"
                line number="17"
                p
                    "para3"
                line number="18"
                line number="19"
                p
                    line number="20"
                    p
                        "para1"
                    line number="21"
                    b
                        "bold text"
                    line number="22"
                    p
                        "para3"
                    line number="23"
            """;
    }
}
