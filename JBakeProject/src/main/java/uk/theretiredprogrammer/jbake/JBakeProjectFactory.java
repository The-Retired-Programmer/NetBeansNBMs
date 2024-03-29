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
package uk.theretiredprogrammer.jbake;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;
import uk.theretiredprogrammer.util.ActivitiesAndActionsFactory;

@ServiceProvider(service = ProjectFactory.class)
public class JBakeProjectFactory implements ProjectFactory {

    @Override
    public boolean isProject(FileObject projectDirectory) {
        boolean isA3present = ActivitiesAndActionsFactory.IsActivitiesAndActionsAvailable();
        FileObject jbakepropertiesfile = projectDirectory.getFileObject("jbake.properties");
        return isA3present && jbakepropertiesfile != null && jbakepropertiesfile.isData();
    }

    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject(dir) ? new JBakeProject(dir, state) : null;
    }

    @Override
    public void saveProject(final Project project) throws IOException, ClassCastException {
        // leave unimplemented for the moment
    }
}
