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


public class ListConcatonation_Test extends TransformhtmlTest {

    public ListConcatonation_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        //
        transformer.transform(new ListConcatonation());
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
                <ul>
                    <li>one-one</li>
                </ul>
                <br/>
                <ul>
                    <li>one-one</li>
                </ul>
                <ul>
                    <li>two-one</li>
                </ul>
                <br/>
                <ol>
                    <li>one-one</li>
                </ol>
                <ol>
                    <li>two-one</li>
                </ol>
                <br/>
                <ol>
                    <li>one-one</li>
                </ol>
                <ul>
                    <li>two-one</li>
                </ul>
                <br/>
                <ul style="font-size: 12pt; ">
                    <li>one-one</li>
                </ul>
                <ul style="font-size: 14pt; ">
                    <li>two-one</li>
                </ul>
                <br/>
                <ul>
                    <li>one-one</li>
                </ul>
                <ul style="font-size: 14pt; ">
                    <li>two-one</li>
                </ul>
                <br/>
                <ul>
                    <li>one-one</li>
                </ul>
                <ul>
                    <li>two-one</li>
                </ul>
                <ul>
                    <li>three-one</li>
                </ul>
                <ul>
                    <li>four-one</li>
                    <li>four-two</li>
                </ul>
                """;
    }
    
    private String expected() {
        return """
            html
                line number="1"
                ul
                    line number="2"
                    li
                        "one-one"
                    line number="3"
                line number="4"
                br
                line number="5"
                ul
                    line number="6"
                    li
                        "one-one"
                    line number="7"
                    line number="9"
                    li
                        "two-one"
                    line number="10"
                line number="8"
                line number="11"
                br
                line number="12"
                ol
                    line number="13"
                    li
                        "one-one"
                    line number="14"
                    line number="16"
                    li
                        "two-one"
                    line number="17"
                line number="15"
                line number="18"
                br
                line number="19"
                ol
                    line number="20"
                    li
                        "one-one"
                    line number="21"
                line number="22"
                ul
                    line number="23"
                    li
                        "two-one"
                    line number="24"
                line number="25"
                br
                line number="26"
                ul style="font-size: 12pt; " 
                    line number="27"
                    li
                        "one-one"
                    line number="28"
                line number="29"
                ul style="font-size: 14pt; "
                    line number="30"
                    li
                        "two-one"
                    line number="31"
                line number="32"
                br
                line number="33"
                ul
                    line number="34"
                    li
                        "one-one"
                    line number="35"
                line number="36"
                ul style="font-size: 14pt; "
                    line number="37"
                    li
                        "two-one"
                    line number="38"
                line number="39"
                br
                line number="40"
                ul
                    line number="41"
                    li
                        "one-one"
                    line number="42"
                    line number="44"
                    li
                        "two-one"
                    line number="45"
                    line number="47"
                    li
                        "three-one"
                    line number="48"
                    line number="50"
                    li
                        "four-one"
                    line number="51"
                    li
                        "four-two"
                    line number="52"
                line number="43"
                line number="46"
                line number="49"
            """;
    }
}
