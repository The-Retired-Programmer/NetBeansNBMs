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
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

public class StyleAttribute {

    private final List<Style> srules = new ArrayList<>();

    public boolean extract(Element element) throws IOException {
        String style = element.getAttribute("style");
        return style.isBlank() ? false : extract(style);
    }

    public boolean extractIfOnlyAttribute(Element element) throws IOException {
        return element.getAttributes().getLength() == 1 ? extract(element) : false;
    }

    public boolean extract(String style) throws IOException {
        String[] splitrules = style.split(";");
        for (String srule : splitrules) {
            if (!srule.isBlank()) {
                replaceoradd(new Style(srule));
            }
        }
        return true;
    }

    public final List<Style> getStyleRules() {
        return srules;
    }

    public boolean isSame(String[] comparisonrules) throws IOException {
        if (comparisonrules.length == srules.size()) {
            for (String comparisonrule : comparisonrules) {
                Style crule = new Style(comparisonrule);
                if (!crule.isSame(lookup(crule.getName()))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public StyleAttribute clear() {
        srules.clear();
        return this;
    }

    public boolean contains(Style find) {
        return find.isSame(lookup(find.getName()));
    }

    public boolean isEmpty() {
        return srules.isEmpty();
    }

    public Style lookup(String name) {
        for (Style rule : srules) {
            if (rule.getName().equals(name)) {
                return rule;
            }
        }
        return null;
    }

    public StyleAttribute insertStyleRule(String name, String value) throws IOException {
        return insertStyleRule(new Style(name, value));
    }

    public StyleAttribute insertStyleRule(Style rule) {
        replaceoradd(rule);
        return this;
    }
    
    private void replaceoradd(Style rule) {
        Style found = lookup(rule.getName());
        if (found != null) {
           srules.remove(found);
        }
        srules.add(rule);
    }

    public StyleAttribute insertStyle(StyleAttribute styletoadd) {
        for (Style rule : styletoadd.srules) {
            replaceoradd(rule);
        }
        return this;
    }

    public Style removeStyleRuleIfName(String name) {
        Style found = lookup(name);
        srules.remove(found);
        return found;
    }

    public Style removeStyleRule(Style remove) {
        Style found = lookup(remove.getName());
        if (remove.isSame(found)) {
            srules.remove(found);
        }
        return found;
    }

    public Style removeThisStyleRule(Style remove) {
        srules.remove(remove);
        return remove;
    }

    public List<Style> removeStyleRuleIfPattern(String pattern) {
        List<Style> rulestoremove = new ArrayList<>();
        for (var srule : srules) {
            String rule = srule.toString();
            if (rule.matches(pattern)) {
                rulestoremove.add(srule);
            }
        }
        for (var rule : rulestoremove) {
            srules.remove(rule);
        }
        return rulestoremove;
    }

    public Style replaceStyleRuleValue(String namematch, String valuereplacement) {
        Style found = lookup(namematch);
        if (found == null) {
            return null;
        }
        found.setValue(valuereplacement);
        return found;
    }

    public Style replaceStyleRule(String original, String replacement) throws IOException {
            Style originalsr = new Style(original);
            Style replacementsr = new Style(replacement);
            Style found = lookup(originalsr.getName());
            if (found == null) {
                return null;
            }
            srules.remove(found);
            srules.add(replacementsr);
            return replacementsr;
    }

    public List<Style> replaceStyleRuleUsingPattern(String pattern, String replacement) throws IOException {
        List<Style> rulesreplaced = new ArrayList<>();
        List<Style> rulesremoved = new ArrayList<>();
        for (var srule : srules) {
            String rule = srule.toString();
            String update = rule.replaceAll(pattern, replacement);
            if (!rule.equals(update)) {
                if (update.isBlank()) {
                    rulesremoved.add(srule);
                } else {
                    srule.replace(update);
                }
                rulesreplaced.add(srule);
            }
        }
        for (Style sr : rulesremoved) {
            srules.remove(sr);
        }
        return rulesreplaced;
    }

    public StyleAttribute setStyle(Element element) {
        String stylevalue = toString();
        if (stylevalue.isBlank()) {
            element.removeAttribute("style");
        } else {
            element.setAttribute("style", toString());
        }
        return this;
    }

    public StyleAttribute removeTargetStyleRuleIfNotPresent(StyleAttribute target) {
        List<Style> toberemoved = new ArrayList<>();
        for (var crule : target.srules) {
            if (!crule.isSame(lookup(crule.getName()))) {
                toberemoved.add(crule);
            }
        }
        for (var sr : toberemoved) {
            target.srules.remove(sr);
        }
        return this;
    }

    public StyleAttribute removeTargetStyleRuleIfPresent(StyleAttribute target) {
        List<Style> toberemoved = new ArrayList<>();
        for (var trule : target.srules) {
            if (trule.isSame(lookup(trule.getName()))) {
                toberemoved.add(trule);
            }
        }
        for (var sr : toberemoved) {
            target.srules.remove(sr);
        }
        return this;
    }

    public String toString() {
        String res = "";
        for (var srule : srules) {
            res = res + srule.toString();
        }
        return res;
    }
}
