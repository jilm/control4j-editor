/*
 *  Copyright 2015 Jiri Lidinsky
 *
 *  This file is part of control4j.
 *
 *  control4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  control4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with control4j.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.lidinsky.editor.actions;

import cz.lidinsky.editor.Action;
import cz.lidinsky.editor.Editor;
import cz.lidinsky.editor.DataModel;
import cz.lidinsky.editor.ComponentFactory;

import cz.lidinsky.tools.tree.Node;

import control4j.gui.components.Screen;
import control4j.gui.VisualContainer;
import control4j.gui.VisualObject;
import control4j.gui.GuiObject;

import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


public class DeleteSelection extends Action
  implements javax.swing.event.TreeSelectionListener {

  public DeleteSelection() {
    super();
    Editor.getComponentTree().addTreeSelectionListener(this);
  }

  public void valueChanged(javax.swing.event.TreeSelectionEvent event) {
    int selectionCount = Editor.getComponentTree().getSelectionCount();
    setEnabled(selectionCount > 0);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    List<Node<GuiObject>> selection
      = Editor.getComponentTree().getSelectedNodes();
    for (Node<GuiObject> node : selection) {
      Editor.getDataModel().removeNode(node);
    }
  }

}
