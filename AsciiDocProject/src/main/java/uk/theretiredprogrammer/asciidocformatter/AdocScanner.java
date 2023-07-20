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

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import uk.theretiredprogrammer.asciidocformatter.AdocBlock.AdocBlockType;
import uk.theretiredprogrammer.util.ApplicationException;

public class AdocScanner {

    private Element root;
    private int count;
    private int next;
    private String linetext;
    private AdocLineType linetype;
    private int linestartoffset;
    private int lineendoffset;
    private StyledDocument document;

    public AdocBlock extractBlock(StyledDocument document, int mark) throws ApplicationException {
        initscanner(document);
        while (true) {
            AdocBlock block = getNextBlock();
            if (block == null) {
                throw new ApplicationException("Fail: can't locate start location");
            }
            if (block.startoffset <= mark && block.endoffset > mark) {
                return block;
            }
        }
    }

    public List<AdocBlock> extractBlocks(StyledDocument document, int frommark, int tomark) throws ApplicationException {
        List<AdocBlock> blocks = new ArrayList<>();
        AdocBlock recent = extractBlock(document, frommark);
        blocks.add(recent);
        if (recent.startoffset <= tomark && recent.endoffset > tomark) {
            return blocks;
        }
        while (true) {
            AdocBlock block = getNextBlock();
            if (block == null) {
                throw new ApplicationException("Fail: can't locate end location");
            }
            blocks.add(block);
            if (block.startoffset <= tomark && block.endoffset > tomark) {
                return blocks;
            }
        }
    }

    private void initscanner(StyledDocument document) {
        this.document = document;
        root = document.getDefaultRootElement();
        count = root.getElementCount();
        next = 0;
    }

    private boolean getnextline() throws ApplicationException {
        if (next >= count) {
            return false;
        }
        Element line = root.getElement(next++);
        linestartoffset = line.getStartOffset();
        lineendoffset = line.getEndOffset();
        try {
            linetext = document.getText(linestartoffset, lineendoffset - linestartoffset);
        } catch (BadLocationException ex) {
            throw new ApplicationException("Fail: can't extract text by location", ex);
        }
        linetype = getlinetype(linetext);
        return true;
    }

    private void backupscanner() {
        if (next > 0) {
            next--;
        }
    }

    private AdocLineType getlinetype(String linetext) throws ApplicationException {
        if (linetext.replaceAll("^\\h*", "").equals("\n")) {
            return AdocLineType.EMPTYLINE;
        }
        if (linetext.endsWith(" +\n")) {
            return AdocLineType.ENDLINE;
        }
        if (linetext.equals("+\n")) {
            return AdocLineType.CONTINUATION;
        }
        if (linetext.startsWith("* ") || linetext.startsWith(". ") || linetext.startsWith("<") || linetext.startsWith("=")) {
            return AdocLineType.STARTLINE;
        }
        if (linetext.startsWith(":") /*|| linetext.startsWith("[") */) {
            return AdocLineType.COMMANDLINE;
        }
        if (linetext.equals("----\n")) {
            return AdocLineType.BLOCKBRACKETLINE;
        }
        return AdocLineType.LINE;
    }

    private enum AdocLineType {
        STARTLINE, LINE, ENDLINE, EMPTYLINE, COMMANDLINE, BLOCKBRACKETLINE, CONTINUATION
    };

    private enum ParseState {
        OUTSIDEPARA, INSIDEPARA, INSIDEBLOCK
    }

    private StringBuilder blockbuilder;
    private ParseState state = ParseState.OUTSIDEPARA;
    private int blockstartoffset;
    private int blockendoffset;

    private void addlinetoblocktext(ParseState nextstate) {
        addlinetoblocktext();
        state = nextstate;
    }

    private void addlinetoblocktext() {
        blockbuilder.append(linetext);
        blockendoffset = lineendoffset;
    }

    private AdocBlock addlinetoblocktextandcomplete(AdocBlockType blocktype) {
        addlinetoblocktext();
        return new AdocBlock(blocktype, blockstartoffset, blockendoffset, blockbuilder.toString());
    }

    private AdocBlock complete(AdocBlockType blocktype) {
        backupscanner();
        return new AdocBlock(blocktype, blockstartoffset, blockendoffset, blockbuilder.toString());
    }

    private AdocBlock getNextBlock() throws ApplicationException {
        blockbuilder = new StringBuilder();
        state = ParseState.OUTSIDEPARA;
        while (getnextline()) {
            switch (state) {
                case OUTSIDEPARA:
                    blockstartoffset = linestartoffset;
                    switch (linetype) {
                        case STARTLINE:
                        case LINE:
                            addlinetoblocktext(ParseState.INSIDEPARA);
                            break;
                        case CONTINUATION:
                            return addlinetoblocktextandcomplete(AdocBlockType.CONTINUATION);
                        case ENDLINE:
                            return addlinetoblocktextandcomplete(AdocBlockType.NEWLINE);
                        case EMPTYLINE:
                            return addlinetoblocktextandcomplete(AdocBlockType.NEWLINE);
                        case COMMANDLINE:
                            return addlinetoblocktextandcomplete(AdocBlockType.COMMANDLINE);
                        case BLOCKBRACKETLINE:
                            addlinetoblocktext(ParseState.INSIDEBLOCK);
                            break;
                    }
                    break;
                case INSIDEPARA:
                    switch (linetype) {
                        case STARTLINE:
                        case CONTINUATION:
                            return complete(AdocBlockType.NEWLINE);
                        case LINE:
                            addlinetoblocktext();
                            break;
                        case ENDLINE:
                            return addlinetoblocktextandcomplete(AdocBlockType.NEWLINE);
                        case EMPTYLINE:
                            return addlinetoblocktextandcomplete(AdocBlockType.PARAGRAPH);
                        case COMMANDLINE:
                            return complete(AdocBlockType.NEWLINE);
                        case BLOCKBRACKETLINE:
                            throw new ApplicationException("Fail: block bracket (----) should not be within a paragraph");
                    }
                    break;
                case INSIDEBLOCK:
                    switch (linetype) {
                        case STARTLINE:
                        case CONTINUATION:
                        case LINE:
                        case ENDLINE:
                        case EMPTYLINE:
                        case COMMANDLINE:
                            addlinetoblocktext();
                            break;
                        case BLOCKBRACKETLINE:
                            return addlinetoblocktextandcomplete(AdocBlockType.BLOCK);
                    }
                    break;
            }
        }
        // do end of document actions
        switch (state) {
            case OUTSIDEPARA:
                return null;
            case INSIDEPARA:
                return addlinetoblocktextandcomplete(AdocBlockType.NEWLINE);
            case INSIDEBLOCK:
                throw new ApplicationException("Fail: block bracket (----) unterminated at end of file");
        }
        throw new ApplicationException("Fail: illegal state in getNextBlock in AdocParse");
    }
}
