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

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException, TransformerException {
        transformation("sample_dinghies.html","DINGHIES_SMOKE_TEST.html","DINGHIES_SMOKE_TEST.textile", expected(), false);
    }

    private String expected() {
        return """
            html
                p
                    img alt="Dinghies" src="assets/images/Dinghies2/Dinghies_2.jpg" style="margin:020px10px0px;float:left;width:50%;"
                p
                    "Dinghies are at the heart of sailing at Exe SC.The wide expanse of the Exe estuary and immediate access to the sea provide excellent opportunities for our sport. Whether you are an experienced dinghy sailor or a complete novice, young or old, Exe SC provides the facilities, training and coaching needed for you to fully enjoy the racing and cruising opportunities available."
                p
                    "We actively encourage junior sailors. Starting at a young age with splash nights, progressing through learning to sail at our RYA accredited "
                    a href="training/training-link.html"
                        "Training Centre"
                    " and moving on through race coaching, our "
                    a href="juniors.html"
                        "Junior section"
                    " is a vibrant part of the club."
                p
                    "The Training Centre equally runs courses for adults, from novice to experienced, providing opportunities to continue sailing through the "
                    a href="training/boat-hire.html"
                        "use of club boats"
                    " and regular "
                    a href="index.php?option=com_content&view=article&id=294"
                        "improvers"
                    " sessions."
                p
                    "Exe SC actively encourages the participation of women in all forms of sailing, through the "
                    a href="index.php?option=com_content&view=article&id=25"
                        "Girls Go Sailing"
                    " initiative."
                p
                    a href="index.php?option=com_content&view=article&id=273"
                        "Dinghy racing"
                    " is staged from April to December both in the estuary and, during the summer months, at sea. Whilst the estuary often provides relatively sheltered conditions, the challenges of racing in tidal waters keeps even the most experienced sailors on their toes. New racers are encouraged through the "
                    a href="index.php?option=com_content&view=article&id=285"
                        "Get Racing"
                    " programme."
                p
                    "The whole estuary and coast are also a delight to explore in a dinghy. Local "
                    a href="index.php?option=com_content&view=article&id=274"
                        "cruises in company"
                    " are organised on a regular basis and competent sailors are always welcome to join in."
                p
                    a href="open-events.html"
                        "Open Meetings"
                    " and National Championships are staged each year and the club has an excellent reputation amongst the racing fleets for the quality of the sailing on offer and the welcome and hospitality shown to visiting sailors."
                p style="margin:50px00100px;padding:0;float:right;width:20%;text-align:center;line-height:12pt;"
                    img alt="Rob Vince" src="assets/images/Dinghies2/Rob_Vince_2.jpg" style="width:100%;margin:0;padding:0;"
                    "Rob Vince"
                p
                    "Dinghy sailing at Exe SC is organised by the Dinghy Committee led by the Dinghy Captain Rob Vince. He is supported by committee members representing the main classes of dinghy sailed and activities undertaken."
                p
                    "RS 400 - Les Arscott "
                    br
                    " RS 200 – Chris Crook "
                    br
                    " Comet Trio - Bob Horlock "
                    br
                    " RS Aero – Andy Seymour "
                    br
                    " Supernova – Rob Vince "
                    br
                    " Juniors – Amelie Andrew"
                    br
                    " Dinghy Cruising – Nick Webber"                                                                                                                                                                                         
            """;
    }
}
