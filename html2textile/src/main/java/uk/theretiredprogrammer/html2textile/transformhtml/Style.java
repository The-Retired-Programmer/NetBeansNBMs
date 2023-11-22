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
package uk.theretiredprogrammer.html2textile.transformhtml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.w3c.dom.Element;

public class Style {
    
    private final Map<String,String> srules = new HashMap<>();
    
    public boolean getStyle(Element element) throws IOException {
        String style = element.getAttribute("style");
        return style.isBlank() ? false : extractStyleRules(style);
    }
    
    private boolean extractStyleRules(String style) throws IOException {
        String[]splitrules = style.split(";");
        for (String srule : splitrules) {
            extractStyleRuleNameAndValue(srule);
        }
        
        return true;
    }
    
    private void extractStyleRuleNameAndValue(String srule) throws IOException {
        srule = srule.strip();
        String[]parts = srule.split(":");
        if ( parts.length != 2) {
            throw new IOException("error - bad style rule: "+srule);
        }
        normaliseandinsert(parts[0], parts[1]);
    }
    
    public void insertStyleRule(String name, String value) {
        normaliseandinsert(name, value);
    }
    
    public void removeStyleRule(String name) {
        srules.remove(name.strip());
    }
    
    public void removeStyleRuleIf(String name, String value) {
        srules.remove(name.strip(),normalisevalue(value));
    }
    
    public void removeStyleRuleIfPattern(String pattern) {
        List<String> rulenames = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (var srule: srules.entrySet()) {
           preparerule(srule,sb);
           String rule = sb.toString();
           if (rule.matches(pattern)) {
               String[]parts = rule.split(":");
               rulenames.add(parts[0]);
           }
           sb.setLength(0);
        }
        for(var rulename: rulenames) {
            srules.remove(rulename);
        }
    }
    
    private void normaliseandinsert(String name, String value) {
        srules.put(name.strip(), normalisevalue(value));
    }
    
    private String normalisevalue(String value) {
        return value.strip().replaceAll("\\h{2,}", " ");
    }
    
    public void putStyle(Element element) {
        StringBuilder sb = new StringBuilder();
        for (var srule: srules.entrySet()) {
           preparerule(srule,sb);
        }
        element.setAttribute("style", sb.toString());
    }
    
    private void preparerule(Entry<String, String> srule, StringBuilder sb) {
        sb.append(srule.getKey());
        sb.append(": ");
        sb.append(srule.getValue());
        sb.append(";");
    } 
}
