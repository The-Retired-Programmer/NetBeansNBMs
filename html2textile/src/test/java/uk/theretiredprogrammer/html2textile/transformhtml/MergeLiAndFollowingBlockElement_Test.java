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

public class MergeLiAndFollowingBlockElement_Test extends TransformhtmlTest {

    public MergeLiAndFollowingBlockElement_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        RegexTransformationRuleSet ruleset = new RegexTransformationRuleSet();
        TransformHtml transformer = super.createtransformation("mergeliandfollowingblockelement", ruleset);
        transformer.transform(new StyleNormalisation());
        //
        transformer.transform(new MergeLiAndFollowingBlockElement());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String expected() {
        return """
                html
                    line number="1"
                    ul
                        line number="2"
                        li
                            "abc"
                        line number="3"
                        li
                            "abc"
                        line number="4"
                        li
                            line number="5"
                            line number="6"
                            "xyz"
                            line number="7"
                            line number="8"
                        line number="9"
                        li style="font-size=12pt;"
                            line number="10"
                            "ijk"
                            line number="11"
                        line number="12"
                        li style="font-size=12pt;"
                            line number="13"
                            "ijk"
                            line number="14"
                        line number="15"
                        li style="font-size=12pt;font-size=18pt;"
                            line number="16"
                            "xyz"
                            line number="17"
                        line number="18"
                """;
    }
}
