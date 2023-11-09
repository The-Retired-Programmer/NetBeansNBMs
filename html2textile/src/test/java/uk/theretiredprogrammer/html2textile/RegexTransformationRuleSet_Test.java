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

import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class RegexTransformationRuleSet_Test {
    
    public static final String SANDBOX_INPUT = "/home/richard/SANDBOX/SANDBOX/dinghies.html";
    
    public RegexTransformationRuleSet_Test() {
    }

    @Test
    public void test1() throws IOException {
        System.out.println("test1 - htmlrules");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet("htmlrules");
        String res = rules.transform("abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz");
        assertEquals("abc|;| |'|'|&|xyz",res);
    }
    
    @Test
    public void test2() throws IOException {
        System.out.println("test2 - htmlrules - with special files");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet(new File(SANDBOX_INPUT),"htmlrules",false);
        String res = rules.transform("abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz");
        assertEquals("abc|;| |'|!!!|&|xyz",res);
    }
    
    @Test
    public void test3() throws IOException {
        System.out.println("test3 - stylerules");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet("stylerules");
        String res = rules.transform("font-family:serif;font-size:12pt;font-size:10pt;font-size:18pt;color:#000000;color:#ffffff;");
        assertEquals("font-size:18pt;color:#ffffff;",res);
    }
    
    @Test
    public void test4() throws IOException {
        System.out.println("test4 - stylerules - with special files");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet(new File(SANDBOX_INPUT),"stylerules", false);
        String res = rules.transform("font-family:serif;font-size:12pt;font-size:10pt;font-size:18pt;color:#000000;color:#ffffff;");
        assertEquals("font-size:18pt;color:#ffffff;",res);
    }
    
    @Test
    public void test5() throws IOException {
        System.out.println("test5 - textilerules");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet("textilerules");
        String res = rules.transform("h3{text-align:center;color:red;}. text-text-text\n");
        assertEquals("h3{color:red;}=. text-text-text\n",res);
    }
    
    @Test
    public void test6() throws IOException {
        System.out.println("test6 - textilerules 2");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet("textilerules");
        String res = rules.transform("p{margin:020px10px0px;float:left;width:50%;}. text-text-text\n");
        assertEquals("p(float-left-50). text-text-text\n",res);
    }
    
    @Test
    public void test7() throws IOException {
        System.out.println("test7 - textilerules - with special files");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet(new File(SANDBOX_INPUT),"textilerules", false);
        String res = rules.transform("h3{text-align:center;color:red;}. text-text-text\n");
        assertEquals("h3{color:red;}=. text-text-text\n",res);
    }
    
    @Test
    public void test8() throws IOException {
        System.out.println("test8 - textilerules 2 - with special files");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet(new File(SANDBOX_INPUT),"textilerules", false);
        String res = rules.transform("p{margin:020px10px0px;float:left;width:50%;}. text-text-text\n");
        assertEquals("p(float-PORT-50). text-text-text\n",res);
    }
    
    @Test
    public void test9() throws IOException {
        System.out.println("test9 - htmlrules - with special files and NO system file");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet(new File(SANDBOX_INPUT),"htmlrules",true);
        String res = rules.transform("abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz");
        assertEquals("abc|;|&nbsp;|&lsquo;|!!!|&|xyz",res);
    }
    
    @Test
    public void test10() throws IOException {
        System.out.println("test10 - textilerules - with special files and NO system file");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet(new File(SANDBOX_INPUT),"textilerules", true);
        String res = rules.transform("h3{text-align:left;color:red;}. text-text-text\n");
        assertEquals("h3{text-align:left;color:red;}. text-text-text\n",res);
    }
    
    @Test
    public void test11() throws IOException {
        System.out.println("test11 - textilerules - with special files");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet(new File(SANDBOX_INPUT),"textilerules", false);
        String res = rules.transform("p{width:100%;margin:0;padding:0;}. text-text-text\n");
        assertEquals("text-text-text\n",res);
    }
    
     @Test
    public void test12() throws IOException {
        System.out.println("test12 - textilerules - with special files and NO system file");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet(new File(SANDBOX_INPUT),"textilerules", true);
        String res = rules.transform("p{width:100%;margin:0;padding:0;}. text-text-text\n");
        assertEquals("p{width:100%;margin:0;padding:0;}. text-text-text\n",res);
    }
    
    @Test
    public void test13() throws IOException {
        System.out.println("test13 - htmlrules - with special files from parent");
        RegexTransformationRuleSet rules = new RegexTransformationRuleSet(new File(SANDBOX_INPUT),"htmlrules",false);
        String res = rules.transform("abc|;|&nbsp;|&lsquo;|&rsquo;|&|xyz");
        assertEquals("abc|;| |'|!!!|&|xyz",res);
    }
}
