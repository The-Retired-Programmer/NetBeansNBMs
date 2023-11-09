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

    public App_Test() {
    }

    @Test
    public void testApp1() {
        System.out.println("test1");
        App app = new App();
        int rc = app.goInner(new String[]{SANDBOX + "dinghies.html"});
        assertEquals(0, rc);
    }
    
    @Test
    public void testApp2() {
        System.out.println("test2");
        App app = new App();
        int rc = app.goInner(new String[]{SANDBOX + "dinghies.html",SANDBOX + "dinghiesCopy.html"});
        assertEquals(0, rc);
    }

    @Test
    public void testApp3() {
        System.out.println("test3");
        App app = new App();
        int rc = app.goInner(new String[]{"-x", SANDBOX + "dinghies.html"});
        assertEquals(0, rc);
    }

    @Test
    public void testApp4() {
        System.out.println("test4");
        App app = new App();
        int rc = app.goInner(new String[]{SANDBOX + "dinghiesCopy.html"});
        assertEquals(0, rc);
    }

    @Test
    public void testApp5() {
        System.out.println("test5");
        App app = new App();
        int rc = app.goInner(new String[]{"-x", SANDBOX + "dinghiesCopy.html"});
        assertEquals(0, rc);
    }

    // failure modes
    @Test
    public void testApp11() {
        System.out.println("test11");
        App app = new App();
        int rc = app.goInner(new String[]{SANDBOX + "dinghiesx.html"});
        assertEquals(4, rc);
    }

    // parameter line failures

    @Test
    public void testApp26() {
        System.out.println("test26");
        App app = new App();
        int rc = app.goInner(new String[]{"-x", "true", SANDBOX + "dinghiesCopy.html"});
        assertEquals(4, rc);
    }
}
