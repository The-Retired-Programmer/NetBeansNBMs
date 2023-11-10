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

public class ForDebugging_Test extends SmokeTest {

    public ForDebugging_Test() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException, TransformerException {
        transformation("sample - fordebugging.html","FORDEBUGGING_TEST.html","FORDEBUGGING_TEST.textile", expected);
    }

    String expected = """
AIM

_An excellent start to becoming an instructor and becoming part of the ESC instructor team of volunteers. Extremely rewarding and fun. Ideal for Splash nights and being able to assist on Level 1 courses and Stage 1 & 2 courses._

DURATION

A 2 day weekend course

REQUIRED EXPERIENCE

Sailing skills to the standard of RYA Level 2 Basic Skills/RYA Stage 4

MINIMUM AGE

Minimum age: 14yrs

COST

Cost: tbc

YOU NEED TO BRING

                      
* Sailing gear to include
** Wetsuit
** Buoyancy aid (minimum 50 Newtons)
** Footwear (Grippy shoes that can get wet. Wetsuit boots or shoes, or trainers are fine, Plimsoles, Crocs or Wellies not ok)
** Swim gear and/or rash vest for under the wetsuit
** Waterproof jacket or spray top
** Towel
** Sun protection including suncream/sun hat
** Warm hat for colder days
**(MsoListParagraphCxSpMiddle) G14 Instructor Logbook/handbook prior to the course
**(MsoListParagraphCxSpMiddle) Notepad and pen
** Waterbottle
** Packed lunch (extra snacks/drinks available when club bar is open)



h6. REGISTER YOUR INTEREST WITH THE CLUB PROFESSIONAL ("training@exe-sailing-club.org":mailto:training@exe-sailing-club.org )

FOR COURSE DATES AND TO BOOK CLICK HERE (tbc)

ADDITIONAL DETAILS

Courses held at Exe Sailing Club

FURTHER QUESTIONS

Do not hesitate to contact our Club Professional on

Email: "training@exe-sailing-club.org":mailto:training@exe-sailing-club.org 

Phone: 01395 264607

""";
}
