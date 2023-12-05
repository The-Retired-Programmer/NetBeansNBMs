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
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.html2textile.rules.Rules;
import uk.theretiredprogrammer.html2textile.transformtext.TransformHtmlText;

public class TransformhtmlTest {

    public TransformHtml createtransformation(String inputname) throws IOException, ParserConfigurationException, SAXException {
        Rules.create();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/transformhtml/example_" + inputname + ".html");
        return commontransformation(new InputStreamReader(is));
    }

    public TransformHtml createtransformation(Reader input) throws IOException, ParserConfigurationException, SAXException {
        Rules.create();
        return commontransformation(input);
    }

    public TransformHtml createtransformation(Reader rules, Reader input) throws IOException, ParserConfigurationException, SAXException {
        Rules.create(rules);
        return commontransformation(input);
    }

    public TransformHtml commontransformation(Reader input) throws IOException, ParserConfigurationException, SAXException {
        TransformHtmlText texttransformer = Rules.get_HTML_PREPROCESSING();
        texttransformer.setReader(input);
        texttransformer.rootWrap("html");
        try ( Reader transformed = texttransformer.transform()) {
            return new TransformHtml(transformed);
        }
    }
}
