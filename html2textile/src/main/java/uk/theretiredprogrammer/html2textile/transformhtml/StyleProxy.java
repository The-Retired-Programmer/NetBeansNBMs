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
import java.util.List;
import org.w3c.dom.Element;
import uk.theretiredprogrammer.html2textile.rules.Attribute;
import uk.theretiredprogrammer.html2textile.rules.Proxy;
import uk.theretiredprogrammer.html2textile.rules.Rule;
import uk.theretiredprogrammer.html2textile.rules.RuleSet;
import uk.theretiredprogrammer.html2textile.rules.Style;
import uk.theretiredprogrammer.html2textile.rules.StyleRule;

public class StyleProxy extends RuleSet<StyleProxy> implements Proxy<Element, Boolean> {

    private Element element;
    private Style style;
    private IOException error = null;

    public Boolean applyRules(Element proxyvalue) throws IOException {
        error = null;
        boolean res = false;
        element = proxyvalue;
        style = new Style();
        if (style.extract(element)) {
            res = applyRuleActions(this);
            style.setStyle(element);
        }
        if (error != null) {
            throw error;
        }
        return res;
    }

    private boolean remove(String stylerule) {
        try {
            style.removeStyleRule(new StyleRule(stylerule));
        } catch (IOException ex) {
            error = ex;
            return true;
        }
        return false;
    }

    private boolean removeAny(String stylerulename) {
        style.removeStyleRuleIfName(stylerulename);
        return false;
    }

    private boolean removeAll(String pattern) {
        style.removeStyleRuleIfPattern(pattern);
        return false;
    }

    private boolean replace(String namematch, String valuereplacement) {
        style.replaceStyleRuleValue(namematch, valuereplacement);
        return false;
    }

    private boolean replaceRule(String original, String replacement) {
        try {
            style.replaceStyleRule(original, replacement);
        } catch (IOException ex) {
            error = ex;
            return true;
        }
        return false;
    }

    private boolean replaceAll(String pattern, String replacement) {
        try {
            style.replaceStyleRuleUsingPattern(pattern, replacement);
        } catch (IOException ex) {
            error = ex;
            return true;
        }
        return false;
    }

    private boolean movePatternToAttribute(String pattern) {
        List<StyleRule> srules = style.removeStyleRuleIfPattern(pattern);
        for (StyleRule sr : srules) {
            Attribute attr = new Attribute(sr);
            setattribute(attr);
        }
        return false;
    }

    private boolean moveToAttribute(String rulematch) {
        StyleRule sr;
        try {
            sr = new StyleRule(rulematch);
        } catch (IOException ex) {
            error = ex;
            return true;
        }
        Attribute attr = new Attribute(sr);
        style.removeStyleRuleIfName(sr.getName());
        setattribute(attr);
        return false;
    }

    private void setattribute(Attribute attr) {
        element.setAttribute(attr.name, attr.value);
    }

    private boolean moveToElement(String rulematch, String tagname) {
        try {
            StyleRule find = new StyleRule(rulematch);
            if (style.contains(find)) {
                style.removeStyleRule(find);
                Element newelement = DomHelper.createElement(tagname, element);
                DomHelper.appendChildren(newelement, element.getChildNodes());
                DomHelper.appendChild(element, newelement);
                return true;
            }
            return false;
        } catch (IOException ex) {
            error = ex;
            return true;
        }
    }

    public void parseAndInsertRule(String rulecommandline) throws IOException {
        String match;
        String replacement;
        rulecommandline = rulecommandline.trim();
        if (rulecommandline.startsWith("REMOVE ANY ")) {
            match = trimquotes(rulecommandline.substring(10).trim());
            add(new Rule<>((e) -> e.removeAny(match)));
            return;
        }
        if (rulecommandline.startsWith("REMOVE PATTERN ")) {
            match = trimquotes(rulecommandline.substring(14).trim());
            add(new Rule<>((e) -> e.removeAll(match)));
            return;
        }
        if (rulecommandline.startsWith("REMOVE ")) {
            match = trimquotes(rulecommandline.substring(6).trim());
            add(new Rule<>((e) -> e.remove(match)));
            return;
        }
        if (rulecommandline.startsWith("REPLACE PATTERN ")) {
            int withpos = rulecommandline.indexOf(" WITH ");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE PATTERN \" rule - " + rulecommandline);
            }
            match = trimquotes(rulecommandline.substring(15, withpos + 1).trim());
            replacement = trimquotes(rulecommandline.substring(withpos + 5).trim());
            add(new Rule<>((e) -> e.replaceAll(match, replacement)));
            return;
        }
        if (rulecommandline.startsWith("REPLACE STYLE RULE ")) {
            int withpos = rulecommandline.indexOf(" WITH ");
            if (withpos != -1) {
                match = trimquotes(rulecommandline.substring(18, withpos + 1).trim());
                replacement = trimquotes(rulecommandline.substring(withpos + 5).trim());
                add(new Rule<>((e) -> e.replaceRule(match, replacement)));
                return;
            } else {
                throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE STYLE RULE\" rule - " + rulecommandline);
            }
        }
        if (rulecommandline.startsWith("REPLACE ")) {
            int withpos = rulecommandline.indexOf(" WITH ");
            if (withpos != -1) {
                match = trimquotes(rulecommandline.substring(7, withpos + 1).trim());
                replacement = trimquotes(rulecommandline.substring(withpos + 5).trim());
                add(new Rule<>((e) -> e.replace(match, replacement)));
                return;
            } else {
                throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE \" rule - " + rulecommandline);
            }
        }
        if (rulecommandline.startsWith("MOVE PATTERN ")) {
            int withpos = rulecommandline.indexOf(" TO ATTRIBUTE");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" TO ATTRIBUTE\" missing in \"MOVE PATTERN \" rule - " + rulecommandline);
            }
            match = trimquotes(rulecommandline.substring(13, withpos + 1).trim());
            add(new Rule<>((e) -> e.movePatternToAttribute(match)));
            return;
        }
        if (rulecommandline.startsWith("MOVE ")) {
            int withpos = rulecommandline.indexOf(" TO ATTRIBUTE");
            if (withpos != -1) {
                match = trimquotes(rulecommandline.substring(5, withpos + 1).trim());
                add(new Rule<>((e) -> e.moveToAttribute(match)));
                return;
            }
            withpos = rulecommandline.indexOf(" TO ELEMENT ");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" TO ELEMENT \" missing in \"MOVE \" rule - " + rulecommandline);
            }
            match = trimquotes(rulecommandline.substring(5, withpos + 1).trim());
            replacement = trimquotes(rulecommandline.substring(withpos + 11).trim());
            add(new Rule<>((e) -> e.moveToElement(match, replacement)));
            return;
        }
        throw new IOException("Bad Rule definition: unknown command - " + rulecommandline);
    }
}
