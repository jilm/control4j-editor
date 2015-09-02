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

package cz.lidinsky.editor;

import java.util.ArrayList;
import java.awt.Container;
import java.awt.Component;
import javax.swing.tree.TreePath;
import javax.swing.JComponent;
import javax.swing.JPanel;
//import javax.swing.tree.TreeModel;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import control4j.gui.Changer;
import control4j.gui.GuiObject;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;
import control4j.gui.Screens;
import control4j.gui.components.Screen;


import cz.lidinsky.tools.tree.Node;

import java.util.HashSet;
import java.util.Set;

/**
 *  Adds a tree data model interface implementation to the DataModel.
 */
public class TreeModel extends DataModel
implements javax.swing.tree.TreeModel, FileListener {

  /**
   *  Creates an empty model.
   */
  public TreeModel() {
    super();
  }

  //-------------------------------------- Tree Model Interface Implementation.

  /**
   *  @param parent
   *             instance of a Node class is expected
   */
  @Override
  public Node<GuiObject> getChild(Object parent, int index) {
    return ((Node<GuiObject>)parent).getChild(index);
  }

  /**
   *  Number of children of the given object.
   *
   *  @param parent
   *             instance of a Node class is expected
   */
  public int getChildCount(Object parent) {
    return ((Node<GuiObject>)parent).getChildren().size();
  }

  /**
   *
   */
  public int getIndexOfChild(Object parent, Object child) {
    try {
      return ((Node<GuiObject>)parent).getIndexOfChild((Node<GuiObject>)child);
    } catch (Exception e) {
      return -1;
    }
  }

  /**
   *
   */
  public boolean isLeaf(Object node) {
    return ((Node<GuiObject>)node).isLeaf();
  }

  /**
   *
   */
  public void valueForPathChanged(TreePath path, Object newValue) { }

  @Deprecated
  public void setRoot(Screens screens) {
    //root = screens;
    //fireTreeStructureChanged(root);
  }

  //----------------------------------------------------------- Event Handling.

  /** Event listeners. */
  private Set<TreeModelListener> listeners = new HashSet<TreeModelListener>();

  /**
   *  Add an event listener.
   */
  public void addTreeModelListener(TreeModelListener l) {
    if (l != null) {
      listeners.add(l);
    }
  }

  /**
   *  Removes the given listener.
   */
  public void removeTreeModelListener(TreeModelListener l) {
    if (l != null) {
      listeners.remove(l);
    }
  }

  /**
   *  Notify all of the registered listeners, that a node was added.
   *
   *  @param node
   *             an object which was added
   */
  protected void fireTreeNodeInserted(Node<GuiObject> node) {
    Node<GuiObject> parent = node.getParent();
    Object[] path = getPath(parent);
    int[] indexes = new int[] {getIndexOfChild(parent, node)};
    Object[] children = new Object[] {node};
    TreeModelEvent e = new TreeModelEvent(this, path, indexes, children);
    for (TreeModelListener listener : listeners) {
      listener.treeNodesInserted(e);
    }
    System.out.println(e.toString());
  }

  protected void fireTreeNodeChanged(Node<GuiObject> node) {
    Object[] path = getPath(node.getParent());
    int[] indexes = new int[] {getIndexOfChild(getParent(node), node)};
    Object[] children = new Object[] {node};
    TreeModelEvent e = new TreeModelEvent(this, path, indexes, children);
    for (TreeModelListener listener : listeners) {
      listener.treeNodesChanged(e);
    }
  }

  /**
   *  Called when the node has been removed
   *
   *  @param node
   *             a node that has been deleted
   */
  protected void fireTreeNodeRemoved(
      Node<GuiObject> parent, Node<GuiObject> node, int index) {
    Object[] parentPath = getPath(parent);
    int[] indexes = new int[] { index };
    Object[] children = new Object[] { node };
    TreeModelEvent e = new TreeModelEvent(this, parentPath, indexes, children);
    for (TreeModelListener listener : listeners) {
      listener.treeNodesRemoved(e);
    }
  }

  /**
   *  Called when the structure drasticaly changed in some way.
   *
   *  @param node
   *             object indentifying modified subtree
   */
  protected void fireTreeStructureChanged(Node<GuiObject> node) {
    TreeModelEvent e = new TreeModelEvent(this, getPath(node));
    for (TreeModelListener listener : listeners) {
      listener.treeStructureChanged(e);
    }
  }

  /**
   *  Returns a path for a given node.
   */
  protected Object[] getPath(Node<GuiObject> node) {
    ArrayList<Object> path = new ArrayList<Object>();
    while (node != null) {
      path.add(0, node);
      node = node.getParent();
    }
    return path.toArray();
  }

  /**
   *  Returns parent of the given node.
   */
  public static Object getParent(Object node) {
    return ((Node<GuiObject>)node).getParent();
  }

  public void fileChanged(FileEvent e) {
    //setRoot(e.getScreens());
  }

  //------------------------ Override Data Model Public Methods to Fire Events.

  @Override
  public void addChild(Node<GuiObject> parent, Node<GuiObject> child) {
    super.addChild(parent, child);
    fireTreeNodeInserted(child);
  }

  @Override
  public void removeNode(Node<GuiObject> node) {
    Node<GuiObject> parent = node.getParent();
    int index = parent.getIndexOfChild(node);
    super.removeNode(node);
    fireTreeNodeRemoved(parent, node, index);
  }

}
