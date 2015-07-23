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

import java.awt.event.InputEvent;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import control4j.gui.Screens;
import control4j.gui.Changer;
import control4j.gui.GuiObject;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;
import control4j.gui.components.Screen;

/**
 *
 *  Data transfer support for the objects in the tree panel.
 *
 */
class TreeTransferHandler extends javax.swing.TransferHandler
{

  /**
   *  Array of supported flavors.
   */
  protected DataFlavor[] supportedFlavors;

  /**
   *  Source object of the drag and drop. This means the object that was
   *  draged, copyed, cutted, ...
   */
  private GuiObject sourceObject;

  /**
   *  Initialize the array of supported flavors.
   */
  public TreeTransferHandler()
  {
    try
    {
      supportedFlavors = new DataFlavor[1];
      supportedFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
	+ ";class=control4j.gui.GuiObject");
    }
    catch (ClassNotFoundException e)
    {
      supportedFlavors = null;
    }
  }

  /**
   *
   */
  @Override
  public boolean canImport(javax.swing.TransferHandler.TransferSupport support)
  {
    if (support == null)
      throw new NullPointerException();
    if (support.isDataFlavorSupported(supportedFlavors[0]))
      return true;
    else
      return false;
  }

  /**
   *
   */
  @Override
  protected Transferable createTransferable(JComponent c)
  {
    JTree tree = (JTree)c;
    if (tree.getSelectionCount() == 0)
      return null;
    else
    {
      TreePath selection = tree.getLeadSelectionPath();
      sourceObject = (GuiObject)selection.getLastPathComponent();
      return new TreeTransferable(sourceObject);
    }
  }

  /**
   *
   */
  @Override
  public void exportAsDrag(JComponent comp, InputEvent e, int action)
  {
    super.exportAsDrag(comp, e, action);
  }

  /**
   *
   */
  @Override
  public void exportToClipboard(JComponent comp, Clipboard clip, int action)
  {
    super.exportToClipboard(comp, clip, action);
  }

  /**
   *  Remove data that was transferred.
   */
  @Override
  protected void exportDone(JComponent source, Transferable data, int action)
  {
    if (action == MOVE)
    {
      GuiObject parent = sourceObject.getParent();
      int index = parent.getIndexOfChild(sourceObject);
      parent.removeChild(index);
      JTree tree = (JTree)source;
      TreeModel treeModel = (TreeModel)tree.getModel();
      //treeModel.fireTreeNodeRemoved(parent, sourceObject, index);
    }
    sourceObject = null;
  }

  /**
   *
   */
  @Override
  public int getSourceActions(JComponent c)
  {
    return COPY | MOVE;
  }

  /**
   *
   */
  @Override
  public boolean importData(TransferHandler.TransferSupport support)
  {
    if (support == null) throw new NullPointerException();
    if (canImport(support))
    {
      // get a place where the data should be imported
      JTree tree = (JTree)support.getComponent();
      TreeModel treeModel = (TreeModel)tree.getModel();
      TreePath targetPath;
      if (support.isDrop())
        targetPath = tree.getDropLocation().getPath();
      else
	targetPath = tree.getLeadSelectionPath();
      // get a data to import
      try
      {
        GuiObject sourceObject = (GuiObject)support.getTransferable()
	  .getTransferData(supportedFlavors[0]);
	GuiObject targetObject = (GuiObject)targetPath.getLastPathComponent();
	if (sourceObject instanceof Screens)
	{
	  // root element can not be moved or copied
	  return false;
	}
	else if (sourceObject instanceof Screen)
	{
	  // only root element can hold screen object
	  // so, if the target path is another screen, place source here
	  if (targetObject instanceof Screen)
	  {
	    Screens root = (Screens)targetObject.getParent();
	    int index = root.getIndexOfChild(targetObject);
	    Screen clone = (Screen)sourceObject.clone(true);
	    root.insert(clone, index);
            //treeModel.fireTreeNodeInserted(clone);
	    return true;
	  }
          // if the target is root, place screen at the end
	  else if(targetObject instanceof Screens)
	  {
	    Screens root = (Screens)targetObject.getParent();
	    Screen clone = (Screen)sourceObject.clone(true);
	    root.add(clone);
	    //treeModel.fireTreeNodeInserted(clone);
	    return true;
	  }
	  // else do nothing
	  else
	  {
	    return false;
	  }
	}
	// component -> panel
	else if (sourceObject.isVisual() && targetObject.isVisualContainer())
	{
	  VisualObject clone = (VisualObject)sourceObject.clone(true);
	  ((VisualContainer)targetObject).add(clone);
	  //treeModel.fireTreeNodeInserted(clone);
	  return true;
	}
	// Component at the position of another component
	else if (sourceObject.isVisual() && targetObject.isVisual())
	{
	  VisualObject clone = (VisualObject)sourceObject.clone(true);
	  VisualContainer parent = (VisualContainer)targetObject.getParent();
	  int index = parent.getIndexOfChild(targetObject);
          parent.insert(clone, index);
	  //treeModel.fireTreeNodeInserted(clone);
	  return true;
	}
	// Changer -> changeble
	else if (!sourceObject.isVisual() && targetObject.isVisual())
        {
	  Changer clone = (Changer)sourceObject.clone(true);
	  ((VisualObject)targetObject).add(clone);
	  //treeModel.fireTreeNodeInserted(clone);
	  return true;
	}
	else
	{
	  return false;
	}

      }
      catch (java.awt.datatransfer.UnsupportedFlavorException e)
      {
      }
      catch (java.io.IOException e)
      {
      }
      return false;
    }
    else
      return false;
  }


  /**
   *
   */
  private class TreeTransferable implements Transferable
  {

    /**
     *
     */
    private GuiObject data;

    /**
     *
     */
    TreeTransferable(GuiObject data)
    {
      this.data = data;
    }

    /**
     *
     */
    public DataFlavor[] getTransferDataFlavors()
    {
      return supportedFlavors;
    }

    /**
     *
     */
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
      if (supportedFlavors[0].equals(flavor))
	return true;
      else
	return false;
    }

    /**
     *
     */
    public Object getTransferData(DataFlavor flavor)
    throws UnsupportedFlavorException
    {
      if (! isDataFlavorSupported(flavor))
	throw new UnsupportedFlavorException(flavor);
      return data;
    }

  }

}
