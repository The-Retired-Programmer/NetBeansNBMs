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
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class StyleReduction_Test {

    public StyleReduction_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/transformhtml/example_stylereduction.html");
        Reader in = new InputStreamReader(is);
        TransformHtml transformer = new TransformHtml(in);
        transformer.transform(new IndentAndReturnsRemoval());
        transformer.transform(new StyleNormalisation());
        //
        transformer.transform(new StyleReduction());
        //
        String result = transformer.getSerialisedDOM();
        //System.out.println(result);
        assertEquals(expected(), result);
    }

    private String expected() {
        return """
               html
                   p style="text-align:center;"
                       span style="font-size:18pt;"
                           strong
                               "Welcome to Girls Go Sailing"
                   p
                       span style=""
                           "The purpose of the group is to encourage women and girls to enjoy sailing activities safely within the Exe Sailing Club, provide taster sessions in yachts, the Hawk, dinghies and power boats which will lead on to progression through the RYA courses, encourage social gatherings of women and girls and to have fun on and off the water. Go to the "
                           strong
                               a href="https://scm.exe-sailing-club.org/events"
                                   "Calendar"
                           " for up to date. If you would like to get in contact with the GGS group please contact the Club Professional for more information "
                           strong
                               a href="mailto:to:GGS@exe-sailing-club.org"
                                   "ggs@exe-sailing-club.org"
                           "."
                   p
                       span style=""
                           "The team are a group of ladies who run and support the Girls Go Sailing come from varied sailing and non-sailing backgrounds, they are here to help you.  To find out more, please go to our "
                           strong
                               a href="index.php?option=com_content&view=article&id=298"
                                   "GGS team page"
                           "."
                   div
                       span style=""
                           strong
                               "About Girls Go Sailing"
                   div
                       span style=""
                           "In 2018 we saw a great revival of our Girls Go Sailing scheme, which we have run intermittently for a few years. By making it a member lead activity, we have seen increased numbers of women participating and welcome new members at any time throughout the year."
                   p
                       span style=""
                           span style=""
                               span style=""
                                   strong
                                       "Thursday 7th December"
                               " -"
                           " Christmas Dinner, meet at 6.30. Hear some experiences from the 2023 season.  Christmas dinner at 7.30pm. "
                   p
                       span style=""
                   p
                       span style="text-decoration:underline;"
                           strong
                               span style="font-size:14pt;"
                                   "Getting afloat"
                   p dir="auto"
                       span style="text-decoration:underline;"
                           strong
                               "Cruisers"
                   p
                       span style=""
                           "A very useful Beginners Guide to Cruising - click "
                           a href="assets/documents/GGS_2022/Beginners_Guide_to_Cruiser_Racing.pdf"
                               "here"
                           " - specific to our club written by Tink!"
                   p
                       span style="text-decoration:underline;"
                           strong
                               "Powerboat Training & Experience"
               """;
    }
}