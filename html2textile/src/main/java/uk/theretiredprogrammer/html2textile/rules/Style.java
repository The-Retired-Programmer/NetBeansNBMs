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

public class Style {

    private final List<StyleRule> srules = new ArrayList<>();

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
                srules.add(new StyleRule(srule));
            }
        }
        return true;
    }
    
    public final List<StyleRule> getStyleRules() {
        return srules;
    }

    public boolean isSame(String[] comparisonrules) throws IOException {
        if (comparisonrules.length == srules.size()) {
            for (String comparisonrule : comparisonrules) {
                StyleRule crule = new StyleRule(comparisonrule);
                if (!crule.isSame(lookup(crule.getName()))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public boolean contains(StyleRule find) {
        return find.isSame(lookup(find.getName()));
    }

    public boolean isEmpty() {
        return srules.isEmpty();
    }

    public StyleRule lookup(String name) {
        for (StyleRule rule : srules) {
            if (rule.getName().equals(name)) {
                return rule;
            }
        }
        return null;
    }

    public Style insertStyleRule(String name, String value) throws IOException {
        return insertStyleRule(new StyleRule(name, value));
    }

    public Style insertStyleRule(StyleRule rule) {
        srules.add(rule);
        return this;
    }

    public Style insertStyle(Style styletoadd) {
        srules.addAll(styletoadd.srules);
        return this;
    }

    public StyleRule removeStyleRuleIfName(String name) {
        StyleRule found = lookup(name);
        srules.remove(found);
        return found;
    }
    
    public StyleRule removeStyleRule(StyleRule remove) {
        StyleRule found = lookup(remove.getName());
        if (remove.isSame(found)) {
            srules.remove(found);
        }
        return found;
    }
    
    public StyleRule removeThisStyleRule(StyleRule remove) {
            srules.remove(remove);
        return remove;
    }


    public List<StyleRule> removeStyleRuleIfPattern(String pattern) {
        List<StyleRule> rulestoremove = new ArrayList<>();
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

    public StyleRule replaceStyleRuleValue(String namematch, String valuereplacement) {
        StyleRule found = lookup(namematch);
        if (found == null) {
            return null;
        }
        found.setValue(valuereplacement);
        return found;
    }

    public List<StyleRule> replaceStyleRuleUsingPattern(String pattern, String replacement) throws IOException {
        List<StyleRule> rulesreplaced = new ArrayList<>();
        List<StyleRule> rulesremoved = new ArrayList<>();
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
        for (StyleRule sr : rulesremoved) {
            srules.remove(sr);
        }
        return rulesreplaced;
    }

    public Style setStyle(Element element) {
        String stylevalue = toString();
        if (stylevalue.isBlank()) {
            element.removeAttribute("style");
        } else {
            element.setAttribute("style", toString());
        }
        return this;
    }

    public Style removeTargetStyleRuleIfNotPresent(Style target) {
        List<StyleRule> toberemoved = new ArrayList<>();
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
    
    public Style removeTargetStyleRuleIfPresent(Style target) {
        List<StyleRule> toberemoved = new ArrayList<>();
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
