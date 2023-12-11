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
import java.util.List;
import org.w3c.dom.Element;
import uk.theretiredprogrammer.html2textile.rules.Proxy;
import uk.theretiredprogrammer.html2textile.rules.Rule;
import uk.theretiredprogrammer.html2textile.rules.RuleSet;
import uk.theretiredprogrammer.html2textile.rules.StyleAttribute;
import uk.theretiredprogrammer.html2textile.rules.Style;

public class StylesToClassProxy extends RuleSet<StylesToClassProxy> implements Proxy<Element, Boolean> {

    private Element element;
    private StyleAttribute style;
    private IOException error = null;

    public Boolean applyRules(Element proxyvalue) throws IOException {
        error = null;
        element = proxyvalue;
        style = new StyleAttribute(element);
        boolean res = applyRuleActions(this);
        style.setStyleAttribute(element);
        if (error != null) {
            throw error;
        }
        return res;
    }
    
    private boolean replaceexactmatch(String[] match, String classname, String elementname) {
        return element.getTagName().equals(elementname) ? replaceexactmatch(match, classname) : false;
    }

    private boolean replaceexactmatch(String[] match, String classname) {
        List<Style> toremove = new ArrayList<>();
        Style[] srules = new Style[match.length];
        for (int i = 0; i < match.length; i++) {
            try {
                srules[i] = new Style(match[i]);
            } catch (IOException ex) {
                error = ex;
                return true;
            }
        }
        int matchcount = 0;
        for (var srule : srules) {
            Style instyle = style.lookup(srule.getName());
            if (srule.isSame(instyle)) {
                toremove.add(instyle);
                matchcount++;
            }
        }
        if (matchcount == match.length) {
            String classnames = element.getAttribute("class");
            element.setAttribute("class", classnames.isBlank() ? classname : classnames + " " + classname);
            for (var instyle : toremove) {
                style.removeThisStyle(instyle);
            }
        }
        return false;
    }
    
    private boolean replacepartialmatch(String[] match, String classname, String elementname) {
        return element.getTagName().equals(elementname) ? replacepartialmatch(match, classname) : false;
    }

    private boolean replacepartialmatch(String[] match, String classname) {
        Style[] srules = new Style[match.length];
        for (int i = 0; i < match.length; i++) {
            try {
                srules[i] = new Style(match[i]);
            } catch (IOException ex) {
                error = ex;
                return true;
            }
        }
        int matchcount = 0;
        for (var srule : srules) {
            Style instyle = style.lookup(srule.getName());
            if (srule.isSame(instyle)) {
                matchcount++;
            }
        }
        if (matchcount == match.length) {
            String classnames = element.getAttribute("class");
            element.setAttribute("class", classnames.isBlank() ? classname : classnames + " " + classname);
            style.clear();
        }
        return false;
    }

    public void parseAndInsertRule(String rulecommandline) throws IOException {
        String styles;
        String classname;
        String elementname;
        rulecommandline = rulecommandline.trim();
        if (rulecommandline.startsWith("REPLACE EXACT MATCH OF STYLES ")) {
            int withpos = rulecommandline.indexOf(" WITH CLASS ");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" WITH CLASS \" missing in \"REPLACE EXACT MATCH OF STYLES \" rule - " + rulecommandline);
            }
            int ifpos = rulecommandline.indexOf(" IF ELEMENT ");
            if (ifpos == -1) {
                styles = trimquotes(rulecommandline.substring(29, withpos + 1).trim());
                classname = trimquotes(rulecommandline.substring(withpos + 12).trim());
                add(new Rule<>((e) -> e.replaceexactmatch(styles.split(" AND "), classname)));
                return;
            } else {
                styles = trimquotes(rulecommandline.substring(29, withpos + 1).trim());
                classname = trimquotes(rulecommandline.substring(withpos + 12, ifpos + 1).trim());
                elementname = trimquotes(rulecommandline.substring(ifpos + 12).trim());
                add(new Rule<>((e) -> e.replaceexactmatch(styles.split(" AND "), classname, elementname)));
                return;
            }
        }
        if (rulecommandline.startsWith("REPLACE PARTIAL MATCH OF STYLES ")) {
            int withpos = rulecommandline.indexOf(" WITH CLASS ");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" WITH CLASS \" missing in \"REPLACE PARTIAL MATCH OF STYLES \" rule - " + rulecommandline);
            }
            int ifpos = rulecommandline.indexOf(" IF ELEMENT ");
            if (ifpos == -1) {
                styles = trimquotes(rulecommandline.substring(31, withpos + 1).trim());
                classname = trimquotes(rulecommandline.substring(withpos + 12).trim());
                add(new Rule<>((e) -> e.replacepartialmatch(styles.split(" AND "), classname)));
                return;
            } else {
                styles = trimquotes(rulecommandline.substring(31, withpos + 1).trim());
                classname = trimquotes(rulecommandline.substring(withpos + 12, ifpos + 1).trim());
                elementname = trimquotes(rulecommandline.substring(ifpos + 12).trim());
                add(new Rule<>((e) -> e.replacepartialmatch(styles.split(" AND "), classname, elementname)));
                return;
            }
        }
        throw new IOException("Bad Rule definition: unknown command - " + rulecommandline);
    }
}
