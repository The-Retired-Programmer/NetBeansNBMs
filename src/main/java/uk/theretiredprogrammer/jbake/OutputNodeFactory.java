/*
 * Copyright 2018-2019 richard.
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
package uk.theretiredprogrammer.extexp;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 * A Factory to create the Output Node (the output folder in the project).
 *
 * @author richard
 */
@NodeFactory.Registration(projectType = "uk-theretiredprogrammer-extexp", position = 50)
public class OutputNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        PProject p = project.getLookup().lookup(PProject.class);
        assert p != null;
        return new OutputNodeList(p);
    }

    private class OutputNodeList implements NodeList<Node> {

        PProject project;

        public OutputNodeList(PProject project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            FileObject outFolder
                    = project.getProjectDirectory().getFileObject("output");
            List<Node> result = new ArrayList<>();
            if (outFolder != null) {
                try {
                    Node node = DataObject.find(outFolder).getNodeDelegate();
                    result.add(new OutputNode(node));
                } catch (DataObjectNotFoundException ex) {
                    // do nothing at the moment - just ignore node and squash the Exception
                }
            }
            return result;
        }

        public class OutputNode extends FilterNode {

            @StaticResource()
            public static final String FOLDER_ICON = "uk/theretiredprogrammer/jbake/folder.png";

            public OutputNode(Node onode) {
                super(onode);
            }

            @Override
            public String getHtmlDisplayName() {
                return "Output";
            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage(FOLDER_ICON);
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public Action[] getActions(boolean arg0) {
                return new Action[]{
                };
            }
        }

        @Override
        public Node node(Node node) {
            return new FilterNode(node);
        }

        @Override
        public void addNotify() {
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }
    }
}
