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


public class RestuctureLeadingAndTrailingWhiteSpaceFromBracketingElements_Test extends TransformhtmlTest {

    public RestuctureLeadingAndTrailingWhiteSpaceFromBracketingElements_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation("restructurews");
        //
        transformer.transform(new RestuctureLeadingAndTrailingWhiteSpaceFromBracketingElements());
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
                       "start"
                       strong
                           "abcxyz"
                       "end"
                   line number="2"
                   p
                       "start "
                       strong
                           "abcxyz"
                       "end"
                   line number="3"
                   p
                       "start"
                       strong
                           "abcxyz"
                       " end"
                   line number="4"
                   p
                       "start"
                       " "
                       strong
                           "abcxyz"
                       "end"
                   line number="5"
                   p
                       "start"
                       strong
                           "abcxyz"
                       " "
                       "end"
                   line number="6"
                   p
                       "start"
                       " "
                       strong
                           "abcxyz"
                       " "
                       "end"
                   line number="7"
                   p
                       "start "
                       " "
                       strong
                           "abcxyz"
                       " "
                       " end"
                   line number="8"
                   p
                       "start"
                       " "
                       u
                           "abcxyz"
                       " "
                       "end"
                   line number="9"
                   p
                       "start"
                       " "
                       span
                           "abcxyz"
                       " "
                       "end"
                   line number="10"
                   p
                       "start"
                       " "
                       b
                           "abcxyz"
                       " "
                       "end"
                   line number="11"
                   p
                       "start"
                       " "
                       sup
                           "abcxyz"
                       " "
                       "end"
                   line number="12"
                   p
                       "start"
                       " "
                       sub
                           "abcxyz"
                       " "
                       "end"
                   line number="13"
                   p
                       "start"
                       " "
                       a
                           "abcxyz"
                       " "
                       "end"
                   line number="14"
                   p
                       "start"
                       td
                           " abcxyz "
                       "end"
                   line number="15"
                   p
                       "start"
                       " "
                       b
                           strong
                               "abcxyz"
                       " "
                       "end"
                   line number="16"
                   p
                       "start"
                       " "
                       sup
                           sub
                               span
                                   b
                                       strong
                                           "abcxyz"
                       " "
                       "end"
               """;
    }
}
