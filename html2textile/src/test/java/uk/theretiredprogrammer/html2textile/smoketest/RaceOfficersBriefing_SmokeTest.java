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
import uk.theretiredprogrammer.html2textile.transformhtml.SerialiseDom;
import uk.theretiredprogrammer.html2textile.transformhtml.TransformHtml;

public class RaceOfficersBriefing_SmokeTest {

    public RaceOfficersBriefing_SmokeTest() {
    }

    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException, TransformerException {
        String serialised;
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/html2textile/transformhtml/sample_race-officers-briefing.html");
        TransformText texttransformer = new TransformText(new InputStreamReader(is));
        texttransformer.rootWrap("html");
        texttransformer.replace("&nbsp;", " ");
        try ( Reader wrapped = texttransformer.transform()) {
            TransformHtml transformer = new TransformHtml(wrapped);
            transformer.transform();
            transformer.writeHtml(new FileWriter("/home/richard/RaceOfficersBriefing_SMOKE_TEST.html"));
            serialised = SerialiseDom.serialise(transformer.getRoot());
        }
        //System.out.println(serialised);
        assertEquals(expected(), serialised);
    }

    private String expected() {
        return """
               html
                   h3
                       img alt="Race Officers Brief" src="assets/images/Dinghies2/Race_Officers_Brief_2.jpg" style="margin:020px10px0px;float:left;width:50%;"
                       "Preparation"
                   h6
                       "Before the day of the race"
                   p
                       "Check the "
                       a href="https://www.windguru.cz/712"
                           "forecast"
                       " and "
                       a href="https://easytide.admiralty.co.uk/?PortID=0027"
                           "tide times"
                       "."
                   p
                       "Check the "
                       a href="https://scm.exe-sailing-club.org/events?types%5B%5D=4981"
                           "Dinghy Programme"
                       " for the start time, location (sea or estuary), start location (Start Box or rib) and race series."
                   p
                       "Read and understand the Sailing Instructions and check Appendix for Format, Fleets, Starting Order, Warning Signals, Target Time and Time Limit."
                   p
                       "Check the "
                       a href="https://scm.exe-sailing-club.org/duties"
                           "Race Team Roster"
                       " and make sure you know who is in your race management team, contact them to ensure they know when and where to turn up!"
                   p
                       "Check with the Club Professional which ribs you are allocated and where the racing marks (anchors if going to sea) and flag poles are located. Is there training activity concurrent with racing? If so, who is the Senior Instructor (in case of equipment issues)."
                   h6
                       "On the day of the race"
                   p
                       "Get to the club early! (at least 1 ¼ hours before race start in estuary, 1 ½ hours if at sea)"
                   p
                       "Consider if racing is safe. You can postpone the start if confident conditions will improve. If scheduled at sea, consider the advice at "
                       a href="dinghies/race-team-information/racing-at-sea.html"
                           "Racing at Sea"
                       ".  If it is not safe at sea, is it safe in the estuary? Cancel if conditions are unsafe and clearly inform sailors intending to race. This responsibility continues once sailors are afloat. You should abandon racing before conditions become unsafe, signalling the abandonment to competitors and ensuring that they return safely to shore."
                   p
                       "Ensure that you have a watch that works!"
                   h6
                       "Make a Plan"
                   p style="padding-left:30px;"
                       u
                           "Committee Vessel Start"
                       " - Decide where the start location will be. (See Appendix 1 - Committee Boat Estuary Start Notes and discuss with experienced sailors as necessary)."
                   p style="padding-left:30px;"
                       u
                           "Race Box Start"
                       " - Select the course from the "
                       a href="assets/documents/Dinghies/2023/dinghy_course_card_2023_v3.pdf"
                           "course card"
                       " and the start line position after reading Appendix 2 and discussing with experience sailors as necessary."
                   p
                       "Put the declaration form out and write the following on the top of the first page:"
                   p style="padding-left:30px;"
                       "Date"
                       br
                       "Series"
                       br
                       " Race Officers’ name "
                       br
                       " Start Time"
                       br
                       "Forecast wind strength and direction"
                       br
                       "Time and height of high and low water "
                       br
                       "Anticipated start location (Gut or Warren if Race Box start)"
                       br
                       "Anticipated course type (or course number if Race Box start)"
                       br
                       "Fleet starting order"
                       br
                       "Number of Races"
                   p
                       "Decide which route you want the sailors to take to the start location and back to the club after racing and communicate this to the competitors by a diagram or words on the blackboard above the sign on sheet."
                   h6
                       "Enact the Plan"
                   p
                       "Gather the race team and brief them on your plan. Allocate tasks, check radios are on channel 08. Gather equipment and head for the marina/race box."
                   p
                       u
                           "Committee Vessel Start"
                       " -"
                   p style="padding-left:30px;"
                       "Collect the rib course board and tiles box, marks (mark anchors if at sea), starting horn, VHF radios and Race Officer’s bag. Make sure you have the flags, recording forms, pencils, chalk etc in the RO’s bag."
                   p style="padding-left:30px;"
                       "Collect the appropriate fuel containers for the ribs allocated."
                   p style="padding-left:30px;"
                       "Collect the team, proceed to the marina, prepare ribs. Allocate safety boat to watch for boats launching. Once last boat has launched they can join you at the race area or follow last boat as appropriate."
                   p style="padding-left:30px;"
                       "Layout the Course. Try to set a good beat to the windward mark. Lay the other marks, or use channel marks, to obtain desired course. Try to picture what the sailors will do so that, where possible, boats do not cross on entering and leaving the marks."
                   p style="padding-left:30px;"
                       "The Committee Vessel and marks should be tied to unoccupied moorings.  You must move promptly if the owner returns! The 3 Visitor moorings (marked with a V) near buoy 15 should not be used as turning marks or to attach the committee vessel.  Do not anchor the rib in the estuary, the bottom is littered with old mooring chains etc ready to foul any anchor."
                   p style="padding-left:30px;"
                       "Try to set the start line at 90° to the apparent wind, it should be about 1.2 times the length of all the boats in the race (so 10 Trios (5 m) and 5 Aeros (4 m) equates to 70 m x 1.2 = 80 m). Don’t set the line across the channel. Ask an experienced sailor to confirm the line is satisfactory."
                   p style="padding-left:30px;"
                       "Use the Course Board to define the course (see Appendix 2 - suggested use of course board). Confirm with an experienced sailor that the course makes sense and is likely to be the correct length. You should aim for 3 laps to meet the target time for the race."
                   p
                       u
                           "Race Box Start"
                       " -"
                   p style="padding-left:30px;"
                       "Prepare to leave the club for the short walk at least 30 minutes before the first warning signal."
                   p style="padding-left:30px;"
                       "Collect start box key. Take spare recording sheets, horn, radio, pens and two watches (there is a clock and a set of binoculars in the hut but not a stopwatch )."
                   p style="padding-left:30px;"
                       "Once inside the hut agree duties with the ARO and Recorder."
                   p style="padding-left:30px;"
                       "Check the in-built horn is working and open the flag role."
                   p style="padding-left:30px;"
                       "Flags should be attached to the halyard’s ready to be flown. Orange flag is closest to the centre pole on the right-hand side looking out to sea followed by class/fleet and preparatory flags. Individual recall (Code X), general recall (1"
                       sup
                           "st"
                       " Substitute) and postponement (AP) flags to be hung on the left-hand side."
                   p style="padding-left:30px;"
                       "Radio safety boat to position dan buoy indicating the pin end of the start line so that it is visible and near the end of the Warren for courses 2 to 5 and in the gut for course 1."
                   p style="padding-left:30px;"
                       "In advance of the first warning signal, giving competitors time to come close to shore to read course details, raise the Orange flag and indicate course by hanging the course number tile from the banister below the flag poles. (Note: if you have changed the course number from that indicated on the declaration sheet, please fly flag L and let the safety boats know the change of course so that they can bring it to the competitors attention!)"
                   h3
                       "Running the Racing"
                   p
                       "Raise the orange flag on the committee boat (behind the seat, not at the back of the RIB)/race box."
                   p
                       "Prepare the other flags you will need."
                   p
                       "Ask your assistant to note down the sail numbers of the boats that have come to the start area. Make sure that the safety boats know the total number of boats taking part. Position the safety boat(s) where they are likely to be needed."
                   p
                       "Aim to start the first race on time. Only delay if more than half the fleet are late to the start area. Ask your assistant to note down the actual start time of each race."
                   p
                       "Run the start sequence (RRS 26). Call the line, if boats are over and you can identify them raise the X flag with one sound signal. If there are too many over the line for you to identify then signal a general recall 1st substitute and 2 sound signals."
                   p
                       "Record the time each boat crosses the line on each lap.  "
                       strong
                           "If using Course 1 this includes the first time the boats pass No 11 buoy after going out to No 10"
                       "."
                   p
                       "Shorten the course if needed to meet the target time for the race. (S flag and 2 sound signals as the leading boat rounds the last mark)."
                   p
                       "Once the leading boat has finished, finish all the others as they cross the line even if they haven’t sailed the required number of laps (their result will be worked out on average lap times)."
                   p
                       "Record the finish time for each boat, give a sound signal for each finisher (not strictly required)."
                   p
                       "Alter the course/line as necessary. Run subsequent races as scheduled."
                   h6
                       "After the Race"
                   p
                       "Ask the safety boat to pick up course marks after last boat has passed them."
                   p
                       "Escort last boats back to shore, do not leave ribs until every boat is back on shore."
                   p
                       "Refuel ribs as necessary, leave ribs as found, engine up, electrics off, cover on."
                   p
                       u
                           "Race Box"
                       " Once race is over pack away flags, disconnect the battery charger in the cupboard (it would have been left on trickle charge earlier in the day), lock the hut and return to the club."
                   p
                       "Return to club, refill fuel cans, sign off ribs, record engine hours and any defects."
                   p
                       "Collect sign on/off sheet, check competitors have signed off."
                   p
                       "Take photo of sign on/off sheet and timings record sheet for each race and send photos to results secretary. Place originals in the black ‘club post box’ in entrance hall."
                   p
                       "Dinghy Committee Feb 23"
                   h3
                       "Appendices"
                   h6 style="text-align:center;"
                       "1.Committee Boat Estuary Start Notes"
                   h6
                       "Tides"
                   p
                       "Courses can only be set outside the main channel over the Banks with a tide height of over 3.6m and then only for an hour either side of high water."
                   p
                       "Never lay a course involving a crossing of Bull Hill (the Bank to the north of No. 13 Buoy), eg, if setting a course from No. 19 Buoy downstream to 13, use No. 15 Buoy and No 17 Buoy as spacer marks."
                   p
                       "Up to 2 hours before HW the tide flows strongly in the main channel. There is less tide and more counter-eddies (flow of tide contrary to the direction of the main flow) in the 13-15 and 19 Buoy areas which will allow racing even in lighter airs."
                   p
                       "One and a half hours before HW a strong counter-eddy develops from No. 14 Buoy extending to No. 12 Buoy. This creates an interesting sailing area."
                   h6
                       "Moorings"
                   p
                       "Avoid laying courses through Shelly in strong tides and winds. It is probably worth avoiding this area in all but lighter winds and tides."
                   p
                       "Wind direction / Starting areas"
                   p
                       "Southerly/South-westerly starting around 19 Buoy will provide many options."
                   p
                       "North-westerly and Northerly. The 13-15 Buoy area will provide the same."
                   h6 style="text-align:center;"
                       "2.  Race Box Starts - Suggestions for Start Line Positions and  Course Selection"
                   p
                       img alt="Race Box Start Line" src="assets/images/Dinghies2/raceboxstartlinev2.jpg" style="margin:10px0px10px0px;float:center;width:100%;"
                   table style="border:0;margin-left:auto;margin-right:auto;"
                       tbody
                           tr style="background-color:#adadad;"
                               td style="border:mediumsolid#000000;"
                                   strong
                                       "Wind Direction"
                               td style="text-align:center;"
                                   strong
                                       "Tide Direction"
                               td style="text-align:center;"
                                   strong
                                       "Preferred Course(s)"
                               td style="text-align:center;"
                                   strong
                                       "Preferred Start Line"
                           tr
                               td rowspan="2" style="border:mediumsolid#000000;"
                                   "NNW < "
                                   strong
                                       "N"
                                   " > NNE "
                               td style="background-color:#dea55e;text-align:center;"
                                   "Flood"
                               td style="text-align:center;"
                                   "4"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   "W"
                           tr
                               td style="background-color:#dea55e;text-align:center;"
                                   "Ebb"
                               td style="text-align:center;"
                                   "3"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   "W"
                           tr
                               td rowspan="2" style="border:mediumsolid#000000;"
                                   "NNE < "
                                   strong
                                       "NE"
                                   " > ENE  "
                               td style="background-color:#dea55e;text-align:center;"
                                   " Flood"
                               td style="text-align:center;"
                                   "1 or 4 "
                               td style="background-color:#e9ae6d;text-align:center;"
                                   " W"
                           tr
                               td style="background-color:#dea55e;text-align:center;"
                                   "Ebb "
                               td style="text-align:center;"
                                   " 3"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   " W"
                           tr
                               td rowspan="2" style="border:mediumsolid#000000;"
                                   "ENE < "
                                   strong
                                       "E"
                                   " > ESE  "
                               td style="background-color:#dea55e;text-align:center;"
                                   "Flood "
                               td style="text-align:center;"
                                   " 1 or 4"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   "W "
                           tr
                               td style="background-color:#dea55e;text-align:center;"
                                   " Ebb"
                               td style="text-align:center;"
                                   " 3"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   "W "
                           tr
                               td rowspan="2" style="border:mediumsolid#000000;"
                                   "ESE < "
                                   strong
                                       "SE"
                                   " > SSE  "
                               td style="background-color:#dea55e;text-align:center;"
                                   "Flood "
                               td style="text-align:center;"
                                   " 1"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   " G"
                           tr
                               td style="background-color:#dea55e;text-align:center;"
                                   " Ebb"
                               td style="text-align:center;"
                                   " 3"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   "G or W "
                           tr
                               td rowspan="2" style="border:mediumsolid#000000;"
                                   "SSE < "
                                   strong
                                       "S"
                                   " > SSW  "
                               td style="background-color:#dea55e;text-align:center;"
                                   "Flood "
                               td style="text-align:center;"
                                   "1, 2 or 4 "
                               td style="background-color:#e9ae6d;text-align:center;"
                                   "G "
                           tr
                               td style="background-color:#dea55e;text-align:center;"
                                   "Ebb "
                               td style="text-align:center;"
                                   " 3"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   " G"
                           tr
                               td rowspan="2" style="border:mediumsolid#000000;"
                                   "SSW < "
                                   strong
                                       "SW"
                                   " > WSW  "
                               td style="background-color:#dea55e;text-align:center;"
                                   " Flood"
                               td style="text-align:center;"
                                   "2 or 4 "
                               td style="background-color:#e9ae6d;text-align:center;"
                                   "W "
                           tr
                               td style="background-color:#dea55e;text-align:center;"
                                   " Ebb"
                               td style="text-align:center;"
                                   " 3"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   " W"
                           tr
                               td rowspan="2" style="border:mediumsolid#000000;"
                                   "WSW < "
                                   strong
                                       "W"
                                   " > WNW  "
                               td style="background-color:#dea55e;text-align:center;"
                                   "Flood "
                               td style="text-align:center;"
                                   "4 "
                               td style="background-color:#e9ae6d;text-align:center;"
                                   " W"
                           tr
                               td style="background-color:#dea55e;text-align:center;"
                                   " Ebb"
                               td style="text-align:center;"
                                   " 3"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   " W"
                           tr
                               td rowspan="2" style="border:mediumsolid#000000;"
                                   "WNW < "
                                   strong
                                       "NW"
                                   " > NNW  "
                               td style="background-color:#dea55e;text-align:center;"
                                   "Flood "
                               td style="text-align:center;"
                                   " 2 or 4"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   " W"
                           tr
                               td style="background-color:#dea55e;text-align:center;"
                                   " Ebb"
                               td style="text-align:center;"
                                   " 3"
                               td style="background-color:#e9ae6d;text-align:center;"
                                   " W"
                   p
                       " Notes:"
                   p style="padding-left:30px;"
                       "1.  There are many possible variations of wind and tide which might make a different course or start line position preferable - These are only suggestions!"
                   p style="padding-left:30px;"
                       "2.  Sailing out to Channel mark 10 on an ebbing tide and potentially dying breeze is not usually safe hence the only suggested course on an ebb tide is course 3."
                   p style="padding-left:30px;"
                       "3.  Course 5 is very long and is only viable in near perfect conditions.  It does not therefore figure in the suggestions but if the tide is slack and the wind is good and unlikely to die please give it a go!"
                   p style="padding-left:30px;"
                       "4.  If using Course 1 remember to record the times boats pass No 11 buoy after going out to No 10 for the first time."
                   h6 style="text-align:center;"
                       "3. Suggested use of Course Board"
                   h6
                       "Contents"
                   table style="border-collapse:collapse;width:100%;"
                       tbody
                           tr
                               td style="border:none;vertical-align:middle;width:27%;"
                                   p style="margin:6pt0px6pt0px;text-align:center;"
                                       img alt="image0002.jpg" src="assets/images/Dinghies2/image0002.jpg" style="margin:020px10px0px;float:left;width:100%;"
                               td colspan="2" style="border:none;vertical-align:middle;width:27%;"
                                   p style="margin:6pt0px6pt0px;text-align:center;"
                                       img alt="image0003.jpg" src="assets/images/Dinghies2/image0003.jpg" style="margin:020px10px0px;float:left;width:100%;"
                               td style="border:none;vertical-align:middle;width:25%;"
                                   p style="margin:6pt0px6pt0px;text-align:center;"
                                       img alt="image0004.jpg" src="assets/images/Dinghies2/image0004.jpg" style="margin:020px10px0px;float:left;width:100%;"
                               td style="border:none;vertical-align:middle;width:21%;"
                                   p style="margin:6pt0px6pt0px;text-align:center;"
                                       img alt="image0005.jpg" src="assets/images/Dinghies2/image0005.jpg" style="margin:020px10px0px;float:left;width:100%;"
                           tr
                               td style="border:none;vertical-align:middle;width:27%;"
                                   p
                                       "Course Board Front"
                               td colspan="2" style="border:none;vertical-align:middle;width:27%;"
                                   p
                                       "Course Board Back"
                               td style="border:none;vertical-align:middle;width:25%;"
                                   p
                                       "Tiles Box"
                               td style="border:none;vertical-align:middle;width:21%;"
                                   p
                                       "Tiles"
                           tr
                               td colspan="5" style="border:none;vertical-align:middle;width:100%;"
                                   p
                                       "Tiles are either:"
                                   p
                                       "White (Lap numbers (1 to 4), course shapes (Triangle or Sausage) and Start Line (S/L) or Finish Line (F/L))"
                                   p
                                       "Red one side, Green the other (to indicate marks of the course (Y is for a yellow laid mark, H is for Hinton). (The green side has a black line across the middle so that red/green colour blind people can distinguish the colours)"
                           tr
                               td colspan="2" style="border:none;vertical-align:middle;width:51%;"
                                   h6
                                       "Set Up"
                                   p
                                       "Place the pole of the board in one of the tubes used to support the signal flags on the "
                                       strong
                                           "side of the rib away from the start line"
                                       ". Use the bungy cord to stop the board rotating in the wind and tie the board back to the A frame on Grey RIB as necessary using the ropes at the top of the board. Use the tiles to define the course. The back of the course board has been painted as a chalkboard should you need to use this facility. Please put everything away carefully at the end of racing!"
                               td colspan="3" rowspan="2" style="border:none;vertical-align:middle;width:50%;"
                                   p style="margin:6pt0px6pt0px;text-align:center;"
                                       img alt="image0006.jpg" src="assets/images/Dinghies2/image0006.jpg" style="margin:020px10px0px;float:left;width:100%;"
                           tr
                               td colspan="2" style="border:none;vertical-align:middle;width:51%;"
                                   p
                                       strong
                                           "Example"
                                       " The example on the right would be a triangular course beginning with the start line (S/L), Y to Port (Y on a red background), 17 to Port (17 on a red background), Y to Port (Y on a red background), and back through the finish line. The start line and finish line is between the RIB and a Yellow mark and the race consists of 3 laps."
                   """;
    }
}
