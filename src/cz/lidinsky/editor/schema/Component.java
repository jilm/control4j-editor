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

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.JComponent;

/** Decorator for the component object. */
public class Component<T> {

  public Component(Symbol symbol) {
    this.symbol = notNull(symbol);
    update();
  }

  // id, it is painted near the component symbol

  // symbol

  private Symbol symbol;

  public Symbol getSymbol() {
    return symbol;
  }

  // reflection

  // decorated object
  private T decorated;

  public T getDecorated() {
    return decorated;
  }

  // terminals

  //-------------------------------------------------------- Coordinate System.

  /**
   *  Transformation from the symbol coordinate system into the schema
   *  coordinates.
   */
  private AffineTransform transform = new AffineTransform();

  Point2D schema2component(float x, float y) {
    try {
      Point2D point = new Point2D.Float(x, y);
      return transform.inverseTransform(point, point);
    } catch (java.awt.geom.NoninvertibleTransformException e) {
      // should not happen
      throw new AssertionError();
    }
  }

  Point2D component2schema(float x, float y) {
    Point2D point = new Point2D.Float(x, y);
    return transform.transform(point, point);
  }

  private Point2D tempPoint = new Point2D.Float();

  public boolean contains(float x, float y) {
    Point2D point = schema2component(x, y);
    return (point.getX() > 0 && point.getX() < symbol.getWidth()
        && point.getY() > 0 && point.getY() < symbol.getHeight());
  }

  public int getX() {
    return (int)transform.getTranslateX();
  }

  public int getY() {
    return (int)transform.getTranslateY();
  }

  public Component<T> move(int dx, int dy) {
    transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
    return this;
  }

  public Component<T> rotate(int rotation) {
    transform.quadrantRotate(
        rotation, symbol.getWidth() / 2, symbol.getHeight() / 2);
    update();
    return this;
  }

  private Point2D size;

  public int getWidth() {
    return (int)size.getX();
  }

  public int getHeight() {
    return (int)size.getY();
  }

  /**
   *  Updates all of the cached information when something has changed that
   *  could influence the coordinate system.
   */
  private void update() {
    if (size == null) {
      size = new Point2D.Float();
    }
    size.setLocation(symbol.getWidth(), symbol.getHeight());
    transform.deltaTransform(size, size);
  }

  public AffineTransform getTransform() {
    return transform;
  }

}
