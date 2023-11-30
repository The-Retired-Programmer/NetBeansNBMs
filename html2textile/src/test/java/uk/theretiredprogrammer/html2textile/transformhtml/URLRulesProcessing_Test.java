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

public class URLRulesProcessing_Test extends TransformhtmlTest {

    public URLRulesProcessing_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        TransformHtml transformer = super.createtransformation(new StringReader(rules()), new StringReader(input()));
        //
        transformer.transform(Rules.get_HTML_URL_PROCESSING());
        //
        String result = SerialiseDom.serialise(transformer.getRoot());
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String rules() {
        return  """
                [HTML_URL_PROCESSING]
                    MAP IMG  assets2/image/fred.png TO exe-sailing-club.org/assets2/image/fred.png
                    MAP IMG PATTERN (assets/.*)  TO exe-sailing-club.org/legacy/$1
                    MAP A  exe-sailing-club.org/dinghies/rules.html TO exe-sailing-club.org/legacy/dinghies/rules.html
                    MAP A PATTERN www.exe-sailing-club.org/(.*\\.html) TO exe-sailing-club.org/legacy/$1
                """;
    }

    private String input() {
        return  """
                <img src="assets2/image/fred.png"/>
                <img src="assets/image/mike.png"/>
                <img src="assets/image/dir/subdir/mike2.png"/>
                <a href="exe-sailing-club.org/dinghies/rules.html">HERE</a>
                <a href="www.exe-sailing-club.org/dinghies/rules.html">HERE</a>
                <a href="www.exe-sailing-club.org/a/b/dinghies/rules.html">HERE</a>
                """;
    }

    private String expected() {
        return  """
                html
                    line number="1"
                    img scr="exe-sailing-club.org/assets2/image/fred.png" src="exe-sailing-club.org/assets2/image/fred.png"
                    line number="2"
                    img scr="exe-sailing-club.org/legacy/assets/image/mike.png" src="assets/image/mike.png"
                    line number="3"
                    img scr="exe-sailing-club.org/legacy/assets/image/dir/subdir/mike2.png" src="assets/image/dir/subdir/mike2.png"
                    line number="4"
                    a href="exe-sailing-club.org/legacy/dinghies/rules.html"
                        "HERE"
                    line number="5"
                    a href="exe-sailing-club.org/legacy/dinghies/rules.html"
                        "HERE"
                    line number="6"
                    a href="exe-sailing-club.org/legacy/a/b/dinghies/rules.html"
                        "HERE"
                """;
    }
}
