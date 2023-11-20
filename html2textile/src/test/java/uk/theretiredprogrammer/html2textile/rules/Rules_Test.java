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

    public Rules_Test() {
    }

    @Test
    public void test1() throws IOException {
        System.out.println("test1 - htmlrules");
        systemOnly("HTML_PREPROCESSING",
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                "abc|;| |'|'|&|xyz",
                false);
    }

    @Test
    public void test2() throws IOException {
        System.out.println("test2 - htmlrules - with special files");
        systemPlusAssociated(
                new File(SANDBOX_INPUT), "HTML_PREPROCESSING",
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                "abc|;| |'|!!!|&|xyz",
                false);
    }

    @Test
    public void test3() throws IOException {
        System.out.println("test3 - stylerules");
        systemOnly("HTML_STYLE_PROCESSING",
                "font-family:serif;font-size:12pt;font-size:10pt;font-size:18pt;color:#000000;color:#ffffff;",
                "font-size:18pt;color:#ffffff;",
                false);
    }

    @Test
    public void test4() throws IOException {
        System.out.println("test4 - stylerules - with special files");
        systemPlusAssociated(new File(SANDBOX_INPUT), "HTML_STYLE_PROCESSING",
                "font-family:serif;font-size:12pt;font-size:10pt;font-size:18pt;color:#000000;color:#ffffff;",
                "font-size:18pt;color:#ffffff;",
                false);
    }

    @Test
    public void test5() throws IOException {
        System.out.println("test5 - textilerules");
        systemOnly("TEXTILE_POSTPROCESSING",
                "h3{text-align:center;color:red;}. text-text-text\n",
                "h3{color:red;}=. text-text-text\n",
                false);
    }

    @Test
    public void test6() throws IOException {
        System.out.println("test6 - textilerules 2");
        systemOnly("TEXTILE_POSTPROCESSING",
                "p{margin:020px10px0px;float:left;width:50%;}. text-text-text\n",
                "p(float-left-50). text-text-text\n",
                false);
    }

    @Test
    public void test7() throws IOException {
        System.out.println("test7 - textilerules - with special files");
        systemPlusAssociated(new File(SANDBOX_INPUT), "TEXTILE_POSTPROCESSING",
                "h3{text-align:center;color:red;}. text-text-text\n",
                "h3{color:red;}=. text-text-text\n",
                false);
    }

    @Test
    public void test8() throws IOException {
        System.out.println("test8 - textilerules 2 - with special files");
        systemPlusAssociated(new File(SANDBOX_INPUT), "TEXTILE_POSTPROCESSING",
                "p{margin:020px10px0px;float:left;width:50%;}. text-text-text\n",
                "p(float-PORT-50). text-text-text\n",
                false);
    }

    @Test
    public void test9() throws IOException {
        System.out.println("test9 - htmlrules - with special files and NO system file");
        systemPlusAssociated(new File(SANDBOX_INPUT), "HTML_PREPROCESSING",
                "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                "abc|;|&nbsp;|&lsquo;|!!!|&|xyz",
                true);
    }

    @Test
    public void test10() throws IOException {
        System.out.println("test10 - textilerules - with special files and NO system file");
        systemPlusAssociated(new File(SANDBOX_INPUT), "TEXTILE_POSTPROCESSING",
                "h3{text-align:left;color:red;}. text-text-text\n",
                "h3{text-align:left;color:red;}. text-text-text\n",
                true);
    }

    @Test
    public void test11() throws IOException {
        System.out.println("test11 - textilerules - with special files");
        systemPlusAssociated(new File(SANDBOX_INPUT), "TEXTILE_POSTPROCESSING",
                "p{width:100%;margin:0;padding:0;}. text-text-text\n",
                "text-text-text\n",
                false);
    }

    @Test
    public void test12() throws IOException {
        System.out.println("test12 - textilerules - with special files and NO system file");
        systemPlusAssociated(new File(SANDBOX_INPUT), "TEXTILE_POSTPROCESSING",
                "p{width:100%;margin:0;padding:0;}. text-text-text\n",
                "p{width:100%;margin:0;padding:0;}. text-text-text\n",
                true);
    }

    private void systemOnly(String key, String input, String expected, boolean ignoresystemfile) throws IOException {
        Rules.create();
        runtest(key, input, expected, ignoresystemfile);
    }

    private void systemPlusAssociated(File inputfile, String key, String input, String expected, boolean ignoresystemfile) throws IOException {
        Rules.create(inputfile);
        runtest(key, input, expected, ignoresystemfile);
    }

    private void runtest(String key, String input, String expected, boolean ignoresystemfile) throws IOException {
        StringProxy s = new StringProxy();
        s.set(input);
        Rules.get(key).applyRuleActions(s, ignoresystemfile);
        assertEquals(expected, s.get());
    }
}
