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

import control4j.application.gui.AbstractAdapter;
import control4j.gui.GuiObject;
import control4j.gui.Screens;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;
import control4j.gui.Changer;
import control4j.gui.components.Screen;

import cz.lidinsky.tools.tree.Builder;

import cz.lidinsky.tools.tree.Node;

public class Gui2EditorAdapter extends AbstractAdapter {

  private Node<GuiObject> pointer;

  private DataModel handler;

  public Gui2EditorAdapter(DataModel handler) {
    this.handler = handler;
    pointer = handler.getRoot();
  }

  public void put(Screens screens) {
  }

  public void put(VisualObject object) {
    pointer = handler.addChild(pointer, object);
  }

  public void put(VisualContainer container) {
    pointer = handler.addChild(pointer, container);
  }

  public void goBack() {
    pointer = pointer.getParent();
  }

  public void put(Changer changer) {
    handler.addChild(pointer, changer);
  }

  //---------------------------------------------------------- Building a Tree.

  private Builder<GuiObject> treeBuilder = new Builder<GuiObject>();

  public void open() {
    treeBuilder.open();
  }

  public void close(GuiObject object) {
    Node<GuiObject> node = treeBuilder.close(object);
    if (object instanceof Screen) {
      handler.addChild(handler.getRoot(), node);
    }
  }

}
