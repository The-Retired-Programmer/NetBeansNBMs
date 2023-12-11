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
package uk.theretiredprogrammer.html2textile.integrationtest;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.html2textile.App;

public class ForDebugging_App_Test {

    public ForDebugging_App_Test() {
    }

//    @Test
    public void testApp() throws IOException, ParserConfigurationException, SAXException, URISyntaxException, TransformerException {
        String[] args = new String[]{"-i", "/home/richard/PRODUCTS/ESC_WEBSITE/Joomla2SCM/Dinghies/raceofficersbriefing.fragment.html"};
        int rc = new App().goInner(args);
        assertEquals(0,rc);
    }

}
