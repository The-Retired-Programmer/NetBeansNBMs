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


public class DomSerialisation_Test extends TransformhtmlTest {

    public DomSerialisation_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
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
                <div style="margin: 20px 20px 20px 20px; font-family: arial, helvetica, sans-serif; font-size: 12pt; line-height: 1.5em; color: #000000; ">
                    <p>This is wrapped in an RL div</p>
                    <p>and this</p>
                    <p>and finally</p>
                </div>
                """;
    }

    private String expected() {
        return """
               html
                   line number="1"
                   div style="margin: 20px 20px 20px 20px; font-family: arial, helvetica, sans-serif; font-size: 12pt; line-height: 1.5em; color: #000000; "
                       line number="2"
                       p
                           "This is wrapped in an RL div"
                       line number="3"
                       p
                           "and this"
                       line number="4"
                       p
                           "and finally"
                       line number="5"
               """;
    }
}
