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
package uk.theretiredprogrammer.asciidocformatter;

import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import uk.theretiredprogrammer.util.ApplicationException;

public class AdocDocument {

    public void reformatSelectedBlock(StyledDocument document, int frommark) throws ApplicationException {
        AdocBlock block = new AdocScanner().extractBlock(document, frommark);
        ReformattedBlocks reformatted = new AdocFormatter().reformattedSelected(block);
        replaceindocument(document, reformatted);
    }

    public void reformatSelectedBlocks(StyledDocument document, int frommark, int tomark) throws ApplicationException {
        List<AdocBlock> blocks = new AdocScanner().extractBlocks(document, frommark, tomark);
        ReformattedBlocks reformatted = new AdocFormatter().reformattedSelected(blocks);
        replaceindocument(document, reformatted);
    }

    private void replaceindocument(StyledDocument document, ReformattedBlocks reformatted) throws ApplicationException {
        String insert = endofdocumentcorrection(document, reformatted);
        try {
            document.remove(reformatted.replacestartoffset, reformatted.replaceendoffset - reformatted.replacestartoffset);
            document.insertString(reformatted.replacestartoffset, insert, null);
        } catch (BadLocationException ex) {
            throw new ApplicationException("Fail: could not find the replacement location", ex);
        }
    }

    private String endofdocumentcorrection(StyledDocument document, ReformattedBlocks reformatted) {
        if (reformatted.replaceendoffset > document.getLength()) {
            reformatted.replaceendoffset = document.getLength();
            String insert = reformatted.reformatedoutput.toString();
            int insertlength = insert.length();
            return insert.substring(0, insertlength - 1);
        }
        return reformatted.reformatedoutput.toString();
    }
}
