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

import static cz.lidinsky.tools.Validate.notNull;

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

import cz.lidinsky.tools.tree.Node;

/**
 *
 *
 *
 */
class VisualObjectTransferHandler extends javax.swing.TransferHandler {

  /**
   *  Array of supported flavors.
   */
  protected DataFlavor[] supportedFlavors;

  /**
   *  Initialize the array of supported flavors.
   */
  public VisualObjectTransferHandler() {
    try {
      supportedFlavors = new DataFlavor[4];
      supportedFlavors[0]
        = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
            + ";class=cz.lidinsky.tools.tree.Node");
      supportedFlavors[1]
        = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
            + ";class=control4j.gui.VisualObject");
      supportedFlavors[2]
        = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
            + ";class=javax.swing.JComponent");
      supportedFlavors[3]
        = DataFlavor.getTextPlainUnicodeFlavor();
    } catch (ClassNotFoundException e) {
      supportedFlavors = null;
    }
  }

  //------------------------------------------------- Transfer Handler Methods.

  @Override
  public boolean canImport(TransferHandler.TransferSupport support) {
    return false;
  }

  /**
   *
   */
  @Override
  protected Transferable createTransferable(JComponent c) {
    return new VisualObjectTransferable(DataModel.getNode(c));
  }

  /**
   *
   */
  @Override
  public void exportAsDrag(JComponent comp, InputEvent e, int action) {
    super.exportAsDrag(comp, e, action);
  }

  /**
   *
   */
  @Override
  public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
    super.exportToClipboard(comp, clip, action);
  }

  /**
   *  Remove data that was transferred.
   */
  @Override
  protected void exportDone(JComponent source, Transferable data, int action) {
    if (action == MOVE) {
      //Editor.getDataModel().removeNode(data.); // TODO:
    }
  }

  /**
   *
   */
  @Override
  public int getSourceActions(JComponent c) {
    System.out.println("get source actions ...");
    return COPY | MOVE;
  }

  /**
   *
   */
  private class VisualObjectTransferable implements Transferable {

    /**
     *
     */
    private Node<GuiObject> data;

    /**
     *
     */
    VisualObjectTransferable(Node<GuiObject> data) {
      this.data = notNull(data);
    }

    /**
     *
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return supportedFlavors;
    }

    /**
     *
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      for (DataFlavor supported : supportedFlavors) {
        if (supported.equals(flavor)) {
          return true;
        }
      }
      return false;
    }

    /**
     *
     */
    @Override
    public Object getTransferData(DataFlavor flavor)
    throws UnsupportedFlavorException {
      int i = 0;
      for (; ; i++) {
        if (i >= supportedFlavors.length) {
          throw new UnsupportedFlavorException(flavor);
        }
        if (supportedFlavors[i].equals(flavor)) break;
      }
      switch (i) {
        case 0:
          return data;
        case 1:
          return data.getDecorated();
        case 2:
          return DataModel.getJC(data);
        case 3:
          return data.toString();
        default:
          throw new UnsupportedFlavorException(flavor);
      }
    }
  }

}
