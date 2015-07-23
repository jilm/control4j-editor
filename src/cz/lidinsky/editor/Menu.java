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

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

class Menu extends JMenuBar {

  public Menu() {
    super();
  }

  /**
   *  Load menu from the properties object.
   */
  public Menu(Properties settings) {
    // get top level items
    String temp = settings.getProperty("main_menu_items");
    String menuItems[] = temp.split(" ");
    for (String item : menuItems) {
      add((JMenu)loadMenuItem(settings, item));
    }
  }

  protected JMenuItem loadMenuItem(Properties settings, String key) {
    try {
      String temp = settings.getProperty(key + "_menu_items");
      JMenuItem menuItem;
      if (temp != null) {
        JMenu menu = new JMenu();
        String menuItems[] = temp.split(" ");
        for (String item : menuItems) {
          if (item.equals("|")) {
            menu.add(new JSeparator());
          } else {
            menu.add(loadMenuItem(settings, item));
          }
        }
        menuItem = menu;
      } else {
        menuItem = new JMenuItem();
      }
      // set the menu item label
      setLabel(menuItem, settings.getProperty(key + "_menu_label"));
      // action
      String actionKey = settings.getProperty(key + "_menu_action");
      if (actionKey != null) {
        menuItem.setAction(Action.getAction(settings, actionKey));
      }
      return menuItem;
    } catch (Exception e) {
      // TODO:
      throw new AssertionError();
    }
  }

  protected void setLabel(final JMenuItem menuItem, final String label) {
    if (label != null) {
      int mnemonicIndex = label.indexOf('_');
      if (mnemonicIndex >= 0) {
        String text = StringUtils.remove(label, '_');
        int key = text.codePointAt(mnemonicIndex);
        menuItem.setText(text);
        menuItem.setMnemonic(key);
      } else {
        menuItem.setText(label);
      }
    }
  }

  /**
   *  Adds new menu item at the end of the menu.
   */
  Menu addItem(String text, String actionCommand, ActionListener listener)
  {
    JMenuItem item = new JMenuItem(text);
    item.setActionCommand(actionCommand);
    item.addActionListener(listener);
    JMenu lastMenu = getMenu(getMenuCount()-1);
    lastMenu.add(item);
    return this;
  }

  Menu addItem(String text, String actionCommand, ActionListener listener, char mnemonic)
  {
    JMenuItem item = new JMenuItem(text);
    item.setActionCommand(actionCommand);
    item.addActionListener(listener);
    item.setMnemonic(mnemonic);
    JMenu lastMenu = getMenu(getMenuCount()-1);
    lastMenu.add(item);
    return this;
  }

  /**
   *  Adds a separator at the end of the menu.
   */
  Menu addSeparator()
  {
    JMenu lastMenu = getMenu(getMenuCount()-1);
    lastMenu.add(new JSeparator());
    return this;
  }
}
