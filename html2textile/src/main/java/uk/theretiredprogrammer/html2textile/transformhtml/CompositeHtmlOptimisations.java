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

public class CompositeHtmlOptimisations implements TransformHtmlItem {

    private final TransformHtmlItem[] items = new TransformHtmlItem[]{
        new NullSpanRemoval(),
        new AttributeMerge(),
        new NullAttributeRemoval(),
        new BlankInlineElementRemoval(),
        new EmptyLiRemoval(),
        new EmptyListRemoval(),
        new MergeLiAndFollowingBlockElement(),
        new RestuctureLeadingAndTrailingWhiteSpaceFromBracketingElements(),
        new BlockElementTrailingSpaceRemoval(),
        new ListConcatonation(),
        new RemoveTrailingBr(),
        new EmptyParaRemoval(),
        new MergeTextSegments()
    };

    public ResumeAction testElementAndModify(Element element) throws IOException {
        for (var item : items) {
            ResumeAction res = item.testElementAndModify(element);
            if (!res.equals(ResumeAction.RESUME_FROM_NEXT)) {
                return res;
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
