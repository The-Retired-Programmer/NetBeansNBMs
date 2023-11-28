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
import java.io.PrintWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import uk.theretiredprogrammer.html2textile.ErrHandler;
import uk.theretiredprogrammer.html2textile.transformtext.StringProxy;

public class Rules_Test {

    public static final String SANDBOX_INPUT = "/home/richard/SANDBOX/SANDBOX/dinghies.html";

    public Rules_Test() {

    }

    @Test
    public void test1() throws IOException {
        System.out.println("test1 - htmlrules");
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            Rules.create(err);
            run_HTML_PREPROCESSING_test(
                    "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                    "abc|;| |'|'|&|xyz",
                    false);
        }
    }

    @Test
    public void test2() throws IOException {
        System.out.println("test2 - htmlrules - with special files");
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            Rules.create(new File(SANDBOX_INPUT), err);
            run_HTML_PREPROCESSING_test(
                    "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                    "abc|;| |'|!!!|&|xyz",
                    false);
        }
    }

//    @Test
//    public void test3() throws IOException {
//        System.out.println("test3 - stylerules");
//        Rules.create();
//        run_HTML_STYLE_PROCESSING_test(
//                "font-family:serif;font-size:12pt;font-size:10pt;font-size:18pt;color:#000000;color:#ffffff;",
//                "font-size:18pt;color:#ffffff;",
//                false);
//    }
//    @Test
//    public void test4() throws IOException {
//        System.out.println("test4 - stylerules - with special files");
//        Rules.create(new File(SANDBOX_INPUT));
//        run_HTML_STYLE_PROCESSING_test(
//                "font-family:serif;font-size:12pt;font-size:10pt;font-size:18pt;color:#000000;color:#ffffff;",
//                "font-size:18pt;color:#ffffff;",
//                false);
//    }
    @Test
    public void test5() throws IOException {
        System.out.println("test5 - textilerules");
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            Rules.create(err);
            run_TEXTILE_POSTPROCESSING_test(
                    "h3{text-align:center;color:red;}. text-text-text\n",
                    "h3{color:red;}=. text-text-text\n",
                    false);
        }
    }

    @Test
    public void test6() throws IOException {
        System.out.println("test6 - textilerules 2");
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            Rules.create(err);
            run_TEXTILE_POSTPROCESSING_test(
                    "p{margin:020px10px0px;float:left;width:50%;}. text-text-text\n",
                    "p(float-left-50). text-text-text\n",
                    false);
        }
    }

    @Test
    public void test7() throws IOException {
        System.out.println("test7 - textilerules - with special files");
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            Rules.create(new File(SANDBOX_INPUT), err);
            run_TEXTILE_POSTPROCESSING_test(
                    "h3{text-align:center;color:red;}. text-text-text\n",
                    "h3{color:red;}=. text-text-text\n",
                    false);
        }
    }

    @Test
    public void test8() throws IOException {
        System.out.println("test8 - textilerules 2 - with special files");
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            Rules.create(new File(SANDBOX_INPUT), err);
            run_TEXTILE_POSTPROCESSING_test(
                    "p{margin:020px10px0px;float:left;width:50%;}. text-text-text\n",
                    "p(float-PORT-50). text-text-text\n",
                    false);
        }
    }

    @Test
    public void test9() throws IOException {
        System.out.println("test9 - htmlrules - with special files and NO system file");
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            Rules.create(new File(SANDBOX_INPUT), err);
            run_HTML_PREPROCESSING_test(
                    "abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz",
                    "abc|;|&nbsp;|&lsquo;|!!!|&|xyz",
                    true);
        }
    }

    @Test
    public void test10() throws IOException {
        System.out.println("test10 - textilerules - with special files and NO system file");
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            Rules.create(new File(SANDBOX_INPUT), err);
            run_TEXTILE_POSTPROCESSING_test(
                    "h3{text-align:left;color:red;}. text-text-text\n",
                    "h3{text-align:left;color:red;}. text-text-text\n",
                    true);
        }
    }

    @Test
    public void test11() throws IOException {
        System.out.println("test11 - textilerules - with special files");
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            Rules.create(new File(SANDBOX_INPUT), err);
            run_TEXTILE_POSTPROCESSING_test(
                    "p{width:100%;margin:0;padding:0;}. text-text-text\n",
                    "text-text-text\n",
                    false);
        }
    }

    @Test
    public void test12() throws IOException {
        System.out.println("test12 - textilerules - with special files and NO system file");
        try ( PrintWriter errwriter = new PrintWriter(System.err)) {
            ErrHandler err = new ErrHandler((s) -> errwriter.println(s));
            Rules.create(new File(SANDBOX_INPUT), err);
            run_TEXTILE_POSTPROCESSING_test(
                    "p{width:100%;margin:0;padding:0;}. text-text-text\n",
                    "p{width:100%;margin:0;padding:0;}. text-text-text\n",
                    true);
        }
    }

    private void run_TEXTILE_POSTPROCESSING_test(String input, String expected, boolean ignoresystemfile) throws IOException {
        StringProxy s = Rules.get_TEXTILE_POSTPROCESSING();
        assertEquals(expected, s.applyRules(input, ignoresystemfile));
    }

    private void run_HTML_PREPROCESSING_test(String input, String expected, boolean ignoresystemfile) throws IOException {
        StringProxy s = Rules.get_HTML_PREPROCESSING();
        assertEquals(expected, s.applyRules(input, ignoresystemfile));
    }

//    private void run_HTML_STYLE_PROCESSING_test(Element element, String expected, boolean ignoresystemfile) throws IOException {
//        StyleProxy s = Rules.get_HTML_STYLE_PROCESSING();
//        assertEquals(expected, s.applyRules(element, ignoresystemfile)); ??
//    }
}
