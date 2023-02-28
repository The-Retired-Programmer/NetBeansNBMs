/*
 * Copyright 2023 richard linsdale.
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
package uk.theretiredprogrammer.epubconversion;

import java.io.IOException;
import java.util.List;

public class HintsAnalyser {

    public static String analyseParaRules(List<CSSRule> rules) throws IOException {
        String style = "";
        for (var rule : rules) {
            switch (rule.key) {
                case "margin":
                    if (areVerticalMarginsNonZero(rule.value)) {
                        style += new CSSRule("margin", "6pt 0px 6pt 0px").get();
                    } else {
                        style += new CSSRule("margin", "none").get();
                    }
                    break;
                case "text-align":
                    switch (rule.value) {
                        case "center":
                            style += rule.get();
                            break;
                        case "right":
                            style += rule.get();
                            break;
                    }
            }
        }
        return style;
    }

    public static String analyseHeadingsRules(List<CSSRule> rules) throws IOException {
        return "";
    }

    public static String analyseBodyRules(List<CSSRule> rules) throws IOException {
        return "";
    }

    public static String analyseSpanRules(List<CSSRule> rules) throws IOException {
        CSSRule colour = null;
        CSSRule underline = null;
        String style = "";
        for (var rule : rules) {
            switch (rule.key) {
                case "font-size":
                    if (isPointSizeValueGreaterThan(rule.value, 12.0f)) {
                        style += new CSSRule("heading", "h3").get();
                    }
                    break;
                case "color":
                    colour = rule;
                    break;
                case "text-decoration":
                    if ("underline".equals(rule.value)) {
                        underline = rule;
                    }
                    break;
                case "font-style":
                    if ("italic".equals(rule.value)) {
                        style += rule.get();
                    }
                    break;
                case "font-weight":
                    if ("bold".equals(rule.value)) {
                        style += rule.get();
                    }
            }
        }
        if (colour != null) {
            if (underline != null) {
                // do nothing it probably a link
            } else {
                style += colour.get(); // colour && ¬ underine
            }
        } else {
            if (underline != null) {
                style += underline.get(); // underline && ¬ colour
            }
        }
        return style;
    }

    public static String analyseFrameRules(List<CSSRule> rules) throws IOException {
        String style = "";
        for (var rule : rules) {
            switch (rule.key) {
                case "width":
                    int val = getmmValue(rule.value);
                    if (val > 110) {
                        style += "width: 100%; ";
                    } else if (val > 55) {
                        style += "width: 50%; ";
                    } else {
                        style += "width: 25%; ";
                    }
            }
        }
        return style;
    }

    public static String analyseCellTableRules(List<CSSRule> rules) throws IOException {
        String style = "";
        boolean visibleborderseen = false;
        for (var rule : rules) {
            switch (rule.key) {
                case "width":
                    int val = getmmValue(rule.value);
                    float fraction = val / 159.f;
                    int percentage = Math.round(fraction * 100);
                    CSSRule percentagerule = new CSSRule("width", Integer.toString(percentage) + "%");
                    style += percentagerule.get();
                    break;
                case "background-color":
                    style += rule.get();
                    break;
                case "border":
                case "border-top":
                case "border-bottom":
                case "border-left":
                case "border-right":
                    visibleborderseen |= !rule.value.equals("none");
                    break;
            }
        }
        if (!visibleborderseen) {
            CSSRule bordernone = new CSSRule("border", "none");
            style += bordernone.get();
        }
        return style;
    }

    public static String analyseRowTableRules(List<CSSRule> rules) throws IOException {
        return "";
    }

    public static String analyseTableRules(List<CSSRule> rules) throws IOException {
        String style = "";
        for (var rule : rules) {
            switch (rule.key) {
                case "width":
                    int val = getmmValue(rule.value);
                    if (val > 110) {
                        style += "width: 100%; ";
                    } else if (val > 55) {
                        style += "width: 50%; ";
                    } else {
                        style += "width: 25%; ";
                    }
            }
        }
        return style;
    }

    private static int getmmValue(String value) throws IOException {
        if (value.endsWith("in")) {
            String val = value.substring(0, value.length() - 2);
            Float ins = Float.parseFloat(val);
            return Math.round(ins * 25.4f);
        }
        if (value.endsWith("%")) {
            String val = value.substring(0, value.length() - 1);
            Float percentage = Float.parseFloat(val);
            return Math.round(percentage * 6.26f * 25.4f / 100.0f);
        }

        throw new IOException("width can only accept 'in' or '%' units");
    }

    private static boolean areVerticalMarginsNonZero(String parameter) throws IOException {
        String[] values = parameter.trim().split(" ");
        if (values.length != 4) {
            throw new IOException("margin parameters in correct - should have 4 components");
        }
        return isNonZero(values[0]) || isNonZero(values[2]);
    }

    private static boolean isNonZero(String val) {
        return val.contains("1") || val.contains("2") || val.contains("3") || val.contains("4")
                || val.contains("5") || val.contains("6") || val.contains("7")
                || val.contains("8") || val.contains("9");
    }

    private static boolean isPointSizeValueGreaterThan(String parameter, float lowerlimit) {
        String val = parameter.trim();
        if (!val.endsWith("pt")) {
            return false; // not handling non pointsize units
        }
        float decval = Float.parseFloat(val.substring(0, val.length() - 2));
        return decval > lowerlimit;
    }

}
