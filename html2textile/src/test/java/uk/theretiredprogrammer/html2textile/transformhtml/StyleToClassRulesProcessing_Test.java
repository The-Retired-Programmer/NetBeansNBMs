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

public class StyleToClassRulesProcessing_Test extends TransformhtmlTest {

    public StyleToClassRulesProcessing_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        //
        transformer.transform(Rules.get_HTML_STYLE_TO_CLASS_PROCESSING());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String rules() {
        return  """
                [HTML_STYLE_TO_CLASS_PROCESSING]
                    REPLACE EXACT MATCH OF STYLES vertical-align:middle WITH CLASS valign_middle
                    REPLACE EXACT MATCH OF STYLES vertical-align:top WITH CLASS valign_top
                    REPLACE EXACT MATCH OF STYLES vertical-align:bottom WITH CLASS valign_bottom
                    REPLACE EXACT MATCH OF STYLES font-weight: bold AND text-decoration: underline WITH CLASS boldunderline
                    REPLACE EXACT MATCH OF STYLES text-align:right WITH CLASS alignright
                    REPLACE EXACT MATCH OF STYLES font-weight: bold WITH CLASS bold
                    REPLACE PARTIAL MATCH OF STYLES width:50% AND float:left WITH CLASS float-left-50
                """;
    }

    private String input() {
        return  """
                <p style="text-align: right; "/>
                <p class="prettyprint" style="text-align: right; "/>
                <p style="font-weight: bold; text-align: right; src: info"/>
                <p style="font-weight: bold; text-decoration: underline "/>
                <p style="text-decoration: underline;font-weight: bold;  "/>
                <p style="vertical-align:middle; vertical-align:top;vertical-align:bottom;"/>
                <p style="vertical-align:middle; text-align: right;"/>
                <p style="text-align: right; text-align: right; text-align: right; "/>
                <p style="margin: 0px 0px 0px 100px; padding: 0; float: left; width: 50%;" />
                """;
    }

    private String expected() {
        return  """
                html
                    line number="1"
                    p class="alignright"
                    line number="2"
                    p class="prettyprint alignright"
                    line number="3"
                    p class="alignright bold" style="src: info; "
                    line number="4"
                    p class="boldunderline"
                    line number="5"
                    p class="boldunderline"
                    line number="6"
                    p class="valign_middle valign_top valign_bottom"
                    line number="7"
                    p class="valign_middle alignright"
                    line number="8"
                    p class="alignright" style="text-align: right; text-align: right; "
                    line number="9"
                    p class="float-left-50"
                """;
    }
}
