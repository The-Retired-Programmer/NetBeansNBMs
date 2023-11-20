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
package uk.theretiredprogrammer.html2textile.transformtext;

import java.io.IOException;
import uk.theretiredprogrammer.html2textile.rules.Rule;
import uk.theretiredprogrammer.html2textile.rules.RuleSet;

public class StringProxy extends RuleSet<StringProxy> {

    private String text;

    public void set(String text) {
        this.text = text;
    }

    public void replace(String match, String replacement) {
        text = text.replace(match, replacement);
    }

    public void replaceAll(String match, String replacement) {
        text = text.replaceAll(match, replacement);
    }

    public String get() {
        return text;
    }

    public void parseAndInsertRule(String rulecommandline, boolean isSystemRule) throws IOException {
        String match;
        String replacement;
        rulecommandline = rulecommandline.trim();
        if (rulecommandline.startsWith("REMOVE PATTERN ")) {
            match = trimquotes(rulecommandline.substring(14).trim());
            replacement = "";
            add(new Rule<>(isSystemRule, (t) -> {
                t.replaceAll(match, replacement);
                return false;
            }));
            return;
        }
        if (rulecommandline.startsWith("REMOVE ")) {
            match = trimquotes(rulecommandline.substring(6).trim());
            replacement = "";
            add(new Rule<>(isSystemRule, (t) -> {
                t.replace(match, replacement);
                return false;
            }));
            return;
        }
        if (rulecommandline.startsWith("REPLACE PATTERN ")) {
            int withpos = rulecommandline.indexOf(" WITH ");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE PATTERN \" rule - " + rulecommandline);
            }
            match = trimquotes(rulecommandline.substring(15, withpos + 1).trim());
            replacement = trimquotes(rulecommandline.substring(withpos + 5).trim());
            add(new Rule<>(isSystemRule, (t) -> {
                t.replaceAll(match, replacement);
                return false;
            }));
            return;
        }
        if (rulecommandline.startsWith("REPLACE ")) {
            int withpos = rulecommandline.indexOf(" WITH ");
            if (withpos == -1) {
                throw new IOException("Bad Rule definition: \" WITH \" missing in \"REPLACE \" rule - " + rulecommandline);
            }
            match = trimquotes(rulecommandline.substring(7, withpos + 1).trim());
            replacement = trimquotes(rulecommandline.substring(withpos + 5).trim());
            add(new Rule<>(isSystemRule, (t) -> {
                t.replace(match, replacement);
                return false;
            }));
            return;
        }
        throw new IOException("Bad Rule definition: unknown command - " + rulecommandline);
    }
}
