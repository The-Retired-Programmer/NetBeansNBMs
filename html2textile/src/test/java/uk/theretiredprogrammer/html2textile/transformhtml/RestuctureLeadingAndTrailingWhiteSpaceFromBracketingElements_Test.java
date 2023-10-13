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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class RestuctureLeadingAndTrailingWhiteSpaceFromBracketingElements_Test {

    public RestuctureLeadingAndTrailingWhiteSpaceFromBracketingElements_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/transformhtml/example_restructurews.html");
        Reader in = new InputStreamReader(is);
        TransformHtml transformer = new TransformHtml(in);
        transformer.transform(new IndentAndReturnsRemoval());
        transformer.transform(new StyleNormalisation());
        //
        transformer.transform(new RestuctureLeadingAndTrailingWhiteSpaceFromBracketingElements());
        //
        String result = transformer.getSerialisedDOM();
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String expected() {
        return """
               html
                   p
                       "start"
                       strong
                           "abcxyz"
                       "end"
                   p
                       "start "
                       strong
                           "abcxyz"
                       "end"
                   p
                       "start"
                       strong
                           "abcxyz"
                       " end"
                   p
                       "start"
                       " "
                       strong
                           "abcxyz"
                       "end"
                   p
                       "start"
                       strong
                           "abcxyz"
                       " "
                       "end"
                   p
                       "start"
                       " "
                       strong
                           "abcxyz"
                       " "
                       "end"
                   p
                       "start "
                       " "
                       strong
                           "abcxyz"
                       " "
                       " end"
                   p
                       "start"
                       " "
                       u
                           "abcxyz"
                       " "
                       "end"
                   p
                       "start"
                       " "
                       span
                           "abcxyz"
                       " "
                       "end"
                   p
                       "start"
                       " "
                       b
                           "abcxyz"
                       " "
                       "end"
                   p
                       "start"
                       " "
                       sup
                           "abcxyz"
                       " "
                       "end"
                   p
                       "start"
                       " "
                       sub
                           "abcxyz"
                       " "
                       "end"
                   p
                       "start"
                       " "
                       a
                           "abcxyz"
                       " "
                       "end"
                   p
                       "start"
                       td
                           " abcxyz "
                       "end"
                   p
                       "start"
                       " "
                       b
                           strong
                               "abcxyz"
                       " "
                       "end"
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
