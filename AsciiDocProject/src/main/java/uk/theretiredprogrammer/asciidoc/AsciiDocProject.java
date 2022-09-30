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

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.io.IOException;
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
import org.openide.filesystems.FileUtil;
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
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.actionssupport.CLIExec;
import uk.theretiredprogrammer.actionssupport.DynamicAsyncAction;
import uk.theretiredprogrammer.actionssupport.NodeActions;
import uk.theretiredprogrammer.asciidocfiles.TargetLocation;

public class AsciiDocProject implements Project {

    private final FileObject projectDir;
    //private final ProjectState state;
    private Lookup lkp;
    private final NodeActions nodedynamicactionsmanager;
    private final AsciiDocPropertyFile asciidocproperties;

    /**
     * Constructor
     *
     * @param dir project root folder
     * @param state the project state
     */
    AsciiDocProject(FileObject dir, ProjectState state) {
        this.projectDir = dir;
        //this.state = state;
        nodedynamicactionsmanager = new NodeActions(dir, "projectactions", "Build");
        asciidocproperties = new AsciiDocPropertyFile(dir, nodedynamicactionsmanager);
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
        public static final String ICON = "uk/theretiredprogrammer/asciidoc/folder_edit.png";

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
            return AsciiDocProject.this;
        }
    }

    class LogicalView implements LogicalViewProvider {

        @StaticResource()
        public static final String ICON = "uk/theretiredprogrammer/asciidoc/folder_edit.png";

        private final AsciiDocProject project;

        public LogicalView(AsciiDocProject project) {
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

            final AsciiDocProject project;

            public ProjectNode(Node node, AsciiDocProject project)
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
                nodedynamicactionsmanager.setNodeBasicActions(
                        CommonProjectActions.renameProjectAction(),
                        CommonProjectActions.copyProjectAction(),
                        CommonProjectActions.closeProjectAction()
                );
                nodedynamicactionsmanager.setNodeActions(
                        createBookAction(),
                        createArticleAction(),
                        createWebpageAction()
                );
            }

            private DynamicAsyncAction createBookAction() {
                return new DynamicAsyncAction("Assemble book")
                        .onAction(() -> assemblebook())
                        .enable(asciidocproperties.isBookAssembly());
            }

            private void assemblebook() {
                new CLIExec(projectDir,
                        "asciidoctor-pdf -d book -a toc -o generated_assemblies/" + asciidocproperties.bookto()
                        + ".pdf src/" + asciidocproperties.bookfrom() + ".adoc")
                        .stderrToOutputWindow()
                        .executeUsingOutputWindow("Assembling book - " + asciidocproperties.bookto() + ".pdf");
            }

            private DynamicAsyncAction createArticleAction() {
                return new DynamicAsyncAction("Assemble article")
                        .onAction(() -> assemblearticle())
                        .enable(asciidocproperties.isArticleAssembly());
            }

            private void assemblearticle() {
                new CLIExec(projectDir,
                        "asciidoctor-pdf -d article -o generated_assemblies/" + asciidocproperties.articleto()
                        + ".pdf src/" + asciidocproperties.articlefrom() + ".adoc")
                        .stderrToOutputWindow()
                        .executeUsingOutputWindow("Assembling article - " + asciidocproperties.articleto() + ".pdf");
            }

            private DynamicAsyncAction createWebpageAction() {
                return new DynamicAsyncAction("Assemble webpage")
                        .onAction(() -> assemblewebpage())
                        .enable(asciidocproperties.isWebpageAssembly());
            }

            private void assemblewebpage() {
                new CLIExec(projectDir,
                        "asciidoctor -d article -a toc2 -o generated_assemblies/" + asciidocproperties.webpageto()
                        + ".html src/" + asciidocproperties.webpagefrom() + ".adoc")
                        .stderrToOutputWindow()
                        .postprocessing((errwtr) -> copyAssemblyResources(projectDir, errwtr))
                        .executeUsingOutputWindow("Assembling webpage - " + asciidocproperties.webpageto() + ".html");
            }

            private void copyAssemblyResources(FileObject projectDir, OutputWriter errwtr) {
                try {
                    FileObject fo = TargetLocation.openorcreatefolder(projectDir, "generated_assemblies");
                    FileObject resourcesfo = TargetLocation.openorcreatefolder(fo, "resources");
                    emptyFolder(resourcesfo);
                    copyfromfolderrecursively(projectDir.getFileObject("src"), "resources", resourcesfo);
                } catch (IOException ex) {
                    errwtr.println("Error when copying assembly resources: " + ex.getLocalizedMessage());
                }

            }

            private void emptyFolder(FileObject folder) throws IOException {
                if (folder.isFolder()) {
                    for (FileObject fo : folder.getChildren()) {
                        fo.delete();
                    }
                }
            }

            private void copyfromfolder(FileObject fromfolder, FileObject tofolder) throws IOException {
                if (fromfolder.isFolder()) {
                    for (FileObject fo : fromfolder.getChildren()) {
                        FileUtil.copyFile(fo, tofolder, fo.getName());
                    }
                }
            }

            private void copyfromfolderrecursively(FileObject fromfolder, String foldername, FileObject tofolder) throws IOException {
                if (!fromfolder.isFolder()) {
                    return;
                }
                if (fromfolder.getName().equals(foldername)) {
                    copyfromfolder(fromfolder, tofolder);
                    return;
                }
                for (FileObject fo : fromfolder.getChildren()) {
                    copyfromfolderrecursively(fo, foldername, tofolder);
                }
            }

            @Override
            public Action[] getActions(boolean arg0) {
                return nodedynamicactionsmanager.getAllNodeActions();
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
