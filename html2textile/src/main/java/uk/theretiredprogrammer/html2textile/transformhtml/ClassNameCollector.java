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

import java.util.List;
import org.w3c.dom.Element;

public class ClassNameCollector implements TransformHtmlItem {

    private final List<String> collector;

    public ClassNameCollector(List<String> collector) {
        this.collector = collector;
    }

    public ResumeAction testElementAndModify(Element element) {
        String classnames = element.getAttribute("class");
        if (!classnames.isBlank()) {
            for (String classname : classnames.split(" ")) {
                if (!classname.isBlank()) {
                    collector.add(classname);
                }
            }
        }
        return ResumeAction.RESUME_FROM_NEXT;
    }
}
