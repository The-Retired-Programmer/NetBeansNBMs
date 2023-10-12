/*
 * Copyright 2023 richard.
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Reader rdr = new InputStreamReader(System.in);
        PrintWriter wtr = new PrintWriter(System.out);
        PrintWriter err = new PrintWriter(System.err);
        //
        try (rdr; wtr; err) {
            Html2Textile.convert(rdr, wtr);
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
            err.println("Html2Textile: failed -" + ex.getLocalizedMessage());
            System.exit(4);
        }
    }
}
