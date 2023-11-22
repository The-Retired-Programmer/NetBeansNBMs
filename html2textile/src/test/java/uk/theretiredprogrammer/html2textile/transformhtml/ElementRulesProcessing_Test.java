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
    
    private String rules() {
        return  """
                [HTML_ELEMENT_PROCESSING]
                    REMOVE header
                    REMOVE section
                """;
    }

    private String input() {
        return  """
                <header></header>
                <header>
                content <strong> strong content </strong> final content
                </header>
                <section></section>
                """;
    }

    private String expected() {
        return  """
                html
                    line number="1"
                    line number="2"
                    line number="3"
                    "content "
                    strong
                        " strong content "
                    " final content"
                    line number="4"
                    line number="5"
                """;
    }
}