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
import java.util.Arrays;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import org.w3c.dom.NodeList;

public class RestructureTable extends DomModifications {

    @Override
    public ResumeAction testElementAndModify(Element element) throws IOException {
        if (element.getTagName().equals("table")) {
            reorganiseChildElements(element);
            insertcolgroup(element);

            return ResumeAction.RESUME_FROM_NEXT;
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }

    private void reorganiseChildElements(Element table) throws IOException {
        int theadpos = 0;
        Element thead = null;
        int tfootpos = 0;
        Element tfoot = null;
        Element tbody = null;
        int pos = 1;
        Node node = table.getFirstChild();
        if (node == null) {
            throw new IOException("Error - Empty table element");
        }
        do {
            if (node.getNodeType() == ELEMENT_NODE) {
                String tagname = node.getNodeName();
                switch (node.getNodeName()) {
                    case "tbody" -> {
                        if (tbody == null) {
                            tbody = (Element) node;
                        }
                        pos++;
                    }
                    case "thead" -> {
                        thead = (Element) node;
                        theadpos = pos++;
                    }
                    case "tfoot" -> {
                        tfoot = (Element) node;
                        tfootpos = pos++;
                    }
                    case "line" -> {
                    }
                    default ->
                        throw new IOException("unexpected element (" + tagname + ") in a table");
                }
            }
            node = node.getNextSibling();
        } while (node != null);
        if (tbody == null) {
            throw new IOException("Error - missing tbody element within table");
        }
        // analyse the column setup
        analysecolumns(thead, tbody);
        // now do the reorganisation
        //first is thead (or extract first row if missing)
        if (thead != null && theadpos != 1) {
            table.insertBefore(thead, table.getFirstChild());
            tfootpos++;
        } else {
            if (thead == null) {
                Element firsttr = extractfirsttablerow(tbody);
                table.insertBefore(renameasthead(firsttr), table.getFirstChild());
                tfootpos++;
            }
        }
        //second is tfoot
        if (tfoot != null && tfootpos != 2) {
            table.insertBefore(tfoot, table.getFirstChild().getNextSibling());
        }
    }

    private Element extractfirsttablerow(Element tbody) throws IOException {
        Node node = tbody.getFirstChild();
        if (node == null) {
            throw new IOException("Error - tbody element within table does not contain any rows");
        }
        do {
            if (node.getNodeType() == ELEMENT_NODE) {
                switch (node.getNodeName()) {
                    case "tr" -> {
                        tbody.removeChild(node);
                        return (Element) node;
                    }
                    case "line" -> {
                    }
                    default ->
                        throw new IOException("Error - first tbody element within table is not a tablerow");
                }
            }
            node = node.getNextSibling();
        } while (node != null);
        throw new IOException("Error - tbody element within table does not contain a tablerow");
    }

    private Element renameasthead(Element firsttr) {
        Element thead = createElement("thead", firsttr);
        Element theadtr = createElement("tr",firsttr);
        appendAttributes(theadtr,firsttr.getAttributes());
        appendChild(thead, theadtr);
        
        NodeList children = firsttr.getChildNodes();
        if (children != null) {
            if (children.getLength() != 0) {
                Node child = children.item(0);
                while (child != null) {
                    Node nextchild = child.getNextSibling();
                    if (child.getNodeType() == ELEMENT_NODE && child.getNodeName().equals("td")) {
                        Element th = createElement("th", firsttr);
                        appendAttributes(th, child.getAttributes());
                        appendChildren(th,child.getChildNodes());
                        appendChild(theadtr, th);
                    }
                    child = nextchild;
                }
            }
        }
        return thead;
    }

    private void insertcolgroup(Element table) {
        Element colgroup = createElement("colgroup", table);
        appendAttributes(colgroup, new Attribute[]{new Attribute("style", "width:" + 100 / colcount + "%;")});
        insertBeforeNode(table.getFirstChild(),colgroup);
        for (int i = 0; i < colcount; i++) {
            Element col = createElement("col", colgroup);
            appendAttributes(col,buildcommonstyleattribute(stylerules[i]));
            appendChild(colgroup, col);
        }
    }

    // results of the column analysis
    private int colcount = 0;
    private List<String>[] stylerules;

    private void analysecolumns(Element thead, Element tbody) {
        // column count
        int theadcolcount = 0;
        if (thead != null) {
            theadcolcount = countcolumns(thead, "th");
        }
        int tbodycolcount = countcolumns(tbody, "td");
        colcount = theadcolcount > tbodycolcount ? theadcolcount : tbodycolcount;
        // common style rules
        stylerules = new List[colcount];
        for (int i = 0; i < colcount; i++) {
            stylerules[i] = new ArrayList<>();
        }
        extractcommonstylerules(tbody, stylerules, colcount);
        removecommonstylerulesfromtd(tbody, stylerules, colcount);

    }

    private int countcolumns(Element element, String tagname) {
        int maxcount = 0;
        NodeList records = element.getElementsByTagName("tr");
        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            int count = recordcolumncount(record, tagname);
            if (count > maxcount) {
                maxcount = count;
            }
        }
        return maxcount;
    }

