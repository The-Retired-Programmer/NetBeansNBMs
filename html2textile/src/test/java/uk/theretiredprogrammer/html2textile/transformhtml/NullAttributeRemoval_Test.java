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


public class NullAttributeRemoval_Test extends TransformhtmlTest {

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        //
        transformer.transform(new NullAttributeRemoval());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String rules() {
        return  """
                """;
    }

    private String input() {
        return  """
                <p style="text-align:center;"></p>
                <p style=""></p>
                <p style=" "></p>
                <p style="" class="abc"></p>
                <p style="x:y" class="abc"></p>
                <p style=" " class="abc"></p>
                <p style="" class=" "></p>
                <p style=" " class=" "></p>
                <p style="x:y" class=" "></p>
                <p style="" class=""></p>
                <p style=" " class=""></p>
                <p style="x:y" class=""></p>
                <p class="abc"></p>
                """;
    }
    
    private String expected() {
        return """
               html
                   line number="1"
                   p style="text-align:center;"
                   line number="2"
                   p
                   line number="3"
                   p
                   line number="4"
                   p class="abc"
                   line number="5"
                   p class="abc" style="x:y"
                   line number="6"
                   p class="abc"
                   line number="7"
                   p
                   line number="8"
                   p
                   line number="9"
                   p style="x:y"
                   line number="10"
                   p
                   line number="11"
                   p
                   line number="12"
                   p style="x:y"
                   line number="13"
                   p class="abc"
               """;
    }
}
