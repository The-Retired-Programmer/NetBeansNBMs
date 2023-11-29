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


public class CompositeHtmlOptimisations_Test extends TransformhtmlTest {

    public CompositeHtmlOptimisations_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        //
        transformer.transform(new CompositeHtmlOptimisations());
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
                <p style="font-size:10pt">
                <span style="text-align:right">
                <span color=" ">
                <ol><li>
                <ul><li>
                <ol><li><strong>   </strong>
                <ul><li>
                </li></ul>
                </li></ol>
                </li></ul>
                </li></ol>
                </span>
                </span>
                </p>
                <ul>
                    <li style="font-size: 12pt; ">
                        <p style="font-size: 18pt; ">xyz</p>
                    </li>
                </ul>
                <p>start <strong> abcxyz </strong> end</p>
                <ul>
                    <li><strong>abc</strong>def<br /> </li>
                </ul>
                <ul>
                    <li>four-one</li>
                    <li>four-two</li>
                </ul>
                <p>text</p>
                <p><img/></p>
                <p></p>
                <p style="font-size: 12pt; "></p>
                <p>abc<b> word </b>xyz</p>
                <p>abc<span> word </span>xyz</p>
                """;
    }

    private String expected() {
        return """
                html
                    line number="1"
                    p style="font-size: 10pt; text-align: right; "
                        line number="2"
                        line number="3"
                        line number="4"
                        line number="5"
                        line number="6"
                        line number="7"
                        line number="8"
                        line number="9"
                        line number="10"
                        line number="11"
                        line number="12"
                        line number="13"
                        line number="14"
                    line number="15"
                    ul
                        line number="16"
                        li style="font-size: 12pt; font-size: 18pt; "
                            line number="17"
                            "xyz"
                            line number="18"
                        line number="19"
                    line number="20"
                    p
                        "start  "
                        strong
                            "abcxyz"
                        "  end"
                    line number="21"
                    ul
                        line number="22"
                        li
                            strong
                                "abc"
                            "def"
                        line number="23"
                        line number="25"
                        li
                            "four-one"
                        line number="26"
                        li
                            "four-two"
                        line number="27"
                    line number="24"
                    line number="28"
                    p
                        "text"
                    line number="29"
                    p
                        img
                    line number="30"
                    line number="31"
                    line number="32"
                    p
                        "abc "
                        b
                            "word"
                        " xyz"
                    line number="33"
                    p
                        "abc word xyz"
                """;
    }
}
