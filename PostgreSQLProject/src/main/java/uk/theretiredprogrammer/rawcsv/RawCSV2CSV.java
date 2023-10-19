/*
 * Copyright 2023 richard.
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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class RawCSV2CSV {
    
    private final static String REPLACE_SEPARATOR = "###";

    // rules
    private String match;
    private String replace;
    private List<ColDef> columndefs;

    public RawCSV2CSV() {
    }

    public void convert(BufferedReader rawcsv, PrintWriter csv, BufferedReader rules, PrintWriter err) throws IOException {
        columndefs = extractRules(rules);
        replace = buildReplace();
        csv.println(buildCSVHeaderline());
        String line;
        while ((line = rawcsv.readLine()) != null) {
            csv.println(toCSV(line, err));
        }
    }
    
    private String buildReplace(){
        StringJoiner sj = new StringJoiner(REPLACE_SEPARATOR);
        for (var columndef : columndefs) {
            sj.add(columndef.matchpartexpression);
        }
        return sj.toString();
    }

    private String toCSV(String line, PrintWriter err) {
        String[] csv = line.replaceAll(match, replace).split(REPLACE_SEPARATOR);
        for (int i = 0; i < csv.length; i++) {
            csv[i] = doAction(csv[i], columndefs.get(i));
        }
        return buildCSVDataline(csv);
    }
    
    private String buildCSVHeaderline() {
        StringJoiner sj = new StringJoiner(",");
        for (var columndef : columndefs) {
            sj.add(columndef.name);
        }
        return sj.toString();
    }

    private String buildCSVDataline(String[] fields) {
        StringJoiner sj = new StringJoiner(",");
        for (int i = 0; i < fields.length; i++) {
            ColDef columndef = columndefs.get(i);
            sj.add(columndef.quote ? "\"" + fields[i] + "\"" : fields[i]);
        }
        return sj.toString();
    }

    private String doAction(String value, ColDef columndef) {
        return switch (columndef.action) {
            case "TOISODATE" ->
                toISODate(value);
            case "TOSIGNEDCURRENCY" ->
                toSignedCurrency(value);
            case "TRIM" ->
                value.trim();
            default ->
                value;
        };
    }

    private String toISODate(String value) {
        String[] parts = value.trim().split(" ");
        return "20" + parts[2] + tomonthdigits(parts[1]) + parts[0];
    }

    private static final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static final String[] monthdigits = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

    private String tomonthdigits(String month) {
        for (int i = 0; i < months.length; i++) {
            if (month.equals(months[i])) {
                return monthdigits[i];
            }
        }
        return "??";
    }

    private String toSignedCurrency(String value) {
        value = value.trim();
        return value.endsWith("CR") ? value.substring(1, value.length() - 2).replaceAll(",", "") : "-" + value.substring(1).replaceAll(",", "");
    }

    private List<ColDef> extractRules(BufferedReader rules) throws IOException {
        List<ColDef> cols = new ArrayList<>();
        String line;
        boolean isnextlinematch = true;
        while ((line=rules.readLine())!= null) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                if (isnextlinematch) {
                    match=line;
                    isnextlinematch = false;
                } else {
                    cols.add(new ColDef(line));
                }
            }
        }
        return cols;
    }
    
    private class ColDef {
        String name;
        String matchpartexpression;
        String action;
        boolean quote;
        
        public ColDef(String def) throws IOException {
            String[] parts = def.split(",");
            if (parts.length == 3 || parts.length == 4 ) {
                name = parts[0].trim();
                matchpartexpression = parts[1].trim();
                action = parts[2].toUpperCase().trim();
                quote = parts.length == 4 ? parts[3].trim().equals("quote"): false;
            } else {
                throw new IOException("Rules file error - in column definition: "+def);
            }
        }
    }
}
