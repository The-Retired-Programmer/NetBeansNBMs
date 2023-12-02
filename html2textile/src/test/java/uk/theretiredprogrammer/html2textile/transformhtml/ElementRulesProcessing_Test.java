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
import uk.theretiredprogrammer.html2textile.rules.Rules;

public class ElementRulesProcessing_Test extends TransformhtmlTest {

    public ElementRulesProcessing_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        //
        transformer.transform(Rules.get_HTML_ELEMENT_PROCESSING());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    @Test
    public void testtransformation2() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules2()), new StringReader(input2()));
        //
        transformer.transform(Rules.get_HTML_FINAL_ELEMENT_PROCESSING());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected2(), result);
    }

    private String rules() {
        return """
                [HTML_ELEMENT_PROCESSING]
                    REMOVE header
                    REMOVE section
                    REPLACE strong WITH span AND STYLE font-weight: bold;
                    REPLACE b WITH span AND STYLE font-weight: bold;
                    REPLACE u WITH span AND STYLE text-decoration: underline;
                    REPLACE i WITH span AND STYLE font-style: italic
                    REPLACE em WITH span AND STYLE font-style:italic
                    REMOVE div IF STYLES margin: 20px 20px 20px 20px; AND font-family: arial,helvetica,sans-serif; AND font-size: 12pt; AND line-height: 1.5em; AND color: #000000;
                """;
    }

    private String input() {
        return """
                <header></header>
                <header>
                content content content
                </header>
                <section></section>
                <em>em</em>
                <i>i</i>
                <u>u</u>
                <b>b</b>
                <strong>strong</strong>
                <div style="margin: 20px 20px 20px 20px; font-family: arial,helvetica,sans-serif; font-size: 12pt; line-height: 1.5em; color: #000000;">
                content
                <em>em</em>
                </div>
                <div style="margin: 20px 20px 20px 20px; font-family: arial,helvetica,sans-serif; font-size: 10pt; line-height: 1.5em; color: #000000;">
                content
                <em>em</em>
                </div>
                """;
    }

    private String expected() {
        return """
                html
                    line number="1"
                    line number="2"
                    line number="3"
                    "content content content"
                    line number="4"
                    line number="5"
                    line number="6"
                    span style="font-style: italic; "
                        "em"
                    line number="7"
                    span style="font-style: italic; "
                        "i"
                    line number="8"
                    span style="text-decoration: underline; "
                        "u"
                    line number="9"
                    span style="font-weight: bold; "
                        "b"
                    line number="10"
                    span style="font-weight: bold; "
                        "strong"
                    line number="11"
                    line number="12"
                    "content"
                    line number="13"
                    span style="font-style: italic; "
                        "em"
                    line number="14"
                    line number="15"
                    div style="margin: 20px 20px 20px 20px; font-family: arial,helvetica,sans-serif; font-size: 10pt; line-height: 1.5em; color: #000000;"
                        line number="16"
                        "content"
                        line number="17"
                        span style="font-style: italic; "
                            "em"
                        line number="18"
                """;
    }

    private String rules2() {
        return """
                [HTML_FINAL_ELEMENT_PROCESSING]
                    REPLACE span AND STYLE font-style:italic WITH em
                    REMOVE span IF STYLE IS EMPTY 
                    REMOVE div IF NO ATTRIBUTES
                """;
    }

    private String input2() {
        return  """
                <span style="color:red; ">content1</span>
                <span style="">content2</span>
                <span style="font-style:italic; ">content3</span>
                <span >content4</span>
                <span style="font-style:italic; color:red; ">content5</span>
                <div>content6</div>
                <div class="BOLD">content7</div>
                """;
    }

    private String expected2() {
        return """
                html
                    line number="1"
                    span style="color:red; "
                        "content1"
                    line number="2"
                    "content2"
                    line number="3"
                    em
                        "content3"
                    line number="4"
                    "content4"
                    line number="5"
                    span style="color: red; "
                        em
                            "content5"
                    line number="6"
                    "content6"
                    line number="7"
                    div class="BOLD"
                        "content7"
                """;
    }
}
