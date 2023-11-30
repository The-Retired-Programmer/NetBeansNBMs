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

public class URLProxy extends RuleSet<URLProxy> implements Proxy<Element, Boolean> {

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

    private boolean mapa(String from, String to) {
        if ("a".equals(element.getTagName()) && from.equals(element.getAttribute("href"))) {
            element.setAttribute("href", to);
        }
        return false;
    }
    
    private boolean mapapattern(String pattern, String replacement) {
        if ("a".equals(element.getTagName()) ) {
            element.setAttribute("href", element.getAttribute("href").replaceAll(pattern, replacement));
        }
        return false;
    }
    
    private boolean mapimg(String from, String to) {
        if ("img".equals(element.getTagName()) && from.equals(element.getAttribute("src"))) {
            element.setAttribute("src", to);
        }
        return false;
    }
    
    private boolean mapimgpattern(String pattern, String replacement) {
        if ("img".equals(element.getTagName()) ) {
            element.setAttribute("scr", element.getAttribute("src").replaceAll(pattern, replacement));
        }
        return false;
    }
    
    public void parseAndInsertRule(String rulecommandline, boolean isSystemRule) throws IOException {
        String map;
        String target;
        rulecommandline = rulecommandline.trim();
        if (rulecommandline.startsWith("MAP A PATTERN ")) {
            int topos = rulecommandline.indexOf(" TO ");
            if (topos == -1) {
                throw new IOException("Bad Rule definition: \" TO \" missing in \"MAP A PATTERN \" rule - " + rulecommandline);
            }
            map = trimquotes(rulecommandline.substring(13, topos + 1).trim());
            target = trimquotes(rulecommandline.substring(topos + 3).trim());
            add(new Rule<>(isSystemRule, (e) -> e.mapapattern(map, target)));
            return;
        }
        if (rulecommandline.startsWith("MAP A ")) {
            int topos = rulecommandline.indexOf(" TO ");
            if (topos == -1) {
                throw new IOException("Bad Rule definition: \" TO \" missing in \"MAP A \" rule - " + rulecommandline);
            }
            map = trimquotes(rulecommandline.substring(5, topos + 1).trim());
            target = trimquotes(rulecommandline.substring(topos + 3).trim());
            add(new Rule<>(isSystemRule, (e) -> e.mapa(map, target)));
            return;
        }
        if (rulecommandline.startsWith("MAP IMG PATTERN ")) {
            int topos = rulecommandline.indexOf(" TO ");
            if (topos == -1) {
                throw new IOException("Bad Rule definition: \" TO \" missing in \"MAP IMG PATTERN \" rule - " + rulecommandline);
            }
            map = trimquotes(rulecommandline.substring(15, topos + 1).trim());
            target = trimquotes(rulecommandline.substring(topos + 3).trim());
            add(new Rule<>(isSystemRule, (e) -> e.mapimgpattern(map, target)));
            return;
        }
        if (rulecommandline.startsWith("MAP IMG ")) {
            int topos = rulecommandline.indexOf(" TO ");
            if (topos == -1) {
                throw new IOException("Bad Rule definition: \" TO \" missing in \"MAP IMG \" rule - " + rulecommandline);
            }
            map = trimquotes(rulecommandline.substring(7, topos + 1).trim());
            target = trimquotes(rulecommandline.substring(topos + 3).trim());
            add(new Rule<>(isSystemRule, (e) -> e.mapimg(map, target)));
            return;
        }
        throw new IOException("Bad Rule definition: unknown command - " + rulecommandline);
    }
}
