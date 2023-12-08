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
import org.w3c.dom.Node;
import static org.w3c.dom.Node.ELEMENT_NODE;
import org.w3c.dom.NodeList;
import uk.theretiredprogrammer.html2textile.rules.StyleAttribute;

public class RestructureTable implements TransformHtmlItem {

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
                thead = renameasthead(firsttr);
                table.insertBefore(thead, table.getFirstChild());
                tfootpos++;
            }
        }
        //second is tfoot
        if (tfoot != null && tfootpos != 2) {
            table.insertBefore(tfoot, table.getFirstChild().getNextSibling());
        }
        // now analyse the style and extract common style to go into the colgroup/col
        
        extractcommonstylerules(tbody, colcount);
        removecommonstylerulesfromtd(tbody, colcount);
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
        Element thead = DomHelper.createElement("thead", firsttr);
        Element theadtr = DomHelper.createElement("tr", firsttr);
        DomHelper.appendAttributes(theadtr, firsttr.getAttributes());
        DomHelper.appendChild(thead, theadtr);

        NodeList children = firsttr.getChildNodes();
        if (children != null) {
            if (children.getLength() != 0) {
                Node child = children.item(0);
                while (child != null) {
                    Node nextchild = child.getNextSibling();
                    if (child.getNodeType() == ELEMENT_NODE && child.getNodeName().equals("td")) {
                        Element th = DomHelper.createElement("th", firsttr);
                        DomHelper.appendAttributes(th, child.getAttributes());
                        DomHelper.appendChildren(th, child.getChildNodes());
                        DomHelper.appendChild(theadtr, th);
                    }
                    child = nextchild;
                }
            }
        }
        return thead;
    }

    private void insertcolgroup(Element table) throws IOException {
        Element colgroup = DomHelper.createElement("colgroup", table);
        StyleAttribute styles = new StyleAttribute();
        styles.insertStyle("width", Integer.toString(100 / colcount) + "%");
        styles.setStyleAttribute(colgroup);
        DomHelper.insertBeforeNode(table.getFirstChild(), colgroup);
        for (int i = 0; i < colcount; i++) {
            Element col = DomHelper.createElement("col", colgroup);
            colstyles[i].setStyleAttribute(col);
            DomHelper.appendChild(colgroup, col);
        }
    }

    // results of the column analysis
    private int colcount = 0;
    private StyleAttribute[] colstyles;

    private void analysecolumns(Element thead, Element tbody) throws IOException {
        // column count
        int theadcolcount = 0;
        if (thead != null) {
            theadcolcount = countcolumns(thead, "th");
        }
        int tbodycolcount = countcolumns(tbody, "td");
        colcount = theadcolcount > tbodycolcount ? theadcolcount : tbodycolcount;
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

    private void extractcommonstylerules(Element element, int colcount) throws IOException {
        int[] rowspancounters = new int[colcount];
        colstyles = new StyleAttribute[colcount];
        for (int j = 0; j < colcount;j++) {
            colstyles[j] = new StyleAttribute();
        }
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
                            colstyles[j]= new StyleAttribute(column);
                        } else {
                            StyleAttribute newstyle = new StyleAttribute(column);
                            newstyle.removeTargetStyleIfNotPresent(colstyles[j]);
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

    private void removecommonstylerulesfromtd(Element element, int colcount) throws IOException {
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
                    if (colspan == 1) { 
                        StyleAttribute target = new StyleAttribute(column);
                        colstyles[j].removeTargetStyleIfPresent(target);
                        target.setStyleAttribute(column);
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
}
