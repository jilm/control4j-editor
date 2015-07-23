package cz.lidinsky.editor;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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

import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.reflect.Constructor;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JTree;
import javax.swing.JMenu;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;
import control4j.gui.GuiObject;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;
import control4j.gui.Changer;
import control4j.gui.components.Screen;
import control4j.scanner.Scanner;
import control4j.scanner.Item;

/**
 *
 */
class LayoutHandling implements ActionListener
{

  private JTree tree;
  private TreeModel treeModel;

  public LayoutHandling(JTree tree)
  {
    this.tree = tree;
    this.treeModel = (TreeModel)tree.getModel();
  }

  /**
   *  Adds Layout menu at the end of the given menu bar.
   */
  void addMenu(Menu menu)
  {
    JMenu layoutMenu = new JMenu("Layout");
    layoutMenu.setMnemonic('L');
    menu.add(layoutMenu);
    menu.addItem("Full width", "LAYOUT_FULL_WIDTH", this, 'w');
    menu.addItem("Full height", "LAYOUT_FULL_HEIGHT", this, 'h');
    menu.addSeparator();
    menu.addItem("Center vertically", "LAYOUT_CENTER_VERTICALLY", this, 'v');
    menu.addSeparator();
    menu.addItem("Move vertically", "LAYOUT_MOVE_VERTICALLY", this);
  }

