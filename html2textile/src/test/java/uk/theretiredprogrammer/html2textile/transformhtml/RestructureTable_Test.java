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
import uk.theretiredprogrammer.html2textile.RegexTransformationRuleSet;

public class RestructureTable_Test extends TransformhtmlTest {

    public RestructureTable_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        RegexTransformationRuleSet ruleset = new RegexTransformationRuleSet();
        TransformHtml transformer = super.createtransformation("restructuretable", ruleset);
        transformer.transform(new StyleNormalisation());
        //
        transformer.transform(new RestructureTable());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String expected() {
        return """
html
    line number="1"
    table
        colgroup style="width:33%;"
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
    line number="11"
    table
        colgroup style="width:100%;"
            col
        thead
            tr
                th
                    "d1"
        line number="12"
        tbody
            line number="13"
            line number="14"
            tr
                td
                    "d2"
            line number="15"
        line number="16"
    line number="18"
    table
        colgroup style="width:33%;"
            col
            col
            col
        thead
            tr
                th
                    "h1"
        line number="19"
        tfoot
        line number="20"
        line number="21"
        tbody
            line number="22"
            tr
                td
                    "d1"
                td
                    "d2"
                td
                    "d3"
            line number="23"
        line number="24"
    line number="26"
    table
        colgroup style="width:33%;"
            col style="font-size:10px;"
            col
            col
        thead
            tr
                th
                    "h1"
        tfoot
        line number="27"
        tbody
            line number="28"
            tr
                td style="font-weight:bold;font-style:italic;"
                    "d1"
            line number="29"
            tr
                td style="font-weight:bold;font-style:italic;"
                    "d1a"
            line number="30"
            tr
                td style="font-style:italic;font-weight:bold;"
                    "d1b"
            line number="31"
            tr
                td style="font-style:italic;"
                    "d1"
                td
                    "d2"
                td
                    "d3"
            line number="32"
            tr
                td style="font-weight:bold;"
                    "d1"
                td
                    "d2"
            line number="33"
        line number="34"
        line number="35"
        line number="36"
    line number="38"
    table
        colgroup style="width:33%;"
            col style="font-size:10px;"
            col style="font-weight:bold;"
            col
        thead
            tr
                th
                    "h1"
        tfoot
        line number="39"
        tbody
            line number="40"
            tr
                td colspan="3" style="font-size:10px;font-weight:bold;font-style:italic;"
                    "d1"
            line number="41"
            tr
                td colspan="2" style="font-weight:bold;font-size:10px;font-style:italic;"
                    "d2"
            line number="42"
            tr
                td style="font-style:italic;font-weight:bold;"
                    "d3"
                td
                    "x"
            line number="43"
            tr
                td style="font-style:italic;"
                    "d4"
                td
                    "x"
                td
                    "x"
            line number="44"
            tr
                td colspan="2" style="font-weight:bold;font-size:10px;"
                    "d5"
                td style="color:red;"
                    "x"
            line number="45"
        line number="46"
        line number="47"
        line number="48"
    line number="50"
    table
        colgroup style="width:16%;"
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
        line number="51"
        tbody
            line number="52"
            tr
                td
                    "d1"
            line number="53"
            tr
                td
                    "d1"
                td
                    "d2"
                td
                    "d3"
            line number="54"
            tr
                td
                    "d1"
                td colspan="5"
                    "d2"
            line number="55"
        line number="56"
        line number="57"
        line number="58"
    line number="60"
    table
        colgroup style="width:33%;"
            col
            col style="font-size:10pt;"
            col
        thead
            tr
                th
                    "h1"
        tfoot
        line number="61"
        tbody
            line number="62"
            tr
                td
                    "d1"
                td
                    "d2"
                td
                    "d3"
            line number="63"
            tr
                td rowspan="2"
                    "d1"
                td
                    "d2"
                td
                    "d3"
            line number="64"
            tr
                td
                    "d2"
                td
                    "d3"
            line number="65"
            tr
                td
                    "d1"
                td
                    "d2"
            line number="66"
            tr
                td
                    "d1"
                td colspan="2" style="font-size:10pt;"
                    "d2"
            line number="67"
            tr
                td colspan="2" rowspan="3" style="font-size:10pt;"
                    "big"
                td
                    "d3"
            line number="68"
            tr
                td
                    "d3"
            line number="69"
            tr
                td
                    "d3"
            line number="70"
            tr
                td
                    "d1"
                td
                    "d2"
                td
                    "d3"
            line number="71"
        line number="72"
        line number="73"
        line number="74"
    line number="76"
    table style="border:0;margin-left:auto;margin-right:auto;"
        colgroup style="width:25%;"
            col style="border:mediumsolid#000000;"
            col style="text-align:center;"
            col style="text-align:center;"
            col style="text-align:center;"
        thead
            tr style="background-color:#adadad;"
                th
                    strong
                        "Wind Direction"
                th
                    strong
                        "Tide Direction"
                th
                    strong
                        "Preferred Course(s)"
                th
                    strong
                        "Preferred Start Line"
        line number="77"
        tbody
            line number="78"
            line number="84"
            tr
                line number="85"
                td rowspan="2"
                    "NNW < "
                    strong
                        "N"
                    " > NNE "
                line number="86"
                td style="background-color:#dea55e;"
                    "Flood"
                line number="87"
                td
                    "4"
                line number="88"
                td style="background-color:#e9ae6d;"
                    "W"
                line number="89"
            line number="90"
            tr
                line number="91"
                td style="background-color:#dea55e;"
                    "Ebb"
                line number="92"
                td
                    "3"
                line number="93"
                td style="background-color:#e9ae6d;"
                    "W"
                line number="94"
            line number="95"
            tr
                line number="96"
                td rowspan="2"
                    "NNE < "
                    strong
                        "NE"
                    " > ENE  "
                line number="97"
                td style="background-color:#dea55e;"
                    " Flood"
                line number="98"
                td
                    "1 or 4 "
                line number="99"
                td style="background-color:#e9ae6d;"
                    " W"
                line number="100"
            line number="101"
            tr
                line number="102"
                td style="background-color:#dea55e;"
                    "Ebb "
                line number="103"
                td
                    " 3"
                line number="104"
                td style="background-color:#e9ae6d;"
                    " W"
                line number="105"
            line number="106"
            tr
                line number="107"
                td rowspan="2"
                    "ENE < "
                    strong
                        "E"
                    " > ESE  "
                line number="108"
                td style="background-color:#dea55e;"
                    "Flood "
                line number="109"
                td
                    " 1 or 4"
                line number="110"
                td style="background-color:#e9ae6d;"
                    "W "
                line number="111"
            line number="112"
            tr
                line number="113"
                td style="background-color:#dea55e;"
                    " Ebb"
                line number="114"
                td
                    " 3"
                line number="115"
                td style="background-color:#e9ae6d;"
                    "W "
                line number="116"
            line number="117"
            tr
                line number="118"
                td rowspan="2"
                    "ESE < "
                    strong
                        "SE"
                    " > SSE  "
                line number="119"
                td style="background-color:#dea55e;"
                    "Flood "
                line number="120"
                td
                    " 1"
                line number="121"
                td style="background-color:#e9ae6d;"
                    " G"
                line number="122"
            line number="123"
            tr
                line number="124"
                td style="background-color:#dea55e;"
                    " Ebb"
                line number="125"
                td
                    " 3"
                line number="126"
                td style="background-color:#e9ae6d;"
                    "G or W "
                line number="127"
            line number="128"
            tr
                line number="129"
                td rowspan="2"
                    "SSE < "
                    strong
                        "S"
                    " > SSW  "
                line number="130"
                td style="background-color:#dea55e;"
                    "Flood "
                line number="131"
                td
                    "1, 2 or 4 "
                line number="132"
                td style="background-color:#e9ae6d;"
                    "G "
                line number="133"
            line number="134"
            tr
                line number="135"
                td style="background-color:#dea55e;"
                    "Ebb "
                line number="136"
                td
                    " 3"
                line number="137"
                td style="background-color:#e9ae6d;"
                    " G"
                line number="138"
            line number="139"
            tr
                line number="140"
                td rowspan="2"
                    "SSW < "
                    strong
                        "SW"
                    " > WSW  "
                line number="141"
                td style="background-color:#dea55e;"
                    " Flood"
                line number="142"
                td
                    "2 or 4 "
                line number="143"
                td style="background-color:#e9ae6d;"
                    "W "
                line number="144"
            line number="145"
            tr
                line number="146"
                td style="background-color:#dea55e;"
                    " Ebb"
                line number="147"
                td
                    " 3"
                line number="148"
                td style="background-color:#e9ae6d;"
                    " W"
                line number="149"
            line number="150"
            tr
                line number="151"
                td rowspan="2"
                    "WSW < "
                    strong
                        "W"
                    " > WNW  "
                line number="152"
                td style="background-color:#dea55e;"
                    "Flood "
                line number="153"
                td
                    "4 "
                line number="154"
                td style="background-color:#e9ae6d;"
                    " W"
                line number="155"
            line number="156"
            tr
                line number="157"
                td style="background-color:#dea55e;"
                    " Ebb"
                line number="158"
                td
                    " 3"
                line number="159"
                td style="background-color:#e9ae6d;"
                    " W"
                line number="160"
            line number="161"
            tr
                line number="162"
                td rowspan="2"
                    "WNW < "
                    strong
                        "NW"
                    " > NNW  "
                line number="163"
                td style="background-color:#dea55e;"
                    "Flood "
                line number="164"
                td
                    " 2 or 4"
                line number="165"
                td style="background-color:#e9ae6d;"
                    " W"
                line number="166"
            line number="167"
            tr
                line number="168"
                td style="background-color:#dea55e;"
                    " Ebb"
                line number="169"
                td
                    " 3"
                line number="170"
                td style="background-color:#e9ae6d;"
                    " W"
                line number="171"
            line number="172"
        line number="173"
""";
    }
}
