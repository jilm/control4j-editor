package cz.lidinsky.editor;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

import java.util.ArrayList;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JComponent;
import javax.swing.tree.TreePath;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import control4j.gui.VisualObject;
import control4j.gui.GuiObject;
import cz.lidinsky.tools.tree.Node;

public class ComponentToTreeLink implements MouseListener {

  private JTree tree;

  public ComponentToTreeLink(JTree componentTree) {
    this.tree = componentTree;
  }

  public void mouseClicked(MouseEvent e) {
    TreeModel model = (TreeModel)tree.getModel();
    JComponent component = (JComponent)e.getComponent();
    Node<GuiObject> visualObject
      = (Node<GuiObject>)component.getClientProperty(DataModel.LINK_KEY);
    Object[] path = model.getPath(visualObject);
    tree.setSelectionPath(new TreePath(path));
  }

  public void mouseEntered(MouseEvent e) { }

  public void mouseExited(MouseEvent e) { }

  public void mousePressed(MouseEvent e) { }

  public void mouseReleased(MouseEvent e) { }

}
