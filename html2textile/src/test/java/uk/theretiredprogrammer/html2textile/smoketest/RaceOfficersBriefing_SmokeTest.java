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

public class RaceOfficersBriefing_SmokeTest extends SmokeTest {

    public RaceOfficersBriefing_SmokeTest() {
    }

//    @Test
    public void testtransformation() throws IOException, ParserConfigurationException, SAXException, URISyntaxException, TransformerException {
        transformation("sample_race-officers-briefing.html", "RaceOfficersBriefing_SMOKE_TEST.html", "RaceOfficersBriefing_SMOKE_TEST.textile", expected);
    }
    
    String expected = """
h3. !(float-left-50)https://files.exe-sailing-club.org/images/Dinghies2/Race_Officers_Brief_2.jpg(Race Officers Brief)!Preparation

*Before the day of the race*

Check the "forecast":https://www.windguru.cz/712  and "tide times":https://easytide.admiralty.co.uk/?PortID=0027 .

Check the "Dinghy Programme":https://scm.exe-sailing-club.org/events?types%5B%5D=4981  for the start time, location (sea or estuary), start location (Start Box or rib) and race series.

Read and understand the Sailing Instructions and check Appendix for Format, Fleets, Starting Order, Warning Signals, Target Time and Time Limit.

Check the "Race Team Roster":https://scm.exe-sailing-club.org/duties  and make sure you know who is in your race management team, contact them to ensure they know when and where to turn up!

Check with the Club Professional which ribs you are allocated and where the racing marks (anchors if going to sea) and flag poles are located. Is there training activity concurrent with racing? If so, who is the Senior Instructor (in case of equipment issues).

*On the day of the race*

Get to the club early! (at least 1 ¼ hours before race start in estuary, 1 ½ hours if at sea)

Consider if racing is safe. You can postpone the start if confident conditions will improve. If scheduled at sea, consider the advice at "Racing at Sea":dinghies/race-team-information/racing-at-sea.html .  If it is not safe at sea, is it safe in the estuary? Cancel if conditions are unsafe and clearly inform sailors intending to race. This responsibility continues once sailors are afloat. You should abandon racing before conditions become unsafe, signalling the abandonment to competitors and ensuring that they return safely to shore.

Ensure that you have a watch that works!

*Make a Plan*

p((. _Committee Vessel Start_ - Decide where the start location will be. (See Appendix 1 - Committee Boat Estuary Start Notes and discuss with experienced sailors as necessary).

p((. _Race Box Start_ - Select the course from the "course card":https://files.exe-sailing-club.org/documents/Dinghies/2023/dinghy_course_card_2023_v3.pdf  and the start line position after reading Appendix 2 and discussing with experience sailors as necessary.

Put the declaration form out and write the following on the top of the first page:

p((. Date
Series
 Race Officers’ name 
 Start Time
Forecast wind strength and direction
Time and height of high and low water 
Anticipated start location (Gut or Warren if Race Box start)
Anticipated course type (or course number if Race Box start)
Fleet starting order
Number of Races

Decide which route you want the sailors to take to the start location and back to the club after racing and communicate this to the competitors by a diagram or words on the blackboard above the sign on sheet.

*Enact the Plan*

Gather the race team and brief them on your plan. Allocate tasks, check radios are on channel 08. Gather equipment and head for the marina/race box.

_Committee Vessel Start_ -

p((. Collect the rib course board and tiles box, marks (mark anchors if at sea), starting horn, VHF radios and Race Officer’s bag. Make sure you have the flags, recording forms, pencils, chalk etc in the RO’s bag.

p((. Collect the appropriate fuel containers for the ribs allocated.

p((. Collect the team, proceed to the marina, prepare ribs. Allocate safety boat to watch for boats launching. Once last boat has launched they can join you at the race area or follow last boat as appropriate.

p((. Layout the Course. Try to set a good beat to the windward mark. Lay the other marks, or use channel marks, to obtain desired course. Try to picture what the sailors will do so that, where possible, boats do not cross on entering and leaving the marks.

p((. The Committee Vessel and marks should be tied to unoccupied moorings.  You must move promptly if the owner returns! The 3 Visitor moorings (marked with a V) near buoy 15 should not be used as turning marks or to attach the committee vessel.  Do not anchor the rib in the estuary, the bottom is littered with old mooring chains etc ready to foul any anchor.

p((. Try to set the start line at 90° to the apparent wind, it should be about 1.2 times the length of all the boats in the race (so 10 Trios (5 m) and 5 Aeros (4 m) equates to 70 m x 1.2 = 80 m). Don’t set the line across the channel. Ask an experienced sailor to confirm the line is satisfactory.

p((. Use the Course Board to define the course (see Appendix 2 - suggested use of course board). Confirm with an experienced sailor that the course makes sense and is likely to be the correct length. You should aim for 3 laps to meet the target time for the race.

_Race Box Start_ -

p((. Prepare to leave the club for the short walk at least 30 minutes before the first warning signal.

p((. Collect start box key. Take spare recording sheets, horn, radio, pens and two watches (there is a clock and a set of binoculars in the hut but not a stopwatch ).

p((. Once inside the hut agree duties with the ARO and Recorder.

p((. Check the in-built horn is working and open the flag role.

p((. Flags should be attached to the halyard’s ready to be flown. Orange flag is closest to the centre pole on the right-hand side looking out to sea followed by class/fleet and preparatory flags. Individual recall (Code X), general recall (1^st^ Substitute) and postponement (AP) flags to be hung on the left-hand side.

p((. Radio safety boat to position dan buoy indicating the pin end of the start line so that it is visible and near the end of the Warren for courses 2 to 5 and in the gut for course 1.

p((. In advance of the first warning signal, giving competitors time to come close to shore to read course details, raise the Orange flag and indicate course by hanging the course number tile from the banister below the flag poles. (Note: if you have changed the course number from that indicated on the declaration sheet, please fly flag L and let the safety boats know the change of course so that they can bring it to the competitors attention!)

h3. Running the Racing

Raise the orange flag on the committee boat (behind the seat, not at the back of the RIB)/race box.

Prepare the other flags you will need.

Ask your assistant to note down the sail numbers of the boats that have come to the start area. Make sure that the safety boats know the total number of boats taking part. Position the safety boat(s) where they are likely to be needed.

Aim to start the first race on time. Only delay if more than half the fleet are late to the start area. Ask your assistant to note down the actual start time of each race.

Run the start sequence (RRS 26). Call the line, if boats are over and you can identify them raise the X flag with one sound signal. If there are too many over the line for you to identify then signal a general recall 1st substitute and 2 sound signals.

Record the time each boat crosses the line on each lap.  *If using Course 1 this includes the first time the boats pass No 11 buoy after going out to No 10*.

Shorten the course if needed to meet the target time for the race. (S flag and 2 sound signals as the leading boat rounds the last mark).

Once the leading boat has finished, finish all the others as they cross the line even if they haven’t sailed the required number of laps (their result will be worked out on average lap times).

Record the finish time for each boat, give a sound signal for each finisher (not strictly required).

Alter the course/line as necessary. Run subsequent races as scheduled.

*After the Race*

Ask the safety boat to pick up course marks after last boat has passed them.

Escort last boats back to shore, do not leave ribs until every boat is back on shore.

Refuel ribs as necessary, leave ribs as found, engine up, electrics off, cover on.

_Race Box_ Once race is over pack away flags, disconnect the battery charger in the cupboard (it would have been left on trickle charge earlier in the day), lock the hut and return to the club.

Return to club, refill fuel cans, sign off ribs, record engine hours and any defects.

Collect sign on/off sheet, check competitors have signed off.

Take photo of sign on/off sheet and timings record sheet for each race and send photos to results secretary. Place originals in the black ‘club post box’ in entrance hall.

Dinghy Committee Feb 23

h3. Appendices

p=. *1.Committee Boat Estuary Start Notes*

*Tides*

Courses can only be set outside the main channel over the Banks with a tide height of over 3.6m and then only for an hour either side of high water.

Never lay a course involving a crossing of Bull Hill (the Bank to the north of No. 13 Buoy), eg, if setting a course from No. 19 Buoy downstream to 13, use No. 15 Buoy and No 17 Buoy as spacer marks.

Up to 2 hours before HW the tide flows strongly in the main channel. There is less tide and more counter-eddies (flow of tide contrary to the direction of the main flow) in the 13-15 and 19 Buoy areas which will allow racing even in lighter airs.

One and a half hours before HW a strong counter-eddy develops from No. 14 Buoy extending to No. 12 Buoy. This creates an interesting sailing area.

*Moorings*

Avoid laying courses through Shelly in strong tides and winds. It is probably worth avoiding this area in all but lighter winds and tides.

Wind direction / Starting areas

Southerly/South-westerly starting around 19 Buoy will provide many options.

North-westerly and Northerly. The 13-15 Buoy area will provide the same.

p=. *2.  Race Box Starts - Suggestions for Start Line Positions and  Course Selection*

!{margin:10px0px10px0px;float:center;width:100%;}https://files.exe-sailing-club.org/images/Dinghies2/raceboxstartlinev2.jpg(Race Box Start Line)!

table{border:0;margin-left:auto;margin-right:auto;}. 
|:{width:25%;}. |{border:mediumsolid#000000;}. |{text-align:center;}. |{text-align:center;}. |{text-align:center;}. |
|^. 
{background-color:#adadad;}. |_. *Wind Direction*|_. *Tide Direction*|_. *Preferred Course(s)*|_. *Preferred Start Line*|
|-. 
|/2. NNW < *N* > NNE |{background-color:#dea55e;}. Flood|4|{background-color:#e9ae6d;}. W|
|{background-color:#dea55e;}. Ebb|3|{background-color:#e9ae6d;}. W|
|/2. NNE < *NE* > ENE  |{background-color:#dea55e;}.  Flood|1 or 4 |{background-color:#e9ae6d;}.  W|
|{background-color:#dea55e;}. Ebb | 3|{background-color:#e9ae6d;}.  W|
|/2. ENE < *E* > ESE  |{background-color:#dea55e;}. Flood | 1 or 4|{background-color:#e9ae6d;}. W |
|{background-color:#dea55e;}.  Ebb| 3|{background-color:#e9ae6d;}. W |
|/2. ESE < *SE* > SSE  |{background-color:#dea55e;}. Flood | 1|{background-color:#e9ae6d;}.  G|
|{background-color:#dea55e;}.  Ebb| 3|{background-color:#e9ae6d;}. G or W |
|/2. SSE < *S* > SSW  |{background-color:#dea55e;}. Flood |1, 2 or 4 |{background-color:#e9ae6d;}. G |
|{background-color:#dea55e;}. Ebb | 3|{background-color:#e9ae6d;}.  G|
|/2. SSW < *SW* > WSW  |{background-color:#dea55e;}.  Flood|2 or 4 |{background-color:#e9ae6d;}. W |
|{background-color:#dea55e;}.  Ebb| 3|{background-color:#e9ae6d;}.  W|
|/2. WSW < *W* > WNW  |{background-color:#dea55e;}. Flood |4 |{background-color:#e9ae6d;}.  W|
|{background-color:#dea55e;}.  Ebb| 3|{background-color:#e9ae6d;}.  W|
|/2. WNW < *NW* > NNW  |{background-color:#dea55e;}. Flood | 2 or 4|{background-color:#e9ae6d;}.  W|
|{background-color:#dea55e;}.  Ebb| 3|{background-color:#e9ae6d;}.  W|

                      
 Notes:

p((. 1.  There are many possible variations of wind and tide which might make a different course or start line position preferable - These are only suggestions!

p((. 2.  Sailing out to Channel mark 10 on an ebbing tide and potentially dying breeze is not usually safe hence the only suggested course on an ebb tide is course 3.

p((. 3.  Course 5 is very long and is only viable in near perfect conditions.  It does not therefore figure in the suggestions but if the tide is slack and the wind is good and unlikely to die please give it a go!

p((. 4.  If using Course 1 remember to record the times boats pass No 11 buoy after going out to No 10 for the first time.

p=. *3. Suggested use of Course Board*

*Contents*

table{border-collapse:collapse;width:100%;}. 
|:{width:20%;}. |{border:none;vertical-align:middle;}. |{border:none;vertical-align:middle;}. |{border:none;vertical-align:middle;}. |{border:none;vertical-align:middle;}. |{border:none;vertical-align:middle;}. |
|^. 
|{width:27%;}_. p{margin:6pt0px6pt0px;text-align:center;}. !{margin:020px10px0px;float:left;width:100%;}https://files.exe-sailing-club.org/images/Dinghies2/image0002.jpg(image0002.jpg)!

|\\2{border:none;vertical-align:middle;width:27%;}_. p{margin:6pt0px6pt0px;text-align:center;}. !{margin:020px10px0px;float:left;width:100%;}https://files.exe-sailing-club.org/images/Dinghies2/image0003.jpg(image0003.jpg)!

|{width:25%;}_. p{margin:6pt0px6pt0px;text-align:center;}. !{margin:020px10px0px;float:left;width:100%;}https://files.exe-sailing-club.org/images/Dinghies2/image0004.jpg(image0004.jpg)!

|{width:21%;}_. p{margin:6pt0px6pt0px;text-align:center;}. !{margin:020px10px0px;float:left;width:100%;}https://files.exe-sailing-club.org/images/Dinghies2/image0005.jpg(image0005.jpg)!

|
|-. 
|{width:27%;}. p. Course Board Front

|\\2{border:none;vertical-align:middle;width:27%;}. p. Course Board Back

|{width:25%;}. p. Tiles Box

|{width:21%;}. p. Tiles

|
|\\5{border:none;vertical-align:middle;width:100%;}. p. Tiles are either:

White (Lap numbers (1 to 4), course shapes (Triangle or Sausage) and Start Line (S/L) or Finish Line (F/L))

Red one side, Green the other (to indicate marks of the course (Y is for a yellow laid mark, H is for Hinton). (The green side has a black line across the middle so that red/green colour blind people can distinguish the colours)

|
|\\2{border:none;vertical-align:middle;width:51%;}. p. *Set Up*

Place the pole of the board in one of the tubes used to support the signal flags on the *side of the rib away from the start line*. Use the bungy cord to stop the board rotating in the wind and tie the board back to the A frame on Grey RIB as necessary using the ropes at the top of the board. Use the tiles to define the course. The back of the course board has been painted as a chalkboard should you need to use this facility. Please put everything away carefully at the end of racing!

|/2\\3{border:none;vertical-align:middle;width:50%;}. p{margin:6pt0px6pt0px;text-align:center;}. !{margin:020px10px0px;float:left;width:100%;}https://files.exe-sailing-club.org/images/Dinghies2/image0006.jpg(image0006.jpg)!

|
|\\2{border:none;vertical-align:middle;width:51%;}. p. *Example* The example on the right would be a triangular course beginning with the start line (S/L), Y to Port (Y on a red background), 17 to Port (17 on a red background), Y to Port (Y on a red background), and back through the finish line. The start line and finish line is between the RIB and a Yellow mark and the race consists of 3 laps.

|
""";
}
