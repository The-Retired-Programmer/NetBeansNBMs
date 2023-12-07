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
package uk.theretiredprogrammer.html2textile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class App_Test {

    public static final String SANDBOX = "/home/richard/SANDBOX/SANDBOX/";
    public static final String SANDBOX2 = "/home/richard/SANDBOX/SANDBOX2/";

    public App_Test() {
    }

    @Test
    public void testApp1() {
        App app = new App();
        int rc = app.goInner(new String[]{SANDBOX + "dinghies.fragment.html"});
        assertEquals(0, rc);
    }

    @Test
    public void testApp2() {
        App app = new App();
        int rc = app.goInner(new String[]{SANDBOX + "dinghies.fragment.html", SANDBOX + "dinghiesCopy.fragment.html"});
        assertEquals(0, rc);
    }

    @Test
    public void testApp3() {
        App app = new App();
        int rc = app.goInner(new String[]{SANDBOX + "dinghiesCopy.fragment.html"});
        assertEquals(0, rc);
    }
    
    @Test
    public void testApp4() {
        App app = new App();
        int rc = app.goInner(new String[]{"-i",SANDBOX2 + "dinghiesCopy.fragment.html"});
        assertEquals(0, rc);
    }
    
    @Test
    public void testApp5() {
        App app = new App();
        int rc = app.goInner(new String[]{"-h",SANDBOX2 + "dinghiesCopy2.fragment.html"});
        assertEquals(0, rc);
    }
    
//    @Test
    public void testApp6() {
        App app = new App();
        int rc = app.goInner(new String[]{SANDBOX2 + "dinghiesCopy2.transformed.html"});
        assertEquals(0, rc);
    }

    // failure modes
    @Test
    public void testApp7() {
        App app = new App();
        int rc = app.goInner(new String[]{SANDBOX + "dinghiesx.fragment.html"});
        assertEquals(4, rc);
    }
}
