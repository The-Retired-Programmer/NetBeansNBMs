/*
 * Copyright 2022 richard.
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
package uk.theretiredprogrammer.actionssupport.implementation;

import uk.theretiredprogrammer.actionssupport.implementation.CLICommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author richard
 */
public class CLICommandTest {
    
    public CLICommandTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }


    

    @Test
    public void testParse2wordsA() {
        System.out.println("parse2words - help");
        String command = "help";
        CLICommand instance = new CLICommand("testing","test","dummy");
        String[] expResult = new String[] {"help"};
        String[] result = instance.parse2words(command);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testParse2wordsB() {
        System.out.println("parse2words - muliple words");
        String command = "help help2 help3";
        CLICommand instance = new CLICommand("testing","test","dummy");
        String[] expResult = new String[] {"help", "help2", "help3"};
        String[] result = instance.parse2words(command);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testParse2wordsC() {
        System.out.println("parse2words - multiple words with leading spaces and multiple spaces as separators");
        String command = "    help   help2                    help3 Help4";
        CLICommand instance = new CLICommand("testing","test","dummy");
        String[] expResult = new String[] {"help", "help2", "help3", "Help4"};
        String[] result = instance.parse2words(command);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testParse2wordsD() {
        System.out.println("parse2words - quoted help");
        String command = "\"help\"";
        CLICommand instance = new CLICommand("testing","test","dummy");
        String[] expResult = new String[] {"help"};
        String[] result = instance.parse2words(command);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testParse2wordsE() {
        System.out.println("parse2words - quoted and unquoted");
        String command = "  \"help\" -b filename -c \"file name\"     ";
        CLICommand instance = new CLICommand("testing","test","dummy");
        String[] expResult = new String[] {"help", "-b", "filename", "-c", "file name"};
        String[] result = instance.parse2words(command);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testParse2wordsF() {
        System.out.println("parse2words - quoted unterminated");
        String command = "  \"help  ";
        CLICommand instance = new CLICommand("testing","test","dummy");
        String[] expResult = new String[] {"help"};
        String[] result = instance.parse2words(command);
        assertArrayEquals(expResult, result);
    }
}