    private int recordcolumncount(Element record, String tagname) {
        NodeList columns = record.getElementsByTagName(tagname);
        int count = 0;
        for (int i = 0; i < columns.getLength(); i++) {
            Element column = (Element) columns.item(i);
            count += getcolspan(column);
        }
        return count;
    }

    private int getcolspan(Element element) {
        String colspan = element.getAttribute("colspan");
        return colspan.isEmpty() ? 1 : Integer.parseUnsignedInt(colspan);
    }

    private int getrowspan(Element element) {
        String rowspan = element.getAttribute("rowspan");
        return rowspan.isEmpty() ? 1 : Integer.parseUnsignedInt(rowspan);
    }

    private void extractcommonstylerules(Element element, List<String>[] stylerules, int colcount) {
        int[] rowspancounters = new int[colcount];
        NodeList records = element.getElementsByTagName("tr");
        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            NodeList columns = record.getElementsByTagName("td");
            int tdcount = 0;
            for (int j = 0; j < colcount;) {
                if (rowspancounters[j] > 0) {
                    rowspancounters[j++]--;
                } else {
                    Element column = (Element) columns.item(tdcount);
                    int rowspan = getrowspan(column);
                    int colspan = getcolspan(column);
                    while (colspan > 0) {
                        if (i == 0) {
                            loadstylerules(column, j, stylerules);
                        } else {
                            reducestylerules(column, j, stylerules);
                        }
                        rowspancounters[j] = rowspan - 1;
                        j++;
                        colspan--;
                    }
                    tdcount++;
                    if (tdcount >= columns.getLength()) {
                        break;
                    }
                }
            }
        }
    }

    private void removecommonstylerulesfromtd(Element element, List<String>[] stylerules, int colcount) {
        int[] rowspancounters = new int[colcount];
        NodeList records = element.getElementsByTagName("tr");
        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            NodeList columns = record.getElementsByTagName("td");
            int tdcount = 0;
            for (int j = 0; j < colcount;) {
                if (rowspancounters[j] > 0) {
                    rowspancounters[j++]--;
                } else {
                    Element column = (Element) columns.item(tdcount);
                    int rowspan = getrowspan(column);
                    int colspan = getcolspan(column);
                    if (colspan == 1) { // dont remove when colspans actually defined
                        for (String rule : stylerules[j]) {
                            removestylerule(column, rule);
                        }
                        rowspancounters[j++] = rowspan - 1;
                    } else {
                        while (colspan > 0) {
                            rowspancounters[j++] = rowspan - 1;
                            colspan--;
                        }
                    }
                    tdcount++;
                    if (tdcount >= columns.getLength()) {
                        break;
                    }
                }
            }
        }
    }

    private void removestylerule(Element td, String rule) {
        String style = td.getAttribute("style");
        if (style.isBlank()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        String[] rules = style.split(";");
        for (int i = 0; i < rules.length; i++) {
            if (rule.equals(rules[i])) {
                while (++i < rules.length) {
                    sb.append(rules[i]);
                    sb.append(';');
                }
                String newvalue = sb.toString();
                if (newvalue.isBlank()) {
                    td.removeAttribute("style");
                } else {
                    td.setAttribute("style", sb.toString());
                }
                return;
            }
            sb.append(rules[i]);
            sb.append(';');
        }
    }

    private Attribute[] buildcommonstyleattribute(List<String> stylerules) {
        if (stylerules.isEmpty()) {
            return new Attribute[0];
        }
        StringBuilder sb = new StringBuilder();
        for (String rule : stylerules) {
            if (!rule.isBlank()) {
                sb.append(rule);
                sb.append(';');
            }
        }
        String stylevalue = sb.toString();
        return stylevalue.isBlank() ? new Attribute[0] : new Attribute[]{new Attribute("style", stylevalue)};
    }

    private void loadstylerules(Element column, int colno, List<String>[] stylerules) {
        stylerules[colno].addAll(Arrays.asList(column.getAttribute("style").split(";")));
    }

    private void reducestylerules(Element column, int colno, List<String>[] stylerules) {
        String[] attributerules = column.getAttribute("style").split(";");
        List<String> removethese = new ArrayList<>();
        for (String storedrule : stylerules[colno]) {
            if (!isdefined(storedrule, attributerules)) {
                removethese.add(storedrule);
            }
        }
        for (String removethis : removethese) {
            stylerules[colno].remove(removethis);
        }
    }

    private boolean isdefined(String value, String[] lookup) {
        for (String lookupvalue : lookup) {
            if (value.equals(lookupvalue)) {
                return true;
            }
        }
        return false;
    }
}
