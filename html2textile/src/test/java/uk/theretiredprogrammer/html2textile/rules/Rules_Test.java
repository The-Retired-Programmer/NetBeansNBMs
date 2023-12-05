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

    @Test
    public void test1() throws IOException {
        Rules.create();
        run_HTML_PREPROCESSING_test(
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                "abc|;| |'|'|&|xyz"
        );
    }

    @Test
    public void test2() throws IOException {
        Rules.create(new File(SANDBOX_INPUT));
        run_HTML_PREPROCESSING_test(
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                "abc|;| |'|!!!|&|xyz"
        );
    }

    @Test
    public void test5() throws IOException {
        Rules.create();
        run_TEXTILE_POSTPROCESSING_test(
                "h3{text-align:center;color:red;}. text-text-text\n",
                "h3{color:red;}=. text-text-text\n"
        );
    }

    @Test
    public void test6() throws IOException {
        Rules.create();
        run_TEXTILE_POSTPROCESSING_test(
                "p{margin:020px10px0px;float:left;width:50%;}. text-text-text\n",
                "p(float-left-50). text-text-text\n"
        );
    }

    @Test
    public void test7() throws IOException {
        Rules.create(new File(SANDBOX_INPUT));
        run_TEXTILE_POSTPROCESSING_test(
                "h3{text-align:center;color:red;}. text-text-text\n",
                "h3{color:red;}=. text-text-text\n"
        );
    }

    @Test
    public void test8() throws IOException {
        Rules.create(new File(SANDBOX_INPUT));
        run_TEXTILE_POSTPROCESSING_test(
                "p{margin:020px10px0px;float:left;width:50%;}. text-text-text\n",
                "p(float-PORT-50). text-text-text\n"
        );
    }

    @Test
    public void test9() throws IOException {
        Rules.create(new File(SANDBOX_INPUT2));
        run_HTML_PREPROCESSING_test(
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                "abc|;|&nbsp;|&lsquo;|!!!|&|xyz"
        );
    }

    @Test
    public void test10() throws IOException {
        Rules.create(new File(SANDBOX_INPUT2));
        run_TEXTILE_POSTPROCESSING_test(
                "h3{text-align:left;color:red;}. text-text-text\n",
                "h3{text-align:left;color:red;}. text-text-text\n"
        );
    }

    @Test
    public void test11() throws IOException {
        Rules.create(new File(SANDBOX_INPUT));
        run_TEXTILE_POSTPROCESSING_test(
                "p{width:100%;margin:0;padding:0;}. text-text-text\n",
                "text-text-text\n"
        );
    }

    @Test
    public void test12() throws IOException {
        Rules.create(new File(SANDBOX_INPUT2));
        run_TEXTILE_POSTPROCESSING_test(
                "p{width:100%;margin:0;padding:0;}. text-text-text\n",
                "p{width:100%;margin:0;padding:0;}. text-text-text\n"
        );
    }

    private void run_TEXTILE_POSTPROCESSING_test(String input, String expected) throws IOException {
        StringProxy s = Rules.get_TEXTILE_POSTPROCESSING();
        assertEquals(expected, s.applyRules(input));
    }

    private void run_HTML_PREPROCESSING_test(String input, String expected) throws IOException {
        StringProxy s = Rules.get_HTML_PREPROCESSING();
        assertEquals(expected, s.applyRules(input));
    }
}
