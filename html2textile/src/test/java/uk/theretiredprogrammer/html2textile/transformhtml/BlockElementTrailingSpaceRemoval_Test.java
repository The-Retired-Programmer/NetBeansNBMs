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
import uk.theretiredprogrammer.html2textile.rules.Rules;


public class BlockElementTrailingSpaceRemoval_Test extends TransformhtmlTest {

    public BlockElementTrailingSpaceRemoval_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation("blocktrailingspaceremoval");
        transformer.transform(new StyleNormalisation());
        transformer.transform(Rules.get_HTML_STYLE_PROCESSING());
        transformer.transform(new StyleNormalisation());
        //
        transformer.transform(new BlockElementTrailingSpaceRemoval());
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
                       u
                           "test"
                   line number="2"
                   p
                       u
                           "test"
                   line number="3"
                   p
                       u
                           "test"
                   line number="4"
                   p
                       u
                           "test"
                       line number="5"
                   line number="6"
                   p
                       " "
                       u
                           " text "
                   line number="7"
                   p
                       " "
                       u
                           " text "
                   line number="8"
                   p
                   line number="9"
                   ul
                       line number="10"
                       li
                           u
                               "test"
                       line number="11"
                       li
                           u
                               "test"
                       line number="12"
                   line number="13"
                   ul
                   line number="14"
                   ul
                   line number="15"
                   ul
                       li
                           "test"
                   line number="16"
                   ol
                   line number="17"
                   ol
                   line number="18"
                   ol
                       li
                           "test"
                   line number="19"
                   h3
                       b
                           "test"
                   line number="20"
                   p
                       " "
                       u
                           " "
                           b
                               "text"
                           " "
                       "abc"
                   line number="21"
                   ul
                       line number="22"
                       li
                           strong
                               "abc"
                           "def"
                           br
                       line number="23"
               """;
    }
}
