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

    private final List<Style> styleslist = new ArrayList<>();

    public StyleAttribute() {
    }

    public StyleAttribute(Element element) throws IOException {
        loadStyles(element.getAttribute("style"));
    }

    public StyleAttribute(String styles) throws IOException {
        loadStyles(styles);
    }

    private void loadStyles(String styles) throws IOException {
        for (String style : styles.split(";")) {
            if (!style.isBlank()) {
                replaceoradd(new Style(style));
            }
        }
    }

    public final List<Style> getStyles() {
        return styleslist;
    }

    public boolean isSame(String[] comparisonstyles) throws IOException {
        if (comparisonstyles.length == styleslist.size()) {
            for (String comparisonstyle : comparisonstyles) {
                Style style = new Style(comparisonstyle);
                if (!style.isSame(lookup(style.getName()))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public StyleAttribute clear() {
        styleslist.clear();
        return this;
    }

    public boolean contains(Style find) {
        return find.isSame(lookup(find.getName()));
    }

    public boolean isEmpty() {
        return styleslist.isEmpty();
    }

    public Style lookup(String name) {
        for (Style rule : styleslist) {
            if (rule.getName().equals(name)) {
                return rule;
            }
        }
        return null;
    }

    public StyleAttribute insertStyle(String name, String value) throws IOException {
        return insertStyle(new Style(name, value));
    }

    public StyleAttribute insertStyle(Style style) {
        replaceoradd(style);
        return this;
    }

    private void replaceoradd(Style style) {
        Style found = lookup(style.getName());
        if (found != null) {
            styleslist.remove(found);
        }
        styleslist.add(style);
    }

    public StyleAttribute insertStyleAttribute(StyleAttribute styletoadd) {
        for (Style style : styletoadd.styleslist) {
            replaceoradd(style);
        }
        return this;
    }

    public Style removeStyleIfName(String name) {
        Style found = lookup(name);
        styleslist.remove(found);
        return found;
    }

    public Style removeStyle(Style remove) {
        Style found = lookup(remove.getName());
        if (remove.isSame(found)) {
            styleslist.remove(found);
        }
        return found;
    }

    public Style removeThisStyle(Style remove) {
        styleslist.remove(remove);
        return remove;
    }

    public List<Style> removeStyleIfPattern(String pattern) {
        List<Style> stylestoremove = new ArrayList<>();
        for (var style : styleslist) {
            String stylestring = style.toString();
            if (stylestring.matches(pattern)) {
                stylestoremove.add(style);
            }
        }
        for (var style : stylestoremove) {
            styleslist.remove(style);
        }
        return stylestoremove;
    }

    public Style replaceStyleValue(String namematch, String valuereplacement) {
        Style found = lookup(namematch);
        if (found == null) {
            return null;
        }
        found.setValue(valuereplacement);
        return found;
    }

    public Style replaceStyle(String original, String replacement) throws IOException {
        Style originalstyle = new Style(original);
        Style replacementstyle = new Style(replacement);
        Style found = lookup(originalstyle.getName());
        if (found == null) {
            return null;
        }
        styleslist.remove(found);
        styleslist.add(replacementstyle);
        return replacementstyle;
    }

    public List<Style> replaceStyleUsingPattern(String pattern, String replacement) throws IOException {
        List<Style> stylesreplaced = new ArrayList<>();
        List<Style> stylesremoved = new ArrayList<>();
        for (var style : styleslist) {
            String stylestring = style.toString();
            String update = stylestring.replaceAll(pattern, replacement);
            if (!stylestring.equals(update)) {
                if (update.isBlank()) {
                    stylesremoved.add(style);
                } else {
                    style.replace(update);
                }
                stylesreplaced.add(style);
            }
        }
        for (Style style : stylesremoved) {
            styleslist.remove(style);
        }
        return stylesreplaced;
    }

    public StyleAttribute setStyleAttribute(Element element) {
        String stylesvalue = toString();
        if (stylesvalue.isBlank()) {
            element.removeAttribute("style");
        } else {
            element.setAttribute("style", toString());
        }
        return this;
    }

    public StyleAttribute removeTargetStyleIfNotPresent(StyleAttribute target) {
        List<Style> toberemoved = new ArrayList<>();
        for (var targetstyle : target.styleslist) {
            if (!targetstyle.isSame(lookup(targetstyle.getName()))) {
                toberemoved.add(targetstyle);
            }
        }
        for (var style : toberemoved) {
            target.styleslist.remove(style);
        }
        return this;
    }

    public StyleAttribute removeTargetStyleIfPresent(StyleAttribute target) {
        List<Style> toberemoved = new ArrayList<>();
        for (var targetstyle : target.styleslist) {
            if (targetstyle.isSame(lookup(targetstyle.getName()))) {
                toberemoved.add(targetstyle);
            }
        }
        for (var style : toberemoved) {
            target.styleslist.remove(style);
        }
        return this;
    }

    public String toString() {
        String res = "";
        for (var srule : styleslist) {
            res = res + srule.toString();
        }
        return res;
    }
}
