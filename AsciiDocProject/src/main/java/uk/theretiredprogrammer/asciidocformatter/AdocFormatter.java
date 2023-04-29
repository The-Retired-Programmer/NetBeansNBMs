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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.theretiredprogrammer.util.ApplicationException;

public class AdocFormatter {

    public ReformattedBlocks reformattedSelected(AdocBlock block) throws ApplicationException {
        ReformattedBlocks reformatted = new ReformattedBlocks();
        reformatted.replacestartoffset = block.startoffset;
        reformatted.replaceendoffset = block.endoffset;
        reformat(reformatted.reformatedoutput, block);
        return reformatted;
    }

    public ReformattedBlocks reformattedSelected(List<AdocBlock> blocks) throws ApplicationException {
        ReformattedBlocks reformatted = new ReformattedBlocks();
        reformatted.replacestartoffset = blocks.get(0).startoffset;
        reformatted.replaceendoffset = blocks.get(blocks.size() - 1).endoffset;
        for (AdocBlock block : blocks) {
            reformat(reformatted.reformatedoutput, block);
        }
        return reformatted;
    }

    private void reformat(StringBuilder out, AdocBlock block) {
        switch (block.paragraphtype) {
            case COMMANDLINE:
            case BLOCK:
            case CONTINUATION:
                // no reformating
                out.append(block.content);
                break;
            case PARAGRAPH:
                reformatblock(block.content, out);
                out.append("\n");
                break;
            case NEWLINE:
                reformatblock(block.content, out);
        }
    }

    private final int MAXLINELENGTH = 80;
    private final boolean SENTENCEMODE = true;

    private void reformatblock(String intext, StringBuilder out) {
        intext = intext.replace("\n", " ").stripTrailing();
        if (SENTENCEMODE) {
            for (String sentence : sentenceSplit(intext)) {
                blockReformat(sentence, out);
            }
        } else {
            blockReformat(intext, out);
        }
    }

    List<String> sentenceSplit(String text) {
        List<String> sentences = new ArrayList<>();
        Pattern p = Pattern.compile("(.*?\\w+?.*?\\.\\h+)|(.+)");
        Matcher m = p.matcher(text);
        while (m.find()) {
            String s = m.group(1);
            if (s != null) {
                sentences.add(s.stripTrailing());
            }
            s = m.group(2);
            if (s != null) {
                sentences.add(s.stripTrailing());
            }
        }
        return sentences;
    }

    void blockReformat(String text, StringBuilder out) {
        int frompos = 0;
        int spacepos = 0;
        int nextpos = 0;
        int length = text.length();
        while (nextpos < length) {
            char c = text.charAt(nextpos++);
            if (c == ' ') {
                spacepos = nextpos;
            } else {
                if (nextpos - frompos > MAXLINELENGTH && spacepos > frompos) {
                    out.append(text.substring(frompos, spacepos).stripTrailing());
                    out.append('\n');
                    frompos = spacepos;
                }
            }
        }
        if (nextpos > frompos) {
            out.append(text.substring(frompos, nextpos).stripTrailing());
        }
        out.append("\n");
    }
}
