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
package uk.theretiredprogrammer.html2textile.rules;

import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.html2textile.transformtext.StringProxy;

public class Rules_Test {

    public static final String SANDBOX_INPUT = "/home/richard/SANDBOX/SANDBOX/dinghies.html";
    public static final String SANDBOX_INPUT2 = "/home/richard/SANDBOX/SANDBOX2/dinghies.html";
    public static final String SANDBOX_INPUT2A = "/home/richard/SANDBOX/SANDBOX2/dinghiesCopy.html";

    @Test
    public void test1() throws IOException {
        Rules.create();
        run_HTML_PREPROCESSING_test(
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz"
        );
    }
    
    @Test
    public void test2() throws IOException {
        Rules.create(new File(SANDBOX_INPUT));
        run_HTML_PREPROCESSING_test(
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                "abc|;| |'|'|&|xyz"
        );
    }
    
    @Test
    public void test3() throws IOException {
        Rules.create(new File(SANDBOX_INPUT2));
        run_HTML_PREPROCESSING_test(
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                "abc|;| |'|!!!|&|xyz"
        );
    }
    
    @Test
    public void test4() throws IOException {
        Rules.create(new File(SANDBOX_INPUT2A));
        run_HTML_PREPROCESSING_test(
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                "abc|;| |{}|???|&|xyz"
        );
    }

    private void run_HTML_PREPROCESSING_test(String input, String expected) throws IOException {
        StringProxy s = Rules.get_HTML_PREPROCESSING();
        assertEquals(expected, s.applyRules(input));
    }
}
