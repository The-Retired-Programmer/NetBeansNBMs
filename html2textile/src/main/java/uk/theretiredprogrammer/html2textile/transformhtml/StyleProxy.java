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
import org.w3c.dom.Element;
import uk.theretiredprogrammer.html2textile.rules.Proxy;
import uk.theretiredprogrammer.html2textile.rules.Rule;
import uk.theretiredprogrammer.html2textile.rules.RuleSet;

public class StyleProxy extends RuleSet<StyleProxy> implements Proxy<Element, Boolean> {

    private Element element;
    private String[] stylerules;

    public Boolean applyRules(Element proxyvalue, boolean ignoresystemrules) throws IOException {
        element=proxyvalue;
        return applyRuleActions(this, ignoresystemrules);
        // will need a complete here when style object is used
    }

    private boolean replace(String match, String replacement) {
        if (extractstylerules()) {
            for (int i = 0; i< stylerules.length; i++) {
                if (stylerules[i].startsWith(match + ":")) {
                    stylerules[i] = replacement;
                }
            }
            updatestyle();
        }
        return false;
    }

    private boolean replaceAll(String pattern, String replacement) {
        if (extractstylerules()) {
            for (int i = 0; i< stylerules.length; i++) {
                if (stylerules[i].matches(pattern)) {
                    stylerules[i] = replacement;
                }
            }
            updatestyle();
        }
        return false;
    }

    private boolean movePatternToAttribute(String pattern) {
        if (extractstylerules()) {
            for (int i = 0; i< stylerules.length; i++) {
                if (stylerules[i].matches(pattern)) {
                    setattribute(stylerules[i]);
                    stylerules[i] = "";
                }
            }
            updatestyle();
        }
        return false;
    }

    private boolean moveToAttribute(String match) {
        if (extractstylerules()) {
            for (int i = 0; i< stylerules.length; i++) {
                if (stylerules[i].startsWith(match + ":")) {
                    setattribute(stylerules[i]);
                    stylerules[i] = "";
                }
            }
            updatestyle();
        }
        return false;
    }

    private boolean extractstylerules() {
        String style = element.getAttribute("style");
        if (style.isEmpty()) {
            return false;
        }
        stylerules = style.split(";");
        for (int i = 0; i< stylerules.length; i++) {
            stylerules[i]=stylerules[i].trim();
        }
        return true;
    }

    private void setattribute(String stylerule) {
        String[] parts = stylerule.split(":");
        element.setAttribute(parts[0].trim(), parts[1].trim());
    }

    private void updatestyle() {
        StringBuilder sb = new StringBuilder();
        for (String stylerule : stylerules) {
            if (!stylerule.isBlank()) {
                sb.append(stylerule.trim());
                sb.append(';');
            }
        }
        String newstyle = sb.toString();
        if (newstyle.isBlank()) {
            element.removeAttribute("style");
        } else {
            element.setAttribute("style", newstyle);
        }
    }

    public void parseAndInsertRule(String rulecommandline, boolean isSystemRule) throws IOException {
        String match;
        String replacement;
        rulecommandline = rulecommandline.trim();
        if (rulecommandline.startsWith("REMOVE PATTERN ")) {
            match = trimquotes(rulecommandline.substring(14).trim());
            add(new Rule<>(isSystemRule, (e) -> e.replaceAll(match, "")));
            return;
        }
        if (rulecommandline.startsWith("REMOVE ")) {
            match = trimquotes(rulecommandline.substring(6).trim());
            add(new Rule<>(isSystemRule, (e) -> e.replace(match, "")));
            return;
        }
        if (rulecommandline.startsWith("REPLACE PATTERN ")) {
            int withpos = rulecommandline.indexOf(" WITH ");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE PATTERN \" rule - " + rulecommandline);
            }
            match = trimquotes(rulecommandline.substring(15, withpos + 1).trim());
            replacement = trimquotes(rulecommandline.substring(withpos + 5).trim());
            add(new Rule<>(isSystemRule, (e) -> e.replaceAll(match, replacement)));
            return;
        }
        if (rulecommandline.startsWith("REPLACE ")) {
            int withpos = rulecommandline.indexOf(" WITH ");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE \" rule - " + rulecommandline);
            }
            match = trimquotes(rulecommandline.substring(7, withpos + 1).trim());
            replacement = trimquotes(rulecommandline.substring(withpos + 5).trim());
            add(new Rule<>(isSystemRule, (e) -> e.replace(match, replacement)));
            return;
        }
        if (rulecommandline.startsWith("MOVE PATTERN ")) {
            int withpos = rulecommandline.indexOf(" TO ATTRIBUTE");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" TO ATTRIBUTE\" missing in \"MOVE PATTERN \" rule - " + rulecommandline);
            }
            match = trimquotes(rulecommandline.substring(13, withpos + 1).trim());
            add(new Rule<>(isSystemRule, (e) -> e.movePatternToAttribute(match)));
            return;
        }
        if (rulecommandline.startsWith("MOVE ")) {
            int withpos = rulecommandline.indexOf(" TO ATTRIBUTE");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" TO ATTRIBUTE\" missing in \"MOVE \" rule - " + rulecommandline);
            }
            match = trimquotes(rulecommandline.substring(5, withpos + 1).trim());
            add(new Rule<>(isSystemRule, (e) -> e.moveToAttribute(match)));
            return;
        }
        throw new IOException("Bad Rule definition: unknown command - " + rulecommandline);
    }
}
