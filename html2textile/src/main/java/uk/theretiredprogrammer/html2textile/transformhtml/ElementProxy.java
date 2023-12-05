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
import uk.theretiredprogrammer.html2textile.rules.StyleRule;

public class ElementProxy extends RuleSet<ElementProxy> implements Proxy<Element, Boolean> {

    private Element element;
    private IOException error;

    public Boolean applyRules(Element proxyvalue) throws IOException {
        element = proxyvalue;
        error = null;
        boolean res = applyRuleActions(this);
        if (error != null) {
            throw error;
        }
        return res;
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

    public boolean replace(String tagname, String newtagname, String newstylerule) {
        if (tagname.equals(element.getTagName())) {
            try {
                Element newelement = DomHelper.createElement(newtagname, element);
                DomHelper.appendAttributes(newelement, element.getAttributes());
                DomHelper.insertIntoStyleAttribute(newelement, newstylerule);
                DomHelper.appendChildren(newelement, element.getChildNodes());
                DomHelper.replaceNode(element, newelement);
                return true;
            } catch (IOException ex) {
                error = ex;
                return true;
            }
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

    public boolean removeifnoattributes(String tagname) {
        if (tagname.equals(element.getTagName())) {
            if (!element.hasAttributes()) {
                DomHelper.insertBeforeNode(element, element.getChildNodes());
                DomHelper.removeNode(element);
                return true;
            }
        }
        return false;
    }

    public boolean removeifstyleempty(String tagname) {
        if (tagname.equals(element.getTagName())) {
            try {
                Style style = new Style();
                style.extract(element);
                if (style.isEmpty()) {
                    DomHelper.insertBeforeNode(element, element.getChildNodes());
                    DomHelper.removeNode(element);
                    return true;
                }
            } catch (IOException ex) {
                error = ex;
                return true;
            }
        }
        return false;
    }

    public boolean removeifstyles(String tagname, String[] stylerules) {
        if (tagname.equals(element.getTagName())) {
            try {
                Style style = new Style();
                style.extract(element);
                if (style.isSame(stylerules)) {
                    DomHelper.insertBeforeNode(element, element.getChildNodes());
                    DomHelper.removeNode(element);
                    return true;
                }
            } catch (IOException ex) {
                error = ex;
                return true;
            }
        }
        return false;
    }

    public boolean replaceifstyle(String tagname, String newtagname, String stylerule) {
        if (tagname.equals(element.getTagName())) {
            try {
                StyleRule find = new StyleRule(stylerule);
                Style style = new Style();
                style.extract(element);
                if (style.contains(find)) {
                    style.removeStyleRule(find);
                    style.setStyle(element);
                    if (style.isEmpty()) {
                        Element newelement = DomHelper.createElement(newtagname, element);
                        DomHelper.appendChildren(newelement, element.getChildNodes());
                        Element parent = (Element) element.getParentNode();
                        parent.replaceChild(newelement, element);
                    } else {
                        Element newelement = DomHelper.createElement(newtagname, element);
                        DomHelper.appendChildren(newelement, element.getChildNodes());
                        DomHelper.appendChild(element, newelement);
                    }
                    return true;
                }
            } catch (IOException ex) {
                error = ex;
                return true;
            }
        }
        return false;
    }

    public void parseAndInsertRule(String rulecommandline) throws IOException {
        String tagname;
        String newtagname;
        String newstyle;
        String[] stylerules;
        rulecommandline = rulecommandline.trim();
        if (rulecommandline.startsWith("REPLACE ")) {
            int stylepos = rulecommandline.indexOf(" AND STYLE ");
            int withpos = rulecommandline.indexOf(" WITH ");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE \" rule - " + rulecommandline);
            }
            if (stylepos == -1) {
                // this is REPLACE a WITH b
                tagname = trimquotes(rulecommandline.substring(7, withpos + 1).trim());
                newtagname = trimquotes(rulecommandline.substring(withpos + 5).trim());
                add(new Rule<>((e) -> e.replace(tagname, newtagname)));
                return;
            }
            if (stylepos < withpos) {
                // this is REPLACE a AND STYLE xxx WITH b
                tagname = trimquotes(rulecommandline.substring(7, stylepos + 1).trim());
                String stylerule = trimquotes(rulecommandline.substring(stylepos + 10, withpos + 1).trim());
                newtagname = trimquotes(rulecommandline.substring(withpos + 5).trim());
                add(new Rule<>((e) -> e.replaceifstyle(tagname, newtagname, stylerule)));
                return;
            } else {
                // this is REPLACE a WITH b AND STYLE xxxx
                tagname = trimquotes(rulecommandline.substring(7, withpos + 1).trim());
                newtagname = trimquotes(rulecommandline.substring(withpos + 5, stylepos + 1).trim());
                newstyle = trimquotes(rulecommandline.substring(stylepos + 10).trim());
                add(new Rule<>((e) -> e.replace(tagname, newtagname, newstyle)));
                return;
            }
        }
        if (rulecommandline.startsWith("REMOVE ")) {
            int ifpos = rulecommandline.indexOf(" IF NO ATTRIBUTES");
            if (ifpos != -1) {
                tagname = trimquotes(rulecommandline.substring(6, ifpos + 1).trim());
                add(new Rule<>((e) -> e.removeifnoattributes(tagname)));
                return;
            }
            ifpos = rulecommandline.indexOf(" IF STYLE IS EMPTY");
            if (ifpos != -1) {
                tagname = trimquotes(rulecommandline.substring(6, ifpos + 1).trim());
                add(new Rule<>((e) -> e.removeifstyleempty(tagname)));
                return;
            }
            ifpos = rulecommandline.indexOf(" IF STYLES ");
            if (ifpos != -1) {
                tagname = trimquotes(rulecommandline.substring(6, ifpos + 1).trim());
                stylerules = trimquotes(rulecommandline.substring(ifpos + 10).trim()).split(" AND ");
                add(new Rule<>((e) -> e.removeifstyles(tagname, stylerules)));
                return;
            }
            int includingpos = rulecommandline.indexOf(" INCLUDING CONTENT");
            if (includingpos != -1) {
                tagname = trimquotes(rulecommandline.substring(6, includingpos + 1).trim());
                add(new Rule<>((e) -> e.removeincludingcontent(tagname)));
                return;
            }
            tagname = trimquotes(rulecommandline.substring(6).trim());
            add(new Rule<>((e) -> e.remove(tagname)));
            return;

        }
        throw new IOException("Bad Rule definition: unknown command - " + rulecommandline);
    }
}
