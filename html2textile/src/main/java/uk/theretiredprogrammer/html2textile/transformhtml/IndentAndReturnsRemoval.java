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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class IndentAndReturnsRemoval extends DomModifications {
    
    public static void run(TransformHtml transformer) {
        DomModifications rules = new IndentAndReturnsRemoval();
        transformer.transform(rules);
    }

    @Override
    public Outcome testElementAndModify(Element element, int level){
        return Outcome.CONTINUE_SWEEP;
    }
    
    @Override
    public Outcome testTextAndModify(Node textnode, int level){
        if (isFilterable(textnode.getNodeValue())) {
            removeNode(textnode);
            return Outcome.RESTART_SWEEP_FROM_PARENT;
        }
        return Outcome.CONTINUE_SWEEP;
    }

    private boolean isFilterable(String text) {
        return text.contains("\n")
                ? text.replace("\n", " ").replace("\t", " ").strip().equals("")
                : false;
    }
}
