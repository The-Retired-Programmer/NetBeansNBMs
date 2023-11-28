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
package uk.theretiredprogrammer.html2textile.smoketest;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class Dinghies_SmokeTest extends SmokeTest {

    public Dinghies_SmokeTest() {
    }

//    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException, TransformerException {
        transformation("sample_dinghies.html", "DINGHIES_SMOKE_TEST.html", "DINGHIES_SMOKE_TEST.textile", expected);
    }

    String expected = """
    !(float-left-50)https://files.exe-sailing-club.org/images/Dinghies2/Dinghies_2.jpg(Dinghies)!
     
    Dinghies are at the heart of sailing at Exe SC.The wide expanse of the Exe estuary and immediate access to the sea provide excellent opportunities for our sport. Whether you are an experienced dinghy sailor or a complete novice, young or old, Exe SC provides the facilities, training and coaching needed for you to fully enjoy the racing and cruising opportunities available.
     
    We actively encourage junior sailors. Starting at a young age with splash nights, progressing through learning to sail at our RYA accredited "Training Centre":training/training-link.html  and moving on through race coaching, our "Junior section":juniors.html  is a vibrant part of the club.
     
    The Training Centre equally runs courses for adults, from novice to experienced, providing opportunities to continue sailing through the "use of club boats":training/boat-hire.html  and regular "improvers":index.php?option=com_content&view=article&id=294  sessions.
     
    Exe SC actively encourages the participation of women in all forms of sailing, through the "Girls Go Sailing":index.php?option=com_content&view=article&id=25  initiative.
     
    "Dinghy racing":index.php?option=com_content&view=article&id=273  is staged from April to December both in the estuary and, during the summer months, at sea. Whilst the estuary often provides relatively sheltered conditions, the challenges of racing in tidal waters keeps even the most experienced sailors on their toes. New racers are encouraged through the "Get Racing":index.php?option=com_content&view=article&id=285  programme.
     
    The whole estuary and coast are also a delight to explore in a dinghy. Local "cruises in company":index.php?option=com_content&view=article&id=274  are organised on a regular basis and competent sailors are always welcome to join in.
     
    "Open Meetings":open-events.html  and National Championships are staged each year and the club has an excellent reputation amongst the racing fleets for the quality of the sailing on offer and the welcome and hospitality shown to visiting sailors.
     
    p(float-right-20). !https://files.exe-sailing-club.org/images/Dinghies2/Rob_Vince_2.jpg(Rob Vince)!Rob Vince
     
    Dinghy sailing at Exe SC is organised by the Dinghy Committee led by the Dinghy Captain Rob Vince. He is supported by committee members representing the main classes of dinghy sailed and activities undertaken.
     
    RS 400 - Les Arscott 
     RS 200 – Chris Crook 
     Comet Trio - Bob Horlock 
     RS Aero – Andy Seymour 
     Supernova – Rob Vince 
     Juniors – Amelie Andrew
     Dinghy Cruising – Nick Webber
    """;
}
