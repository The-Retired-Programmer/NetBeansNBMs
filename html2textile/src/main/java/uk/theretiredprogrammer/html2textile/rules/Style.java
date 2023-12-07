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
package uk.theretiredprogrammer.html2textile.rules;

import java.io.IOException;

public class Style {

    private String name;
    private String value;

    public Style(String rule) throws IOException {
        normalise(rule);
    }
    
    private void normalise(String rule) throws IOException {
        rule = rule.strip();
        String[] parts = rule.split(":");
        if (parts.length != 2) {
            throw new IOException("error - bad style rule: " + rule);
        }
        normalise(parts[0], parts[1]);
    }

    public Style(String name, String value) {
        normalise(name, value);
    }

    private void normalise(String name, String value) {
        this.name = normalisename(name);
        this.value = normalisevalue(value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String newvalue){
        this.value= newvalue;
    }
    
    public void replace(String updaterule) throws IOException {
        normalise(updaterule);
    }

    public boolean isSame(Style other) {
        return other != null && this.name.equals(other.name) && this.value.equals(other.value);
    }

    public String toString() {
        return name + ": " + value + "; ";
    }

    private String normalisename(String name) {
        return name.strip();
    }

    private String normalisevalue(String value) {
        String v = value.strip();
        if (v.endsWith(";")) {
            v = v.substring(0, v.length() - 1);
        }
        return v.strip().replaceAll("\\h{2,}", " ").replaceAll(",\\h+", ",");
    }
}
