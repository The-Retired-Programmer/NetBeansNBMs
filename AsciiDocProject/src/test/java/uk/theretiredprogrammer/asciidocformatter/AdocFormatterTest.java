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
package uk.theretiredprogrammer.asciidocformatter;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdocFormatterTest {

    public AdocFormatterTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @BeforeEach
    public void setUp() {
    }

//    @Test
//    public void testReformattedSelected_AdocBlock() throws Exception {
//        System.out.println("reformattedSelected");
//        AdocBlock block = null;
//        AdocFormatter instance = new AdocFormatter();
//        ReformattedBlocks expResult = null;
//        ReformattedBlocks result = instance.reformattedSelected(block);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//    @Test
//    public void testReformattedSelected_List() throws Exception {
//        System.out.println("reformattedSelected");
//        List<AdocBlock> blocks = null;
//        AdocFormatter instance = new AdocFormatter();
//        ReformattedBlocks expResult = null;
//        ReformattedBlocks result = instance.reformattedSelected(blocks);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
    // tests for sentenceReformat
    @Test
    public void testSentenceReformat_1() {
        System.out.println("sentenceReformat_1");
        String text = "sentence1 sentence2 sentence3 sentence4 sentence5 sentence6 sentence7 sentence8 sentence9";
        StringBuilder out = new StringBuilder();
        AdocFormatter instance = new AdocFormatter();
        instance.blockReformat(text, out);
        String expected = "sentence1 sentence2 sentence3 sentence4 sentence5 sentence6 sentence7 sentence8\nsentence9\n";
        assertEquals(expected, out.toString());
    }

    @Test
    public void testSentenceReformat_2() {
        System.out.println("sentenceReformat_2");
        String text = "sentence1 sentence2 sentence3 sentence4 sentence5 sentence6 sentence7sentence8sentence9";
        StringBuilder out = new StringBuilder();
        AdocFormatter instance = new AdocFormatter();
        instance.blockReformat(text, out);
        String expected = "sentence1 sentence2 sentence3 sentence4 sentence5 sentence6\nsentence7sentence8sentence9\n";
        assertEquals(expected, out.toString());
    }

    @Test
    public void testSentenceReformat_3() {
        System.out.println("sentenceReformat_3");
        String text = "sentence1sentence2sentence3sentence4sentence5sentence6sentence7sentence8sentence9sentence10";
        StringBuilder out = new StringBuilder();
        AdocFormatter instance = new AdocFormatter();
        instance.blockReformat(text, out);
        String expected = "sentence1sentence2sentence3sentence4sentence5sentence6sentence7sentence8sentence9sentence10\n";
        assertEquals(expected, out.toString());
    }

    @Test
    public void testSentenceReformat_4() {
        System.out.println("sentenceReformat_4");
        String text = "sentence1sentence2sentence3sentence4 sentence5sentence6sentence7sentence8sentence9sentence10";
        StringBuilder out = new StringBuilder();
        AdocFormatter instance = new AdocFormatter();
        instance.blockReformat(text, out);
        String expected = "sentence1sentence2sentence3sentence4\nsentence5sentence6sentence7sentence8sentence9sentence10\n";
        assertEquals(expected, out.toString());
    }

    @Test
    public void testSentenceReformat_5() {
        System.out.println("sentenceReformat_5");
        String text = "sentence1sentence2sentence3sentence4    sentence5sentence6sentence7sentence8sentence9sentence10";
        StringBuilder out = new StringBuilder();
        AdocFormatter instance = new AdocFormatter();
        instance.blockReformat(text, out);
        String expected = "sentence1sentence2sentence3sentence4\nsentence5sentence6sentence7sentence8sentence9sentence10\n";
        assertEquals(expected, out.toString());
    }

    @Test
    public void testSentenceReformat_6() {
        System.out.println("sentenceReformat_6");
        String text = "sentence1sentence2sentence3sentence4    sentence5sentence6sentence7 sentence8sentence9sentence10";
        StringBuilder out = new StringBuilder();
        AdocFormatter instance = new AdocFormatter();
        instance.blockReformat(text, out);
        String expected = "sentence1sentence2sentence3sentence4    sentence5sentence6sentence7\nsentence8sentence9sentence10\n";
        assertEquals(expected, out.toString());
    }

    @Test
    public void testSentenceReformat_7() {
        System.out.println("sentenceReformat_7");
        String text = "sentence1sentence2sentence3sentence4 sentence5sentence6sentence7    sentence8sentence9sentence10";
        StringBuilder out = new StringBuilder();
        AdocFormatter instance = new AdocFormatter();
        instance.blockReformat(text, out);
        String expected = "sentence1sentence2sentence3sentence4 sentence5sentence6sentence7\nsentence8sentence9sentence10\n";
        assertEquals(expected, out.toString());
    }

    @Test
    public void testSentenceReformat_8() {
        System.out.println("sentenceReformat_8");
        String text = "12345678901234567890123456789012345678901234567890123456789012345678901234567890 nextline";
        StringBuilder out = new StringBuilder();
        AdocFormatter instance = new AdocFormatter();
        instance.blockReformat(text, out);
        String expected = "12345678901234567890123456789012345678901234567890123456789012345678901234567890\nnextline\n";
        assertEquals(expected, out.toString());
    }

    @Test
    public void testSentenceReformat_9() {
        System.out.println("sentenceReformat_9");
        String text = "12345678901234567890123456789012345678901234567890123456789012345678901234567890                     nextline";
        StringBuilder out = new StringBuilder();
        AdocFormatter instance = new AdocFormatter();
        instance.blockReformat(text, out);
        String expected = "12345678901234567890123456789012345678901234567890123456789012345678901234567890\nnextline\n";
        assertEquals(expected, out.toString());
    }

    @Test
    public void testSentenceSplit_1() {
        System.out.println("sentenceSplit_1");
        String text = "sentence1";
        AdocFormatter instance = new AdocFormatter();
        List<String> res = instance.sentenceSplit(text);
        String[] expected = new String[]{"sentence1"};
        assertEquals(expected.length, res.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    @Test
    public void testSentenceSplit_2() {
        System.out.println("sentenceSplit_2");
        String text = "sentence1.";
        AdocFormatter instance = new AdocFormatter();
        List<String> res = instance.sentenceSplit(text);
        String[] expected = new String[]{"sentence1."};
        assertEquals(expected.length, res.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    @Test
    public void testSentenceSplit_3() {
        System.out.println("sentenceSplit_3");
        String text = "sentence1. sentence2.";
        AdocFormatter instance = new AdocFormatter();
        List<String> res = instance.sentenceSplit(text);
        String[] expected = new String[]{"sentence1.", "sentence2."};
        assertEquals(expected.length, res.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    @Test
    public void testSentenceSplit_4() {
        System.out.println("sentenceSplit_4");
        String text = "sentence1. sentence2";
        AdocFormatter instance = new AdocFormatter();
        List<String> res = instance.sentenceSplit(text);
        String[] expected = new String[]{"sentence1.", "sentence2"};
        assertEquals(expected.length, res.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    @Test
    public void testSentenceSplit_5() {
        System.out.println("sentenceSplit_5");
        String text = "sentence1.                                                 sentence2.";
        AdocFormatter instance = new AdocFormatter();
        List<String> res = instance.sentenceSplit(text);
        String[] expected = new String[]{"sentence1.", "sentence2."};
        assertEquals(expected.length, res.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    @Test
    public void testSentenceSplit_6() {
        System.out.println("sentenceSplit_6");
        String text = "sentence1.                                                 sentence2";
        AdocFormatter instance = new AdocFormatter();
        List<String> res = instance.sentenceSplit(text);
        String[] expected = new String[]{"sentence1.", "sentence2"};
        assertEquals(expected.length, res.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    @Test
    public void testSentenceSplit_7() {
        System.out.println("sentenceSplit_7");
        String text = "url is https://domain.org so this is still the sentence. end";
        AdocFormatter instance = new AdocFormatter();
        List<String> res = instance.sentenceSplit(text);
        String[] expected = new String[]{"url is https://domain.org so this is still the sentence.", "end"};
        assertEquals(expected.length, res.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    @Test
    public void testSentenceSplit_8() {
        System.out.println("sentenceSplit_8");
        String text = ".  numbered list element - so this is still the sentence. end";
        AdocFormatter instance = new AdocFormatter();
        List<String> res = instance.sentenceSplit(text);
        String[] expected = new String[]{".  numbered list element - so this is still the sentence.", "end"};
        assertEquals(expected.length, res.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    @Test
    public void testSentenceSplit_9() {
        System.out.println("sentenceSplit_9");
        String text = "    .  indented numbered list element - so this is still the sentence. end";
        AdocFormatter instance = new AdocFormatter();
        List<String> res = instance.sentenceSplit(text);
        String[] expected = new String[]{"    .  indented numbered list element - so this is still the sentence.", "end"};
        assertEquals(expected.length, res.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }
}
