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
import java.util.Collection;
import java.lang.reflect.Constructor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JTree;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

/**
 *
 *  Provides operations like copy, cut, paste, delete.
 *
 */
class EditHandling
implements ActionListener
{

  private JTree tree;
  private TreeModel treeModel;

  private TreePath[] container;

  public EditHandling(JTree tree)
  {
    this.tree = tree;
    this.treeModel = (TreeModel)tree.getModel();
  }

  /**
   *  Adds Edit menu at the end of the given menu bar.
   */
  void addMenu(Menu menu)
  {
    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('E');
    menu.add(editMenu);
    // Copy
    editMenu.add(new JMenuItem(TransferHandler.getCopyAction()));
    // Cut
    JMenuItem menuItem = editMenu.add("Cut");
    menuItem.setAction(TransferHandler.getCutAction());
    menuItem.setAccelerator(
      KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
    menuItem.setMnemonic(KeyEvent.VK_T);
    // Paste
    editMenu.add(new JMenuItem(TransferHandler.getPasteAction()));
    menu.addSeparator();
    menu.addItem("Delete", "EDIT_DELETE", this, 'D');
  }

  /**
   *  Response method for Edit menu items.
   */
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("EDIT_DELETE"))
      doDelete();
  }

  protected void doDelete()
  {
  }

}
