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

package cz.lidinsky.editor.schema;

import static cz.lidinsky.tools.Validate.notNull;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

/**
 *  The schema is a collection of interconnected symbols. This is a data model
 */
public class Schema<T> {

  public Schema() {}

  //--------------------------------------------------------------- Components.

  private ArrayList<Component<T>> components = new ArrayList<Component<T>>();

  public void add(Component<T> component) {
    components.add(notNull(component));
  }

  public Collection<Component<T>> getComponents() {
    return components;
  }

  //-------------------------------------------------------------------- Wires.

  private ArrayList<Wire> wires = new ArrayList<Wire>();

  public void addWire(Wire wire) {
    wires.add(notNull(wire));
  }

  public Collection<Wire> getWires() {
    return wires;
  }

  //------------------------------------------------------------ Spatial Index.

  public Component<T> getComponent(float x, float y) {
    for (Component<T> component : components) {
      if (component.contains(x, y)) {
        return component;
      }
    }
    return null;
  }

  public Component<T> getComponent(Point2D point) {
    return getComponent((float)point.getX(), (float)point.getY());
  }

}
