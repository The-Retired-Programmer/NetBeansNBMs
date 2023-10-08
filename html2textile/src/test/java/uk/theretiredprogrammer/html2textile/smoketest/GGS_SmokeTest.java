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

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import uk.theretiredprogrammer.html2textile.tranformtext.TransformText;
import uk.theretiredprogrammer.html2textile.transformhtml.TransformHtml;

public class GGS_SmokeTest {

    public GGS_SmokeTest() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException, TransformerException {
        String serialised;
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/transformhtml/sample_girlsgosailing.html");
        TransformText texttransformer = new TransformText(new InputStreamReader(is));
        texttransformer.rootWrap("html");
        texttransformer.replace("&nbsp;", " ");
        try ( Reader wrapped = texttransformer.transform()) {
            TransformHtml transformer = new TransformHtml(wrapped);
            transformer.transform();
            transformer.writeHtml(new FileWriter("/home/richard/GGS_SMOKE_TEST.html"));
            serialised = transformer.getSerialisedDOM();
        }
        //System.out.println(serialised);
        assertEquals(expected(), serialised);
    }

    private String expected() {
        return """
               html
                   h3 style="text-align:center;"
                       "Welcome to Girls Go Sailing"
                   p
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
                       "The team are a group of ladies who run and support the Girls Go Sailing come from varied sailing and non-sailing backgrounds, they are here to help you.  To find out more, please go to our "
                       strong
                           a href="index.php?option=com_content&view=article&id=298"
                               "GGS team page"
                       "."
                   h6
                       "About Girls Go Sailing"
                   p
                       "In 2018 we saw a great revival of our Girls Go Sailing scheme, which we have run intermittently for a few years. By making it a member lead activity, we have seen increased numbers of women participating and welcome new members at any time throughout the year."
                   p
                   p
                   h6
                       "What do we do?"
                   ul
                       li
                           "We are offering our female members sailing opportunities on cruisers, dinghies and the club Hawk, running the sessions at varying times – some weekends, some weekdays, some evenings, throughout the year."
                       li
                           "We encourage participation in shore-based theory sessions and offer knowledge and safety seminars through the winter and spring."
                       li
                           "We are also planning some social evenings throughout the year."
                   h5
                       "News"
                   p
                       "As part of the RYA Steering the Course week highlighting women in sailing, the Exe Sailing Club Girls Go Sailing group had a lovely write up - click "
                       a href="https://www.rya.org.uk/news/girls-go-sailing-at-exe-sailing-club"
                           "here"
                       " for the article."
                   h6
                       a href="assets/documents/GGS_2022/GGS_WinterSeminars2023.pdf"
                           "Winter"
                           " 2024 Seminars now published"
                   ul
                       li
                           strong
                               "Thursday 7th December"
                           " -"
                           " Christmas Dinner, meet at 6.30. Hear some experiences from the 2023 season.  Christmas dinner at 7.30pm. "
                   ul
                       li
                           strong
                               "Friday 19th January 2024"
                           ", 7pm,Topic - Staying Safe on the Water whilst sailing a dinghy or yacht.  Using a VHF radio, ‘Tide, Wind, Approach’, having a Plan B."
                   ul
                       li
                           strong
                               "Friday 2nd February 2024, "
                           "7pm, Topic - Health and Well Being on the Water and looking after each other."
                           br
                           strong
                       li
                           strong
                               "Friday 8th March 2024"
                           ", 7pm, Topic - Sailing Kit; What to wear and new developments in sailing clothing for dinghies and yachts. Opportunities to buy pre loved kit."
                           br
                           strong
                       li
                           strong
                               "Friday 12th April 2024"
                           ", 7p"
                           "m, Start of Season Meal and social event."
                   p
                       "We will also be practicing some knot tying and will adjourn to the bar after the seminars for some refreshment."
                   p
                       "Hope to see you there."
                   p
                       "If you would like more information or interested in our previous seminars please go to our "
                       strong
                           a href="index.php?option=com_content&view=article&id=267:girls-go-sailing-seminars&catid=2:uncategorised"
                               "Girls Go Sailing Seminars "
                       "page!"
                   p
                   h4
                       "Getting afloat"
                   h5 dir="auto"
                       "Cruisers"
                   p
                       "A very useful Beginners Guide to Cruising - click "
                       a href="assets/documents/GGS_2022/Beginners_Guide_to_Cruiser_Racing.pdf"
                           "here"
                       " - specific to our club written by Tink!"
                   p dir="auto"
                       strong
                           "Cruiser Training & Qualifications - "
                       "Lots of opportunities to take part in RYA cruiser courses throughout summer on Zephyr with Karen Melling - go to "
                       a href="training.html"
                           "Training"
                       " for more details."
                   p
                   h5 dir="auto"
                       "Dinghies"
                   h6 dir="auto"
                       "Dinghy Training & Qualifications"
                   p dir="auto"
                       "The Club also runs various RYA dinghy courses through the season, go to "
                       a href="training.html"
                           "Training"
                       " for more information. "
                   p
                   h5
                       "Powerboat Training & Experience"
                   p
                       "If you are interested in improving power boat skills please sign up for Power Boat Level 2 training courses run by the Club throughout the summer. Go to the "
                       a href="training.html"
                           "Training"
                       " Section of the web site for more information."
                   p
               """;
    }
}
