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
import uk.theretiredprogrammer.html2textile.rules.Style;

public class AttributeProxy extends RuleSet<AttributeProxy> implements Proxy<Element, Boolean> {

    private Element element;
    private IOException exception;

    public Boolean applyRules(Element proxyvalue, boolean ignoresystemrules) throws IOException {
        element = proxyvalue;
        exception = null;
        boolean res = applyRuleActions(this, ignoresystemrules);
        if (exception != null) {
            throw exception;
        }
        return res;
    }

    private boolean movetostyle(String attributename) {
        String value = element.getAttribute(attributename);
        if (!value.isEmpty()) {
            try {
                element.removeAttribute(attributename);
                Style style = new Style();
                style.extract(element);
                style.insertStyleRule(attributename, value);
                style.setStyle(element);
                return true;
            } catch (IOException ex) {
                exception = ex;
                return true;
            }
        }
        return false;
    }

    private boolean remove(String attributename) {
        String value = element.getAttribute(attributename);
        if (!value.isEmpty()) {
            element.removeAttribute(attributename);
            return true;
        }
        return false;
    }

    private boolean removeif(String attributename, String test) {
        String value = element.getAttribute(attributename);
        if (!value.isEmpty() && value.equals(test)) {
            element.removeAttribute(attributename);
            return true;
        }
        return false;
    }

    private boolean removeifpattern(String attributename, String pattern) {
        String value = element.getAttribute(attributename);
        if (!value.isEmpty() && value.matches(pattern)) {
            element.removeAttribute(attributename);
            return true;
        }
        return false;
    }

    public void parseAndInsertRule(String rulecommandline, boolean isSystemRule) throws IOException {
        String attributename;
        String value;
        rulecommandline = rulecommandline.trim();
        if (rulecommandline.startsWith("MOVE ")) {
            int tostylepos = rulecommandline.indexOf(" TO STYLE");
            if (tostylepos == -1) {
                throw new IOException("Bad Rule definition: \" TO STYLE\" missing in \"MOVE \" rule - " + rulecommandline);
            }
            attributename = trimquotes(rulecommandline.substring(4, tostylepos + 1).trim());
            add(new Rule<>(isSystemRule, (e) -> e.movetostyle(attributename)));
            return;
        }
        if (rulecommandline.startsWith("REMOVE ")) {
            int pos = rulecommandline.indexOf(" IF PATTERN ");
            if (pos != -1) {
                attributename = trimquotes(rulecommandline.substring(6, pos + 1).trim());
                value = trimquotes(rulecommandline.substring(pos + 11).trim());
                add(new Rule<>(isSystemRule, (e) -> e.removeifpattern(attributename, value)));
                return;
            }
            pos = rulecommandline.indexOf(" IF ");
            if (pos != -1) {
                attributename = trimquotes(rulecommandline.substring(6, pos + 1).trim());
                value = trimquotes(rulecommandline.substring(pos + 3).trim());
                add(new Rule<>(isSystemRule, (e) -> e.removeif(attributename, value)));
                return;
            }
            attributename = trimquotes(rulecommandline.substring(6).trim());
            add(new Rule<>(isSystemRule, (e) -> e.remove(attributename)));
            return;
        }
        throw new IOException("Bad Rule definition: unknown command - " + rulecommandline);
    }
}
