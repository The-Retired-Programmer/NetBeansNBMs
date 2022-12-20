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
package uk.theretiredprogrammer.picoc;

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
import uk.theretiredprogrammer.actionssupport.DynamicAsyncAction;
import uk.theretiredprogrammer.actionssupport.SaveBeforeAction;
import uk.theretiredprogrammer.actionssupport.NodeActions;

public class PicoCProject implements Project {

    private final FileObject projectDir;
    //private final ProjectState state;
    private Lookup lkp;
    private final NodeActions nodeactionsmanager;
    private final PicoCPropertyFile picocproperties;

    /**
     * Constructor
     *
     * @param dir project root folder
     * @param state the project state
     */
    public PicoCProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
        //this.state = state;
        nodeactionsmanager = new NodeActions(dir, "projectactions");
        picocproperties = new PicoCPropertyFile(dir, nodeactionsmanager);
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

    public SaveBeforeAction getSaveBeforeAction() {
        return picocproperties.getSaveBeforeAction();
    }

//    public String getAsciiDoctorParameters() {
//        return "-R "+picocproperties.getSourceRootFolder()+" -D "+picocproperties.getGeneratedRootFolder()+" " ;
//    }
    public String getTabname() {
        return "Compile " + projectDir.getName();
    }

    private final class Info implements ProjectInformation {

        @StaticResource()
        public static final String ICON = "uk/theretiredprogrammer/picoc/folder_page_white.png";

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(ICON));
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
            return PicoCProject.this;
        }
    }

    class LogicalView implements LogicalViewProvider {

        @StaticResource()
        public static final String ICON = "uk/theretiredprogrammer/picoc/folder_page_white.png";

        private final PicoCProject project;

        public LogicalView(PicoCProject project) {
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

            final PicoCProject project;

            public ProjectNode(Node node, PicoCProject project)
                    throws DataObjectNotFoundException {
                super(node,
                        //NodeFactorySupport.createCompositeChildren(
                        //        project,
                        //        "Projects/uk-theretiredprogrammer-asciidoc/Nodes"),
                        new FilterNode.Children(node),
                        new ProxyLookup(
                                new Lookup[]{
                                    Lookups.singleton(project),
                                    node.getLookup()
                                }));
                this.project = project;
                nodeactionsmanager.setNodeBasicActions(
                        CommonProjectActions.renameProjectAction(),
                        CommonProjectActions.copyProjectAction(),
                        CommonProjectActions.closeProjectAction()
                );
                PicoCBuildWorkers workers = new PicoCBuildWorkers(projectDir.getName(), projectDir.getFileObject("build"));
                nodeactionsmanager.setNodeActions(
                        new DynamicAsyncAction("Clean").onAction(() -> workers.cleanBuildFolder()),
                        new DynamicAsyncAction("Build Make file").onAction(() -> workers.buildMakeFile()),
                        new DynamicAsyncAction("Build Executables").onAction(() -> workers.buildExecutables()),
                        new DynamicAsyncAction("Download using Debug Port").onAction(() -> workers.downloadViaDebug()),
                        new DynamicAsyncAction("Download using Bootloader").onAction(() -> workers.downloadViaBootLoader())
                );
            }

            @Override
            public Action[] getActions(boolean arg0) {
                return nodeactionsmanager.getAllNodeActions();
            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage(ICON);
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
