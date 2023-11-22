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

public class ElementProxy extends RuleSet<ElementProxy> implements Proxy<Element, Boolean> {

    private Element element;

    public Boolean applyRules(Element proxyvalue, boolean ignoresystemrules) throws IOException {
        element=proxyvalue;
        return applyRuleActions(this, ignoresystemrules);
        // will need a complete here when a style object is used
    }

    public boolean replace(String tagname, String newtagname) {
        if (tagname.equals(element.getTagName())) {
            Element newelement = DomHelper.createElement(newtagname, element);
            DomHelper.appendAttributes(newelement, element.getAttributes());
            DomHelper.appendChildren(newelement, element.getChildNodes());
            DomHelper.replaceNode(element, newelement);
            return true;
        }
        return false;
    }

    public boolean remove(String tagname) {
        if (tagname.equals(element.getTagName())) {
            DomHelper.insertBeforeNode(element, element.getChildNodes());
            DomHelper.removeNode(element);
            return true;
        }
        return false;
    }

    public boolean removeincludingcontent(String tagname) {
        if (tagname.equals(element.getTagName())) {
            DomHelper.removeNode(element);
            return true;
        }
        return false;
    }

    public void parseAndInsertRule(String rulecommandline, boolean isSystemRule) throws IOException {
        String tagname;
        String newtagname;
        rulecommandline = rulecommandline.trim();
        if (rulecommandline.startsWith("REPLACE ")) {
            int withpos = rulecommandline.indexOf(" WITH ");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE \" rule - " + rulecommandline);
            }
            tagname = trimquotes(rulecommandline.substring(7, withpos + 1).trim());
            newtagname = trimquotes(rulecommandline.substring(withpos + 5).trim());
            add(new Rule<>(isSystemRule, (e) -> e.replace(tagname, newtagname)));
            return;
        }
        if (rulecommandline.startsWith("REMOVE ")) {
            int includingpos = rulecommandline.indexOf(" INCLUDING CONTENT");
            if (includingpos == -1) {
                tagname = trimquotes(rulecommandline.substring(6).trim());
                add(new Rule<>(isSystemRule, (e) -> e.remove(tagname)));
                return;
            }
            tagname = trimquotes(rulecommandline.substring(6, includingpos + 1).trim());
            add(new Rule<>(isSystemRule, (e) -> e.removeincludingcontent(tagname)));
            return;
        }
        throw new IOException("Bad Rule definition: unknown command - " + rulecommandline);
    }
}