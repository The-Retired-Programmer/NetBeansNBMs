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
package uk.theretiredprogrammer.rawcsv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.junit.jupiter.api.Test;

public class RawCSV2CSV_Test {

    public RawCSV2CSV_Test() {
    }

    @Test
    public void testexecute() throws IOException {
        RawCSV2CSV convertor = new RawCSV2CSV();
        PrintWriter err = new PrintWriter(System.err);
        BufferedReader rawcsv = new BufferedReader(new FileReader("/home/richard/PRODUCTSTESTDATA/RawCSV/MandS20231010.rawcsv"));
        PrintWriter csv = new PrintWriter(new FileWriter("/home/richard/PRODUCTSTESTDATA/RawCSV/MandS20231010.csv"));
        InputStream ris = this.getClass().getClassLoader().getResourceAsStream("uk/theretiredprogrammer/rawcsv/rules");
        BufferedReader rules = new BufferedReader(new InputStreamReader(ris));
        try(rawcsv;csv;rules;err) {
            convertor.convert(rawcsv, csv, rules, err);
        }
    }
}
