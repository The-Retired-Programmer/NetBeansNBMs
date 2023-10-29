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
package uk.theretiredprogrammer.html2textile;

import java.util.function.Consumer;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ErrHandler {

    private final Consumer<String> errmessagewriter;

    private int currentline = 0;

    public ErrHandler(Consumer<String> errorMessageWriter) {
        this.errmessagewriter = errorMessageWriter;
    }

    public void setlinenumber(int linenumber) {
        currentline = linenumber;
    }

    public void info(String message) {
        errmessagewriter.accept(message);
    }

    public void warning(String message) {
        errmessagewriter.accept(warningmsgbuilder(message, (Element) null));
    }

    public void warning(String message, Element element) {
        errmessagewriter.accept(warningmsgbuilder(message, element));
    }

    public void error(String message) {
        errmessagewriter.accept(errmsgbuilder(message, (Element) null));
    }

    public void error(String message, Element element) {
        errmessagewriter.accept(errmsgbuilder(message, element));
    }

    public void exception(Throwable ex) {
        errmessagewriter.accept(errmsgbuilder("", ex));
    }

    public void exception(String message, Throwable ex) {
        errmessagewriter.accept(errmsgbuilder(message, ex));
    }

    private String warningmsgbuilder(String message, Element element) {
        return "Warning: " + message + contextmessage(element);
    }

    private String errmsgbuilder(String message, Element element) {
        return "Error: " + message + contextmessage(element);
    }

    private String errmsgbuilder(String message, Throwable ex) {
        return "Exception: " + message + contextmessage(ex);
    }

    private final int MAXCHILDCONTENTPRINTED = 60;

    private String contextmessage(Element element) {
        StringBuilder sb = new StringBuilder();
        if (currentline > 0) {
            sb.append(" - on line ");
            sb.append(currentline);
        }
        if (element != null) {
            sb.append("\n    ");
            NamedNodeMap attributes = element.getAttributes();
            sb.append(element.getTagName());
            sb.append(" ");
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                sb.append(attr.getNodeName());
                sb.append("=");
                sb.append(attr.getNodeValue());
                if (i != attributes.getLength() - 1) {
                    sb.append(", ");
                }
            }
            String childcontent = element.getTextContent();
            if (childcontent.length() > MAXCHILDCONTENTPRINTED) {
                childcontent = childcontent.substring(0, MAXCHILDCONTENTPRINTED - 3) + "...";
            }
            sb.append(" \"");
            sb.append(childcontent);
            sb.append("\"");
        }
        return sb.toString();
    }

    private String contextmessage(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        if (currentline > 0) {
            sb.append(" - on line ");
            sb.append(currentline);
        }
        String throwablemessage = ex.getLocalizedMessage();
        if (throwablemessage != null) {
            sb.append(" - ");
            sb.append(throwablemessage);
        }
        return sb.toString();
    }
}
