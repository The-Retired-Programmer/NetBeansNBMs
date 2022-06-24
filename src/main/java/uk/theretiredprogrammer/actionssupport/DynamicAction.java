/*
 * Copyright 2015-2022 Richard Linsdale.
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
package uk.theretiredprogrammer.actionssupport;

import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.actionssupport.implementation.DynamicActionImplementation;

public class DynamicAction extends DynamicActionImplementation {
    

    public DynamicAction(FileObject dir, String label, String command) {
       super(dir,label,command);
    }

    public DynamicAction(FileObject dir) {
        super(dir);
    }

}