  /**
   *  Response method for Edit menu items.
   */
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("LAYOUT_FULL_WIDTH"))
      doFullWidth();
    if (e.getActionCommand().equals("LAYOUT_FULL_HEIGHT"))
      doFullHeight();
    if (e.getActionCommand().equals("LAYOUT_CENTER_VERTICALLY"))
      doCenterVertically();
    if (e.getActionCommand().equals("LAYOUT_MOVE_VERTICALLY"))
      doMoveVertically();
  }

  /**
   *  Takes all of the components that are selected and adjust theirs
   *  width such that they occupy full width of its parent.
   *  Width ratio of particular components is preserved.
   *
   *  <p>All of the selected components must have common parent. If not,
   *  this method do nothing.
   *
   *  <p>Only components that have property annotated with key=Width
   *  will be taken into account. The parent also must have property
   *  called Width of course. If not, this method will do nothing.
   *
   *  <p>Common parent may not have layout manager.
   *
   *  <p>Moreover, X position of components will be adjusted such that
   *  components follow each other.
   */
  private void doFullWidth()
  {
    // get all of the selected components
    TreePath[] selectionPaths = tree.getSelectionPaths();
    ArrayList<VisualObject> selection = new ArrayList<VisualObject>();
    for (TreePath path : selectionPaths)
      if (((GuiObject)path.getLastPathComponent()).isVisual())
	selection.add((VisualObject)path.getLastPathComponent());
    // find a common parent of all of the selected components
    if (selection.size() == 0) return;
    VisualObject parent = (VisualObject)selection.get(0).getParent();
    for (VisualObject object : selection)
      if (object.getParent() != parent)
	return;
    // get the width of the parent
    int parentWidth = parent.getVisualComponent().getWidth();
    // get the width of the children
    int[] widths = new int[selection.size()];
    int childrenWidth = 0;  // total width of all children
    for (int i=0; i<selection.size(); i++)
      try
      {
	widths[i] = selection.get(i).getInt("Width");
	childrenWidth += widths[i];
      }
      catch (Exception e) { widths[i] = 0; }
    // get the multiple
    if (childrenWidth == 0) return;
    float multiple = (float)parentWidth / (float)childrenWidth;
    // calculate new children widths
    float x = 0.0f;    // will be the x position of the next component
    float sum = 0.0f;  // becouse of rounding errors
    for (int i=0; i<selection.size(); i++)
    {
      sum += multiple * (float)widths[i];
      int width = Math.round(sum - x);
      try { selection.get(i).set("Width", width); } catch (Exception e) { }
      try { selection.get(i).set("X", Math.round(x)); } catch (Exception e) { }
      x = sum;
    }
  }

  /**
   *  Takes all of the components that are selected and adjust theirs
   *  height such that they occupy full height of its parent.
   *  Height ratio of particular components is preserved.
   *
   *  <p>All of the selected components must have common parent. If not,
   *  this method do nothing.
   *
   *  <p>Only components that have property annotated with key=Height
   *  will be taken into account.
   *
   *  <p>Common parent may not have layout manager.
   *
   *  <p>Moreover, Y position of components will be adjusted such that
   *  components follow each other.
   */
  private void doFullHeight()
  {
    // get all of the selected components
    TreePath[] selectionPaths = tree.getSelectionPaths();
    ArrayList<VisualObject> selection = new ArrayList<VisualObject>();
    for (TreePath path : selectionPaths)
      if (((GuiObject)path.getLastPathComponent()).isVisual())
	selection.add((VisualObject)path.getLastPathComponent());
    // find a common parent of all of the selected components
    if (selection.size() == 0) return;
    VisualObject parent = (VisualObject)selection.get(0).getParent();
    for (VisualObject object : selection)
      if (object.getParent() != parent)
	return;
    // get the heigth of the parent
    int parentHeight = parent.getVisualComponent().getHeight();
    // get the height of the children
    int[] heights = new int[selection.size()];
    int childrenHeight = 0;  // total height of all children
    for (int i=0; i<selection.size(); i++)
      try
      {
	heights[i] = selection.get(i).getInt("Height");
	childrenHeight += heights[i];
      }
      catch (Exception e) { heights[i] = 0; }
    // get the multiple
    if (childrenHeight == 0) return;
    float multiple = (float)parentHeight / (float)childrenHeight;
    // calculate new children heights
    float y = 0.0f;    // will be the y position of the next component
    float sum = 0.0f;  // becouse of rounding errors
    for (int i=0; i<selection.size(); i++)
    {
      sum += multiple * (float)heights[i];
      int height = Math.round(sum - y);
      try { selection.get(i).set("Height", height); } catch (Exception e) { }
      try { selection.get(i).set("Y", Math.round(y)); } catch (Exception e) { }
      y = sum;
    }
  }

  /**
   *  Center selected components vertically inside theirs parent.
   */
  private void doCenterVertically()
  {
    // get selected components
    ArrayList<VisualObject> selection = getSelection();
    // find common parent
    VisualContainer parent = getParent(selection);
    if (parent == null) return;
    // get height of the whole selection
    int yMin = Integer.MAX_VALUE;
    int yMax = Integer.MIN_VALUE;
    for (VisualObject object : selection)
    {
      int y = object.getVisualComponent().getY();
      int height = object.getVisualComponent().getHeight();
      yMin = Math.min(y, yMin);
      yMax = Math.max(y + height, yMax);
    }
    // get height of the parent
    int parentHeight = parent.getVisualComponent().getHeight();
    Insets insets = parent.getVisualComponent().getInsets();
    parentHeight -= insets.top + insets.bottom;
    // compute additional constant
    int yAdd = ((parentHeight - yMax + yMin) / 2) - yMin;
    // shift all of the components
    for (VisualObject object : selection)
    {
      int y = object.getVisualComponent().getY();
      try
      {
	object.set("Y", y + yAdd);
      } catch (Exception e) { }    // TODO
    }
  }

  /**
   *
   */
  private void doMoveVertically()
  {
    // ask a user, how many pixels
    Object response = JOptionPane.showInputDialog(
	Editor.getInstance().getMainFrame(), "How many pixels?",
	"Move Vertically", JOptionPane.QUESTION_MESSAGE, null, null, null);
    if (response == null) return;
    int offset = Integer.parseInt((String)response); // TODO
    // move all of the selected components
    TreePath[] selectionPaths = tree.getSelectionPaths();
    for (TreePath selectionPath : selectionPaths)
      if (((GuiObject)selectionPath.getLastPathComponent()).isVisual())
      {
	VisualObject selection
	    = (VisualObject)selectionPath.getLastPathComponent();
        try
        {
	  int y = ((Integer)Scanner.getValue(selection, "Y")).intValue();
          selection.set("Y", y + offset);
        } catch (Exception e) { }    // TODO
      }
  }

  /**
   *  Returns all of the selected visual objects.
   */
  private ArrayList<VisualObject> getSelection()
  {
    // get all of the selected components
    TreePath[] selectionPaths = tree.getSelectionPaths();
    ArrayList<VisualObject> selection = new ArrayList<VisualObject>();
    for (TreePath path : selectionPaths)
      if (((GuiObject)path.getLastPathComponent()).isVisual())
	selection.add((VisualObject)path.getLastPathComponent());
    return selection;
  }

  /**
   *  Return a parent which is common for all of the given children.
   *  If the given list is empty or if the given children do not have
   *  common parent, it returns null.
   */
  private VisualContainer getParent(ArrayList<VisualObject> children)
  {
    // find a common parent of all of the selected components
    if (children.size() == 0) return null;
    VisualContainer parent = (VisualContainer)children.get(0).getParent();
    for (VisualObject object : children)
      if (object.getParent() != parent)
	return null;
    return parent;
  }

}
