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


public class AddComponent extends Action
  implements javax.swing.event.TreeSelectionListener {

  public AddComponent() {
    super();
    Editor.getComponentTree().addTreeSelectionListener(this);
  }

  public void valueChanged(javax.swing.event.TreeSelectionEvent event) {
    setEnabled(check());
  }

  protected boolean check() {
    try {
      Node<GuiObject> selected = Editor.getComponentTree().getSelectedNode();
      return true;
    } catch (IllegalStateException e) {
      return false;
    }
  }

  @Override
  public void actionPerformed(ActionEvent event) {

    // Is the editor in the appropriate mode ?
    if (check()) {
      String componentName = Editor.getInstance().letSelectComponent();
      // create an instance of selected component
      GuiObject component
        = ComponentFactory.getInstance()
        .createInstance(componentName);
      // add the component to the appropriate place
      Editor.getDataModel().addChild(
          Editor.getComponentTree().getSelectedNode(),
          component);
    }
  }

}
