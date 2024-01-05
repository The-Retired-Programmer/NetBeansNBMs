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


public class MergeSpansIfSame_Test extends TransformhtmlTest {

    public MergeSpansIfSame_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        //
        transformer.transform(new MergeSpansIfSame());
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
                <p><span>abc</span><span>xyz</span></p>
                <p><span style="font-weight: bold;">abc</span><span style="font-weight: bold;">xyz</span></p>
                <p><span style="font-weight: bold; font-style: italic">abc</span><span style="font-style: italic; font-weight: bold;">xyz</span></p>
                <p><span style="font-weight: bold;">abc</span><span style="font-weight: bold;">xyz</span><span style="font-weight: bold;">123</span></p>
                <p><span style="font-weight: bold; color: red; ">abc</span><span style="font-weight: bold;">xyz</span></p>
                <p><span style="font-weight: bold; ">abc</span><span style=" color: red; font-weight: bold;">xyz</span></p>
                """;
    }

    private String expected() {
        return """
                html
                    line number="1"
                    p
                        span
                            "abc"
                            "xyz"
                    line number="2"
                    p
                        span style="font-weight: bold;"
                            "abc"
                            "xyz"
                    line number="3"
                    p
                        span style="font-style: italic; font-weight: bold;" 
                            "abc"
                            "xyz"
                    line number="4"
                    p
                        span style="font-weight: bold;"
                            "abc"
                            "xyz"
                            "123"
                    line number="5"
                    p
                        span style="font-weight: bold; color: red; "
                            "abc"
                        span style="font-weight: bold;"
                            "xyz"
                    line number="6"
                    p
                        span style="font-weight: bold; "
                            "abc"
                        span style=" color: red; font-weight: bold;"
                            "xyz"
                """;
    }
}