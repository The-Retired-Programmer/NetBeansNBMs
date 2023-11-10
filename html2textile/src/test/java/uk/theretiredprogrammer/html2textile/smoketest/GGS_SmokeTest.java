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

public class GGS_SmokeTest extends SmokeTest {

    public GGS_SmokeTest() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException, TransformerException {
        transformation("sample_girlsgosailing.html","GGS_SMOKE_TEST.html","GGS_SMOKE_TEST.textile", expected);
    }
    
    String expected = """
h3=. Welcome to Girls Go Sailing

The purpose of the group is to encourage women and girls to enjoy sailing activities safely within the Exe Sailing Club, provide taster sessions in yachts, the Hawk, dinghies and power boats which will lead on to progression through the RYA courses, encourage social gatherings of women and girls and to have fun on and off the water. Go to the *"Calendar":https://scm.exe-sailing-club.org/events* for up to date. If you would like to get in contact with the GGS group please contact the Club Professional for more information *"ggs@exe-sailing-club.org":mailto:to:GGS@exe-sailing-club.org*.

h6. "GGS team page":index.php?option=com_content&view=article&id=298 

h6. About Girls Go Sailing

In 2018 we saw a great revival of our Girls Go Sailing scheme, which we have run intermittently for a few years. By making it a member lead activity, we have seen increased numbers of women participating and welcome new members at any time throughout the year.

h6. What do we do?

                      
* We are offering our female members sailing opportunities on cruisers, dinghies and the club Hawk, running the sessions at varying times – some weekends, some weekdays, some evenings, throughout the year.
* We encourage participation in shore-based theory sessions and offer knowledge and safety seminars through the winter and spring.
* We are also planning some social evenings throughout the year.

h5. News

As part of the RYA Steering the Course week highlighting women in sailing, the Exe Sailing Club Girls Go Sailing group had a lovely write up - click "here":https://www.rya.org.uk/news/girls-go-sailing-at-exe-sailing-club  for the article.

h6. "Winter 2024 Seminars now published":https://files.exe-sailing-club.org/documents/GGS_2022/GGS_WinterSeminars2023.pdf 

                      
* *Thursday 7th December* - Christmas Dinner, meet at 6.30. Hear some experiences from the 2023 season.  Christmas dinner at 7.30pm.
* *Friday 19th January 2024*, 7pm,Topic - Staying Safe on the Water whilst sailing a dinghy or yacht.  Using a VHF radio, ‘Tide, Wind, Approach’, having a Plan B.
* *Friday 2nd February 2024,* 7pm, Topic - Health and Well Being on the Water and looking after each other.
* *Friday 8th March 2024*, 7pm, Topic - Sailing Kit; What to wear and new developments in sailing clothing for dinghies and yachts. Opportunities to buy pre loved kit.
* *Friday 12th April 2024*, 7pm, Start of Season Meal and social event.

We will also be practicing some knot tying and will adjourn to the bar after the seminars for some refreshment.

Hope to see you there.

h6. "Girls Go Sailing Seminars":index.php?option=com_content&view=article&id=267:girls-go-sailing-seminars&catid=2:uncategorised 

h4. Getting afloat

h5. Cruisers

A very useful Beginners Guide to Cruising - click "here":https://files.exe-sailing-club.org/documents/GGS_2022/Beginners_Guide_to_Cruiser_Racing.pdf  - specific to our club written by Tink!

*Cruiser Training & Qualifications -* Lots of opportunities to take part in RYA cruiser courses throughout summer on Zephyr with Karen Melling - go to "Training":training.html  for more details.

h5. Dinghies

h6. Dinghy Training & Qualifications

The Club also runs various RYA dinghy courses through the season, go to "Training":training.html  for more information.

h5. Powerboat Training & Experience

If you are interested in improving power boat skills please sign up for Power Boat Level 2 training courses run by the Club throughout the summer. Go to the "Training":training.html  Section of the web site for more information.

""";
}
