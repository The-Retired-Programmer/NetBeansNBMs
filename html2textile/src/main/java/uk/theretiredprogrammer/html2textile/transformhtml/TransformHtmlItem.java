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

public interface TransformHtmlItem {

    public enum ResumeAction {
        RESUME_FROM_ROOT, RESUME_FROM_SELF, RESUME_FROM_PARENT, RESUME_FROM_NEXT, RESUME_FROM_PREVIOUS, RESUME_FROM_FIRST_SIBLING
    };

    public ResumeAction testElementAndModify(Element element) throws IOException;
}
