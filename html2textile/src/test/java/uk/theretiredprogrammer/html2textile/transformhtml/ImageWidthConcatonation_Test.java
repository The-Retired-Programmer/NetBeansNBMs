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


public class ImageWidthConcatonation_Test extends TransformhtmlTest {

    public ImageWidthConcatonation_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        transformer.transform(Rules.get_HTML_STYLE_PROCESSING());
        //
        transformer.transform(new ImageWidthConcatonation());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }
    
    private String rules() {
        return """
                """;
    }

    private String input() {
        return """
                <p>
                    <img src="image" alt="image"/>
                    <img style="text-align:left;" src="image" alt="image"/>
                    <img style="text-align:left;" src="image" alt="image" width="100"/>
                    <img style="text-align:left;" src="image" alt="image" width="100px"/>
                    <img style="text-align:left;" src="image" alt="image" width="100mm"/>
                    <img style="text-align:left;" src="image" alt="image" width="400"/>
                    <img style="text-align:left;" src="image" alt="image" width="700"/>
                    <img  src="image" alt="image" width="700"/>
                    <img style="width:10%;" src="image" alt="image" width="700"/>
                </p>
                """;
    }

    private String expected() {
        return """
            html
                line number="1"
                p
                    line number="2"
                    img alt="image" src="image"
                    line number="3"
                    img alt="image" src="image" style="text-align: left; "
                    line number="4"
                    img alt="image" src="image" style="text-align: left; width: 20%; "
                    line number="5"
                    img alt="image" src="image" style="text-align: left; width: 20%; "
                    line number="6"
                    img alt="image" src="image" style="text-align: left; width: 20%; "
                    line number="7"
                    img alt="image" src="image" style="text-align: left; width: 50%; "
                    line number="8"
                    img alt="image" src="image" style="text-align: left; width: 100%; "
                    line number="9"
                    img alt="image" src="image" style="width: 100%; "
                    line number="10"
                    img alt="image" src="image" style="width: 10%; width: 100%; "
                    line number="11"
            """;
    }
}
