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

public class DivReduction_Test extends TransformhtmlTest {

    public DivReduction_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        RegexTransformationRuleSet ruleset = new RegexTransformationRuleSet();
        TransformHtml transformer = super.createtransformation("divreduction", ruleset);
        transformer.transform(new StyleNormalisation());
        //
        transformer.transform(new DivReduction());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String expected() {
        return """
            html
                line number="1"
                p
                    span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                        strong
                            "About Girls Go Sailing"
                line number="2"
                p
                    span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                        "In 2018 we saw a great revival of our Girls Go Sailing scheme, which we have run intermittently for a few years. By making it a member lead activity, we have seen increased numbers of women participating and welcome new members at any time throughout the year."
                line number="3"
                p
                    " "
                line number="4"
                p
                    " "
                line number="5"
                p
                    span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                        strong
                            "What do we do?"
                line number="6"
                line number="7"
                ul
                    line number="8"
                    li
                        span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                            "We are offering our female members sailing opportunities on cruisers, dinghies and the club Hawk, running the sessions at varying times – some weekends, some weekdays, some evenings, throughout the year."
                    line number="9"
                    li
                        span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                            "We encourage participation in shore-based theory sessions and offer knowledge and safety seminars through the winter and spring."
                    line number="10"
                    li
                        span style="font-family:arial,helvetica,sans-serif;font-size:12pt;"
                            "We are also planning some social evenings throughout the year."
                    line number="11"
                line number="12"
                line number="13"
                line number="14"
                p style="margin:50px00100px;padding:0;float:right;width:20%;text-align:center;line-height:12pt;"
                    img alt="Rob Vince" src="assets/images/Dinghies2/Rob_Vince_2.jpg" style="width:100%;margin:0;padding:0;"
                    br
                    "Rob Vince"
                line number="15"
                line number="16"
                line number="17"
                p
                    "para1"
                line number="18"
                p
                    "para2"
                line number="19"
                p
                    "para3"
                line number="20"
                line number="21"
                p
                    line number="22"
                    p
                        "para1"
                    line number="23"
                    b
                        "para2"
                    line number="24"
                    p
                        "para3"
                    line number="25"
            """;
    }
}
