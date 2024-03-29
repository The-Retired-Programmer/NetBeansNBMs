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

public class StyleAttributesRulesProcessing_Test extends TransformhtmlTest {

    public StyleAttributesRulesProcessing_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        //
        transformer.transform(Rules.get_HTML_STYLE_PROCESSING());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String rules() {
        return """
                [HTML_STYLE_PROCESSING]
                    MOVE PATTERN text-align:.* TO ATTRIBUTE
                    REMOVE ANY font-family
                    REMOVE "font-size: 10pt; "
                    REMOVE "font-size: 12pt; "
                    REMOVE "color: #000000; "
                    REMOVE "background-color: inherit; "
                    MOVE font-weight: bold; TO ELEMENT strong
                """;
    }

    private String input() {
        return """
                <p style="text-align: right; ">content</p>
                <p style="font-family: serif; ">content</p>
                <p style="color: #000000; background-color: inherit; ">content</p>
                <p></p>
                <p style="margin: 20px 20px 20px 20px; font-family: arial, helvetica, sans-serif; font-size: 12pt; line-height: 1.5em; color: #000000; ">content</p>
                <p style="font-weight: bold; ">content</p>
                """;
    }

    private String expected() {
        return """
                html
                    line number="1"
                    p text-align="right"
                        "content"
                    line number="2"
                    p
                        "content"
                    line number="3"
                    p
                        "content"
                    line number="4"
                    p
                    line number="5"
                    p style="margin: 20px 20px 20px 20px; line-height: 1.5em; "
                        "content"
                    line number="6"
                    p
                        strong
                            "content"
                """;
    }
}
