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

import control4j.gui.GuiObject;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;

import cz.lidinsky.tools.tree.Node;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.TreePath;

/**
 *
 */
public class Tree extends javax.swing.JTree
implements javax.swing.event.TableModelListener {

  /**
   *
   */
  public Tree() {
    super();
  }

  /**
   *
   */
  public Tree(javax.swing.tree.TreeModel model) {
    super(model);
  }

  /**
   *
   */
  @Override
  public String convertValueToText(
      Object value,
      boolean selected,
      boolean expanded,
      boolean leaf,
      int row,
      boolean hasFocus) {

    StringBuilder builder = new StringBuilder();
    GuiObject guiValue = ((Node<GuiObject>)value).getDecorated();
    // First character specifies the type of object
    if (guiValue instanceof control4j.gui.Screens)
      return "Root";
    else if (guiValue instanceof control4j.gui.VisualContainer)
      builder.append('P');
    else if (guiValue instanceof control4j.gui.VisualObject)
      builder.append('C');
    else if (guiValue instanceof control4j.gui.Changer)
      builder.append("Ch");
    else
      // should not happen
      assert false;
    // delimiter
    builder.append(':');
    // class name
    builder.append(guiValue.getClass().getSimpleName());
    // delimiter
    builder.append(':');
    // name of the component, if supported
    builder.append(guiValue.getName());
    return builder.toString();
  }

  /**
   *  Repaints a tree whenever table has changed.
   */
  public void tableChanged(javax.swing.event.TableModelEvent e)
  {
    repaint();
  }

  public Node<GuiObject> getSelectedNode() {
    if (getSelectionCount() != 1) {
      throw new IllegalStateException();
    } else {
      return (Node<GuiObject>)getLastSelectedPathComponent();
    }
  }

  public List<Node<GuiObject>> getSelectedNodes() {
    TreePath[] selection = getSelectionPaths();
    ArrayList<Node<GuiObject>> result
      = new ArrayList<Node<GuiObject>>(selection.length);
    for (TreePath path : selection) {
      result.add((Node<GuiObject>)path.getLastPathComponent());
    }
    return result;
  }

  public GuiObject getSelectedGO() {
    return getSelectedNode().getDecorated();
  }

  public VisualObject getSelectedVO() {
    try {
      return (VisualObject)getSelectedGO();
    } catch (ClassCastException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  public VisualContainer getSelectedVC() {
    try {
      return (VisualContainer)getSelectedGO();
    } catch (ClassCastException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

}
