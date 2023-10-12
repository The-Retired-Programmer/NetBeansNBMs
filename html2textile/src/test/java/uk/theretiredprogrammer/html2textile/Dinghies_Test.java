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
package uk.theretiredprogrammer.html2textile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;

public class Dinghies_Test {

    public Dinghies_Test() {
    }

    @Test
    public void testDinghiesConversion() throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, TransformerException {
        try ( Reader from = new FileReader("/home/richard/PRODUCTSTESTDATA/html2textile/dinghies.html");  PrintWriter to = new PrintWriter(new FileWriter("/home/richard/PRODUCTSTESTDATA/html2textile/dinghies.textile")); PrintWriter err = new PrintWriter(System.err)) {
            Html2Textile.convert(from, to, err);
        }
    }

}
