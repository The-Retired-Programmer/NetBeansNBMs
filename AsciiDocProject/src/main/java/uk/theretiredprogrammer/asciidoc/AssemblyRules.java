/*
 * Copyright 2022 richard linsdale.
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
package uk.theretiredprogrammer.asciidoc;

import java.util.Properties;

public class AssemblyRules {

    public final String keyword;
    public final String from;
    public final String to;
    
    public AssemblyRules(String keyword, Properties properties) {
        this.keyword = keyword;
        from = properties.getProperty(keyword+"from", keyword);
        to = properties.getProperty(keyword+"to", from);
    }
    
}
