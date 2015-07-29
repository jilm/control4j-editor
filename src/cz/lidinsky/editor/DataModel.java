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

package cz.lidinsky.editor;

import control4j.gui.GuiObject;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;
import control4j.gui.Changer;
import control4j.gui.Screens;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.tree.ChangeableNode;
import cz.lidinsky.tools.tree.Node;

import javax.swing.JComponent;
import java.awt.Container;

public class DataModel {

  protected ChangeableNode<GuiObject> root;

  public static final String LINK_KEY = "JComponentToNodeLink";

  public DataModel() {
    root = new ChangeableNode<GuiObject>();
    root.setDecorated(new Screens());
  }

  public Node<GuiObject> getRoot() {
    return root;
  }

  //------------------------------------------------------------------ Add Node

  public void addChild(Node<GuiObject> parent, Node<GuiObject> child) {
    // check that the parent is a node of this tree
    if (parent.getRoot() != root) {
      throw new CommonException()
        .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
        .set("message", "The parent node is not member of this tree!")
        .set("parent", parent)
        .set("child", child);
    }
    // add a child into the data structure
    GuiObject parentObject = parent.getDecorated();
    GuiObject childObject = child.getDecorated();
    check(parentObject, childObject);
    ((ChangeableNode<GuiObject>)parent).addChild(child);
    // create visual representation
    try {
      if (getJC(parent) != null) {
        getJC(parent).add(createVisualComponent(child));
        configureVisualComponent(child);
      }
    } catch (ClassCastException e) {
      // it is ok. Component is not a visual one.
    }
  }

  public Node<GuiObject> addChild(Node<GuiObject> parent, GuiObject child) {
    ChangeableNode<GuiObject> childNode = new ChangeableNode<GuiObject>();
    childNode.setDecorated(child);
    addChild(parent, childNode);
    return childNode;
  }

  protected void check(GuiObject parent, GuiObject child) {
    if (!parent.isAssignable(child)) {
      throw new CommonException()
        .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
        .set("message",
            "The given object cannot be a child of the given parent!")
        .set("parent", parent)
        .set("child", child);
    }
  }

  public void insertChild(
      Node<GuiObject> parent, Node<GuiObject> child, int index) {

    // check that the parent is a node of this tree
    if (parent.getRoot() != root) {
      throw new IllegalArgumentException();
    }
    // add a child into the data structure
    GuiObject parentObject = parent.getDecorated();
    GuiObject childObject = child.getDecorated();
    check(parentObject, childObject);
    ((ChangeableNode<GuiObject>)parent).insertChild(child, index);
    // create visual representation
    try {
      if (getJC(parent) != null) {
        getJC(parent).add(createVisualComponent(child));
        configureVisualComponent(child);
      }
    } catch (ClassCastException e) {
      // it is ok. Component is not a visual one.
    }
  }

  public Node<GuiObject> insertChild(
      Node<GuiObject> parent, GuiObject child, int index) {

    ChangeableNode<GuiObject> childNode = new ChangeableNode<GuiObject>();
    childNode.setDecorated(child);
    insertChild(parent, childNode, index);
    return childNode;
  }

  //--------------------------------------------------------------- Remove Node

  public void removeNode(Node<GuiObject> node) {
    ChangeableNode<GuiObject> parent
      = (ChangeableNode<GuiObject>)node.getParent();
    if (parent == null) {
      throw new IllegalArgumentException();
    } else {
      releaseVisualComponent(node);
      parent.removeChild(node);
    }
  }

  //----------------------------------------------------- Visual Representation

  public JComponent createRootVisualComponent() {
    if (getJC(root) == null) {
      JComponent component = getVO(root).createVisualComponent();
      component.putClientProperty(LINK_KEY, root);
      return component;
    } else {
      return getJC(root);
    }
  }

  public void configureRootVisualComponent() {
    if (getJC(root) == null) {
      throw new IllegalStateException();
    } else {
      getVO(root).configureVisualComponent();
      for (Node<GuiObject> child : root.getChildren()) {
        try {
          getJC(root).add(createVisualComponent(child));
          configureVisualComponent(child);
        } catch (ClassCastException e) {
          // this is OK, component is just not visual
        }
      }
    }
  }

  protected JComponent createVisualComponent(Node<GuiObject> node) {
    if (getJC(node) == null) {
      JComponent component = getVO(node).createVisualComponent();
      component.putClientProperty(LINK_KEY, node);
      return component;
    } else {
      throw new IllegalStateException();
    }
  }

  protected void configureVisualComponent(Node<GuiObject> node) {
    if (getJC(node) == null) {
      throw new IllegalStateException();
    } else {
      getVO(node).configureVisualComponent();
      for (Node<GuiObject> child : node.getChildren()) {
        try {
          getJC(node).add(createVisualComponent(child));
          configureVisualComponent(child);
        } catch (ClassCastException e) {
          // this is OK, component is just not visual
        }
      }
    }
  }

  protected void releaseVisualComponent(Node<GuiObject> node) {
    try {
      // first of all releae visual componentes of all of the children
      for (Node<GuiObject> child : node.getChildren()) {
        releaseVisualComponent(child);
      }
      // release the visual component of this node
      VisualObject vo = getVO(node);
      JComponent component = vo.getVisualComponent();
      if (component != null) {
        Container parent = component.getParent();
        parent.remove(component);
        vo.releaseVisualComponent();
      }
    } catch (ClassCastException e) {
      // It is ok, given node is not just visual one
    }
  }

  //--------------------------------------------------------- Auxiliary Methods

  /**
   *  Returns a visual object of the given node.
   *
   *  @throws ClassCastException
   *             if the given object doesn't reprsents a visual object
   */
  public static VisualObject getVO(Node<GuiObject> node) {
    return (VisualObject)node.getDecorated();
  }

  public static JComponent getJC(Node<GuiObject> node) {
    return getVO(node).getVisualComponent();
  }

}
