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


public class ImageCaptionReduction_Test extends TransformhtmlTest {

    public ImageCaptionReduction_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation("imagecaptionreduction");
        transformer.transform(new StyleNormalisation());
        //
        transformer.transform(new ImageCaptionReduction());
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
                    line number="2"
                    img alt="image" src="image"
                    line number="3"
                    "Hello World"
                line number="4"
                p
                    line number="5"
                    img alt="image" src="image"
                    "dummy"
                    br
                    "Hello World"
                line number="6"
                p
                    line number="7"
                    img alt="image" src="image"
                    line number="8"
                    line number="9"
                    b
                        "Hello World"
                    line number="10"
                line number="11"
                p
                    line number="12"
                    img alt="image" src="image"
                    line number="13"
                    line number="14"
                line number="15"
                p
                    line number="16"
                    img alt="image" src="image"
                    line number="17"
            """;
    }
}
