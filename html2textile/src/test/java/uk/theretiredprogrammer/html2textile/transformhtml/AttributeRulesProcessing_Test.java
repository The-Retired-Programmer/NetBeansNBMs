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

public class AttributeRulesProcessing_Test extends TransformhtmlTest {

    public AttributeRulesProcessing_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        //
        transformer.transform(Rules.get_HTML_ATTRIBUTE_PROCESSING());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String rules() {
        return  """
                [HTML_ATTRIBUTE_PROCESSING]
                    REMOVE dir IF auto
                    REMOVE id IF PATTERN cloak.*
                    MOVE width TO STYLE
                    MOVE text-align TO STYLE
                    REMOVE title IF ELEMENT a
                """;
    }

    private String input() {
        return  """
                <p>content</p>
                <p style="font-size: 10pt;">content</p>
                <p width="100%">content</p>
                <p width="100%" style="font-size: 10pt;">content</p>
                <p text-align="center" style="font-size: 10pt;">content</p>
                <p border="0" style="font-size: 10pt;">content</p>
                <p dir="auto">content</p>
                <p dir="right">content</p>
                <a title="remove_me" />
                <img title="keep me" />
                """;
    }

    private String expected() {
        return  """
                html
                    line number="1"
                    p
                        "content"
                    line number="2"
                    p style="font-size: 10pt;"
                        "content"
                    line number="3"
                    p style="width: 100%; "
                        "content"
                    line number="4"
                    p style="font-size: 10pt; width: 100%; "
                        "content"
                    line number="5"
                    p style="font-size: 10pt; text-align: center; "
                        "content"
                    line number="6"
                    p border="0" style="font-size: 10pt;"
                        "content"
                    line number="7"
                    p
                        "content"
                    line number="8"
                    p dir="right"
                        "content"
                    line number="9"
                    a
                    line number="10"
                    img title="keep me"
                """;
    }
}
