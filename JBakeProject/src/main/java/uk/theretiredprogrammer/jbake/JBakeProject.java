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

import java.awt.Image;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import uk.theretiredprogrammer.actionssupport.CLIExec;
import uk.theretiredprogrammer.actionssupport.DynamicAsyncAction;
import uk.theretiredprogrammer.actionssupport.NodeActions;

public class JBakeProject implements Project {

    private final FileObject projectDir;
    //private final ProjectState state;
    private Lookup lkp;
    private final NodeActions nodedynamicactionsmanager;

    /**
     * Constructor
     *
     * @param dir project root folder
     * @param state the project state
     */
    JBakeProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
        //this.state = state;
        nodedynamicactionsmanager = new NodeActions(dir, "projectactions", "Baking");
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
                new Info(),
                new LogicalView(this)
            });
        }
        return lkp;
    }

    private final class Info implements ProjectInformation {

        @StaticResource()
        public static final String JBAKE_ICON = "uk/theretiredprogrammer/jbake/jbake_logo.png";

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(JBAKE_ICON));
        }

        @Override
        public String getName() {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public Project getProject() {
            return JBakeProject.this;
        }
    }

    class LogicalView implements LogicalViewProvider {

        @StaticResource()
        public static final String JBAKE_ICON = "uk/theretiredprogrammer/jbake/jbake_logo.png";

        private final JBakeProject project;

        public LogicalView(JBakeProject project) {
            this.project = project;
        }

        @Override
        public Node createLogicalView() {
            try {
                //Obtain the project directory's node:
                FileObject projectDirectory = project.getProjectDirectory();
                DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
                Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
                //Decorate the project directory's node:
                return new ProjectNode(nodeOfProjectFolder, project);
            } catch (DataObjectNotFoundException donfe) {
                //Fallback-the directory couldn't be created -
                //read-only filesystem or something evil happened
                return new AbstractNode(Children.LEAF);
            }
        }

        private final class ProjectNode extends FilterNode {

            final JBakeProject project;

            public ProjectNode(Node node, JBakeProject project)
                    throws DataObjectNotFoundException {
                super(node,
                        //NodeFactorySupport.createCompositeChildren(
                        //        project,
                        //        "Projects/uk-theretiredprogrammer-jbake/Nodes"),
                        new FilterNode.Children(node),
                        new ProxyLookup(
                                new Lookup[]{
                                    Lookups.singleton(project),
                                    node.getLookup()
                                }));
                this.project = project;
                nodedynamicactionsmanager.setNodeBasicActions(
                        CommonProjectActions.renameProjectAction(),
                        CommonProjectActions.copyProjectAction(),
                        CommonProjectActions.closeProjectAction()
                );
                nodedynamicactionsmanager.setNodeActions(
                        new DynamicAsyncAction("Bake")
                                .onAction(() -> new CLIExec(projectDir,"jbake -b")
                                .stderrToOutputWindow()
                                .stdoutToOutputWindow()
                                .execute("Baking"))
                );
            }

            @Override
            public Action[] getActions(boolean arg0) {
                return nodedynamicactionsmanager.getAllNodeActions();
            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage(JBAKE_ICON);
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getDisplayName() {
                return project.getProjectDirectory().getName();
            }
        }

        @Override
        public Node findPath(Node root, Object target) {
            //leave unimplemented for now
            return null;
        }
    }
}
