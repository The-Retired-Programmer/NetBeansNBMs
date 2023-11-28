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

public class RestructureTable_Test extends TransformhtmlTest {

    public RestructureTable_Test() {
    }

    @Test
    public void table_test1() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        testtransformation(
                "",
                """
                <table>
                    <thead>
                        <tr><th>h1</th><th>h2</th><th>h3</th></tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <tr><td>d1</td></tr>
                    </tbody>
                </table>
                """,
                """
                html
                    line number="1"
                    table
                        colgroup style="width: 33%; "
                            col
                            col
                            col
                        line number="2"
                        thead
                            line number="3"
                            tr
                                th
                                    "h1"
                                th
                                    "h2"
                                th
                                    "h3"
                            line number="4"
                        line number="5"
                        tfoot
                        line number="6"
                        tbody
                            line number="7"
                            tr
                                td
                                    "d1"
                            line number="8"
                        line number="9"
                """
        );
    }
    
    @Test
    public void table_test2() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        testtransformation(
                "",
                """
                <table>
                    <tbody>
                        <tr><td>d1</td></tr>
                        <tr><td>d2</td></tr>
                    </tbody>
                </table>

                <table>
                    <tfoot></tfoot>
                    <thead><tr><th>h1</th></tr></thead>
                    <tbody>
                        <tr><td>d1</td><td>d2</td><td>d3</td></tr>
                    </tbody>
                </table>
                                
                """,
                """
                html
                    line number="1"
                    table
                        colgroup style="width: 100%; "
                            col
                        thead
                            tr
                                th
                                    "d1"
                        line number="2"
                        tbody
                            line number="3"
                            line number="4"
                            tr
                                td
                                    "d2"
                            line number="5"
                        line number="6"
                    line number="8"
                    table
                        colgroup style="width: 33%; "
                            col
                            col
                            col
                        thead
                            tr
                                th
                                    "h1"
                        line number="9"
                        tfoot
                        line number="10"
                        line number="11"
                        tbody
                            line number="12"
                            tr
                                td
                                    "d1"
                                td
                                    "d2"
                                td
                                    "d3"
                            line number="13"
                        line number="14"
                """
        );
    }
    
    
    @Test
    public void table_test3() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        testtransformation(
                "",
                """
                <table>
                    <tbody>
                        <tr><td style="font-size:10px;font-weight:bold;font-style:italic;">d1</td></tr>
                        <tr><td style="font-weight:bold;font-size:10px;font-style:italic;">d1a</td></tr>
                        <tr><td style="font-style:italic;font-weight:bold;font-size:10px;">d1b</td></tr>
                        <tr><td style="font-size:10px;font-style:italic;">d1</td><td>d2</td><td>d3</td></tr>
                        <tr><td style="font-weight:bold;font-size:10px;">d1</td><td>d2</td></tr>
                    </tbody>
                    <tfoot></tfoot>
                    <thead><tr><th>h1</th></tr></thead>
                </table>
                """,
                """
                html
                    line number="1"
                    table
                        colgroup style="width: 33%; "
                            col style="font-size: 10px; "
                            col
                            col
                        thead
                            tr
                                th
                                    "h1"
                        tfoot
                        line number="2"
                        tbody
                            line number="3"
                            tr
                                td style="font-weight: bold; font-style: italic; "
                                    "d1"
                            line number="4"
                            tr
                                td style="font-weight: bold; font-style: italic; "
                                    "d1a"
                            line number="5"
                            tr
                                td style="font-style: italic; font-weight: bold; "
                                    "d1b"
                            line number="6"
                            tr
                                td style="font-style: italic; "
                                    "d1"
                                td
                                    "d2"
                                td
                                    "d3"
                            line number="7"
                            tr
                                td style="font-weight: bold; "
                                    "d1"
                                td
                                    "d2"
                            line number="8"
                        line number="9"
                        line number="10"
                        line number="11"
                """
        );
    }
    
    @Test
    public void table_test4() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        testtransformation("",input4(), expected4());
    }
    
    @Test
    public void table_test5() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        testtransformation(rules(),input5(), expected5());
    }
    
    public void testtransformation(String rules, String input, String expectedresult) throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules), new StringReader(input));
        //
        transformer.transform(new RestructureTable());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expectedresult, result);
    }

    private String rules() {
        return  """
                [HTML_PREPROCESSING]
                    REPLACE &nbsp; WITH " " 
                """;
    }

    private String input4() {
        return """
                <table>
                    <tbody>
                        <tr><td colspan="3" style="font-size:10px;font-weight:bold;font-style:italic;">d1</td></tr>
                        <tr><td colspan="2" style="font-weight:bold;font-size:10px;font-style:italic;">d2</td></tr>
                        <tr><td style="font-style:italic;font-weight:bold;font-size:10px;">d3</td><td style="font-weight:bold;">x</td></tr>
                        <tr><td style="font-size:10px;font-style:italic;">d4</td><td style="font-weight:bold;">x</td><td>x</td></tr>
                        <tr><td colspan="2" style="font-weight:bold;font-size:10px;">d5</td><td style="color:red;">x</td></tr>
                    </tbody>
                    <tfoot></tfoot>
                    <thead><tr><th>h1</th></tr></thead>
                </table>
                
                <table>
                    <tbody>
                        <tr><td>d1</td></tr>
                        <tr><td>d1</td><td>d2</td><td>d3</td></tr>
                        <tr><td>d1</td><td colspan="5">d2</td></tr>
                    </tbody>
                    <tfoot></tfoot>
                    <thead><tr><th>h1</th></tr></thead>
                </table>
                
                <table>
                    <tbody>
                        <tr><td>d1</td><td style="font-size:10pt">d2</td><td>d3</td></tr>
                        <tr><td rowspan="2">d1</td><td  style="font-size:10pt">d2</td><td>d3</td></tr>
                        <tr><td style="font-size:10pt">d2</td><td>d3</td></tr>
                        <tr><td>d1</td><td style="font-size:10pt">d2</td></tr>
                        <tr><td>d1</td><td style="font-size:10pt" colspan="2">d2</td></tr>
                        <tr><td style="font-size:10pt" rowspan="3" colspan="2">big</td><td>d3</td></tr>
                        <tr><td>d3</td></tr>
                        <tr><td>d3</td></tr>
                        <tr><td>d1</td><td style="font-size:10pt">d2</td><td>d3</td></tr>
                    </tbody>
                    <tfoot></tfoot>
                    <thead><tr><th>h1</th></tr></thead>
                </table>
                """;
    }
    
    private String input5() {
        return """                
                <table style="border: 0; margin-left: auto; margin-right: auto;">
                <tbody>
                <tr style="background-color: #adadad;">
                <td style="border: medium solid #000000;"><strong>Wind Direction</strong></td>
                <td style="text-align: center;"><strong>Tide Direction</strong></td>
                <td style="text-align: center;"><strong>Preferred Course(s)</strong></td>
                <td style="text-align: center;"><strong>Preferred Start Line</strong></td>
                </tr>
                <tr>
                <td style="border: medium solid #000000;" rowspan="2">NNW &lt; <strong>N</strong> &gt; NNE&nbsp;</td>
                <td style="background-color: #dea55e; text-align: center;">Flood</td>
                <td style="text-align: center;">4</td>
                <td style="background-color: #e9ae6d; text-align: center;">W</td>
                </tr>
                <tr>
                <td style="background-color: #dea55e; text-align: center;">Ebb</td>
                <td style="text-align: center;">3</td>
                <td style="background-color: #e9ae6d; text-align: center;">W</td>
                </tr>
                <tr>
                <td style="border: medium solid #000000;" rowspan="2">NNE &lt; <strong>NE</strong> &gt; ENE&nbsp;&nbsp;</td>
                <td style="background-color: #dea55e; text-align: center;">&nbsp;Flood</td>
                <td style="text-align: center;">1 or 4&nbsp;</td>
                <td style="background-color: #e9ae6d; text-align: center;">&nbsp;W</td>
                </tr>
                <tr>
                <td style="background-color: #dea55e; text-align: center;">Ebb&nbsp;</td>
                <td style="text-align: center;">&nbsp;3</td>
                <td style="background-color: #e9ae6d; text-align: center;">&nbsp;W</td>
                </tr>
                <tr>
                <td style="border: medium solid #000000;" rowspan="2">ENE &lt; <strong>E</strong> &gt; ESE&nbsp;&nbsp;</td>
                <td style="background-color: #dea55e; text-align: center;">Flood&nbsp;</td>
                <td style="text-align: center;">&nbsp;1 or 4</td>
                <td style="background-color: #e9ae6d; text-align: center;">W&nbsp;</td>
                </tr>
                <tr>
                <td style="background-color: #dea55e; text-align: center;">&nbsp;Ebb</td>
                <td style="text-align: center;">&nbsp;3</td>
                <td style="background-color: #e9ae6d; text-align: center;">W&nbsp;</td>
                </tr>
                <tr>
                <td style="border: medium solid #000000;" rowspan="2">ESE &lt; <strong>SE</strong> &gt; SSE&nbsp;&nbsp;</td>
                <td style="background-color: #dea55e; text-align: center;">Flood&nbsp;</td>
                <td style="text-align: center;">&nbsp;1</td>
                <td style="background-color: #e9ae6d; text-align: center;">&nbsp;G</td>
                </tr>
                <tr>
                <td style="background-color: #dea55e; text-align: center;">&nbsp;Ebb</td>
                <td style="text-align: center;">&nbsp;3</td>
                <td style="background-color: #e9ae6d; text-align: center;">G or W&nbsp;</td>
                </tr>
                <tr>
                <td style="border: medium solid #000000;" rowspan="2">SSE &lt; <strong>S</strong> &gt; SSW&nbsp;&nbsp;</td>
                <td style="background-color: #dea55e; text-align: center;">Flood&nbsp;</td>
                <td style="text-align: center;">1, 2 or 4&nbsp;</td>
                <td style="background-color: #e9ae6d; text-align: center;">G&nbsp;</td>
                </tr>
                <tr>
                <td style="background-color: #dea55e; text-align: center;">Ebb&nbsp;</td>
                <td style="text-align: center;">&nbsp;3</td>
                <td style="background-color: #e9ae6d; text-align: center;">&nbsp;G</td>
                </tr>
                <tr>
                <td style="border: medium solid #000000;" rowspan="2">SSW &lt; <strong>SW</strong> &gt; WSW&nbsp;&nbsp;</td>
                <td style="background-color: #dea55e; text-align: center;">&nbsp;Flood</td>
                <td style="text-align: center;">2 or 4&nbsp;</td>
                <td style="background-color: #e9ae6d; text-align: center;">W&nbsp;</td>
                </tr>
                <tr>
                <td style="background-color: #dea55e; text-align: center;">&nbsp;Ebb</td>
                <td style="text-align: center;">&nbsp;3</td>
                <td style="background-color: #e9ae6d; text-align: center;">&nbsp;W</td>
                </tr>
                <tr>
                <td style="border: medium solid #000000;" rowspan="2">WSW &lt; <strong>W</strong> &gt; WNW&nbsp;&nbsp;</td>
                <td style="background-color: #dea55e; text-align: center;">Flood&nbsp;</td>
                <td style="text-align: center;">4&nbsp;</td>
                <td style="background-color: #e9ae6d; text-align: center;">&nbsp;W</td>
                </tr>
                <tr>
                <td style="background-color: #dea55e; text-align: center;">&nbsp;Ebb</td>
                <td style="text-align: center;">&nbsp;3</td>
                <td style="background-color: #e9ae6d; text-align: center;">&nbsp;W</td>
                </tr>
                <tr>
                <td style="border: medium solid #000000;" rowspan="2">WNW &lt; <strong>NW</strong> &gt; NNW&nbsp;&nbsp;</td>
                <td style="background-color: #dea55e; text-align: center;">Flood&nbsp;</td>
                <td style="text-align: center;">&nbsp;2 or 4</td>
                <td style="background-color: #e9ae6d; text-align: center;">&nbsp;W</td>
                </tr>
                <tr>
                <td style="background-color: #dea55e; text-align: center;">&nbsp;Ebb</td>
                <td style="text-align: center;">&nbsp;3</td>
                <td style="background-color: #e9ae6d; text-align: center;">&nbsp;W</td>
                </tr>
                </tbody>
                </table>
                """;
    }

    private String expected4() {
        return """
html
    line number="1"
    table
        colgroup style="width: 33%; "
            col style="font-size: 10px; "
            col style="font-weight: bold; "
            col
        thead
            tr
                th
                    "h1"
        tfoot
        line number="2"
        tbody
            line number="3"
            tr
                td colspan="3" style="font-size:10px;font-weight:bold;font-style:italic;"
                    "d1"
            line number="4"
            tr
                td colspan="2" style="font-weight:bold;font-size:10px;font-style:italic;"
                    "d2"
            line number="5"
            tr
                td style="font-style: italic; font-weight: bold; "
                    "d3"
                td
                    "x"
            line number="6"
            tr
                td style="font-style: italic; "
                    "d4"
                td
                    "x"
                td
                    "x"
            line number="7"
            tr
                td colspan="2" style="font-weight:bold;font-size:10px;"
                    "d5"
                td style="color: red; "
                    "x"
            line number="8"
        line number="9"
        line number="10"
        line number="11"
    line number="13"
    table
        colgroup style="width: 16%; "
            col
            col
            col
            col
            col
            col
        thead
            tr
                th
                    "h1"
        tfoot
        line number="14"
        tbody
            line number="15"
            tr
                td
                    "d1"
            line number="16"
            tr
                td
                    "d1"
                td
                    "d2"
                td
                    "d3"
            line number="17"
            tr
                td
                    "d1"
                td colspan="5"
                    "d2"
            line number="18"
        line number="19"
        line number="20"
        line number="21"
    line number="23"
    table
        colgroup style="width: 33%; "
            col
            col style="font-size: 10pt; "
            col
        thead
            tr
                th
                    "h1"
        tfoot
        line number="24"
        tbody
            line number="25"
            tr
                td
                    "d1"
                td
                    "d2"
                td
                    "d3"
            line number="26"
            tr
                td rowspan="2"
                    "d1"
                td
                    "d2"
                td
                    "d3"
            line number="27"
            tr
                td
                    "d2"
                td
                    "d3"
            line number="28"
            tr
                td
                    "d1"
                td
                    "d2"
            line number="29"
            tr
                td
                    "d1"
                td colspan="2" style="font-size:10pt"
                    "d2"
            line number="30"
            tr
                td colspan="2" rowspan="3" style="font-size:10pt"
                    "big"
                td
                    "d3"
            line number="31"
            tr
                td
                    "d3"
            line number="32"
            tr
                td
                    "d3"
            line number="33"
            tr
                td
                    "d1"
                td
                    "d2"
                td
                    "d3"
            line number="34"
        line number="35"
        line number="36"
        line number="37"
""";
    }
    
    private String expected5() {
        return """
html
    line number="1"
    table style="border: 0; margin-left: auto; margin-right: auto;"
        colgroup style="width: 25%; "
            col style="border: medium solid #000000; "
            col style="background-color: #dea55e; text-align: center; "
            col style="text-align: center; "
            col style="background-color: #e9ae6d; text-align: center; "
        thead
            tr style="background-color: #adadad;"
                th style="border: medium solid #000000;"
                    strong
                        "Wind Direction"
                th style="text-align: center;"
                    strong
                        "Tide Direction"
                th style="text-align: center;"
                    strong
                        "Preferred Course(s)"
                th style="text-align: center;"
                    strong
                        "Preferred Start Line"
        line number="2"
        tbody
            line number="3"
            line number="9"
            tr
                line number="10"
                td rowspan="2"
                    "NNW < "
                    strong
                        "N"
                    " > NNE "
                line number="11"
                td
                    "Flood"
                line number="12"
                td
                    "4"
                line number="13"
                td
                    "W"
                line number="14"
            line number="15"
            tr
                line number="16"
                td
                    "Ebb"
                line number="17"
                td
                    "3"
                line number="18"
                td
                    "W"
                line number="19"
            line number="20"
            tr
                line number="21"
                td rowspan="2"
                    "NNE < "
                    strong
                        "NE"
                    " > ENE  "
                line number="22"
                td
                    " Flood"
                line number="23"
                td
                    "1 or 4 "
                line number="24"
                td
                    " W"
                line number="25"
            line number="26"
            tr
                line number="27"
                td
                    "Ebb "
                line number="28"
                td
                    " 3"
                line number="29"
                td
                    " W"
                line number="30"
            line number="31"
            tr
                line number="32"
                td rowspan="2"
                    "ENE < "
                    strong
                        "E"
                    " > ESE  "
                line number="33"
                td
                    "Flood "
                line number="34"
                td
                    " 1 or 4"
                line number="35"
                td
                    "W "
                line number="36"
            line number="37"
            tr
                line number="38"
                td
                    " Ebb"
                line number="39"
                td
                    " 3"
                line number="40"
                td
                    "W "
                line number="41"
            line number="42"
            tr
                line number="43"
                td rowspan="2"
                    "ESE < "
                    strong
                        "SE"
                    " > SSE  "
                line number="44"
                td
                    "Flood "
                line number="45"
                td
                    " 1"
                line number="46"
                td
                    " G"
                line number="47"
            line number="48"
            tr
                line number="49"
                td
                    " Ebb"
                line number="50"
                td
                    " 3"
                line number="51"
                td
                    "G or W "
                line number="52"
            line number="53"
            tr
                line number="54"
                td rowspan="2"
                    "SSE < "
                    strong
                        "S"
                    " > SSW  "
                line number="55"
                td
                    "Flood "
                line number="56"
                td
                    "1, 2 or 4 "
                line number="57"
                td
                    "G "
                line number="58"
            line number="59"
            tr
                line number="60"
                td
                    "Ebb "
                line number="61"
                td
                    " 3"
                line number="62"
                td
                    " G"
                line number="63"
            line number="64"
            tr
                line number="65"
                td rowspan="2"
                    "SSW < "
                    strong
                        "SW"
                    " > WSW  "
                line number="66"
                td
                    " Flood"
                line number="67"
                td
                    "2 or 4 "
                line number="68"
                td
                    "W "
                line number="69"
            line number="70"
            tr
                line number="71"
                td
                    " Ebb"
                line number="72"
                td
                    " 3"
                line number="73"
                td
                    " W"
                line number="74"
            line number="75"
            tr
                line number="76"
                td rowspan="2"
                    "WSW < "
                    strong
                        "W"
                    " > WNW  "
                line number="77"
                td
                    "Flood "
                line number="78"
                td
                    "4 "
                line number="79"
                td
                    " W"
                line number="80"
            line number="81"
            tr
                line number="82"
                td
                    " Ebb"
                line number="83"
                td
                    " 3"
                line number="84"
                td
                    " W"
                line number="85"
            line number="86"
            tr
                line number="87"
                td rowspan="2"
                    "WNW < "
                    strong
                        "NW"
                    " > NNW  "
                line number="88"
                td
                    "Flood "
                line number="89"
                td
                    " 2 or 4"
                line number="90"
                td
                    " W"
                line number="91"
            line number="92"
            tr
                line number="93"
                td
                    " Ebb"
                line number="94"
                td
                    " 3"
                line number="95"
                td
                    " W"
                line number="96"
            line number="97"
        line number="98"
""";
    }
}
