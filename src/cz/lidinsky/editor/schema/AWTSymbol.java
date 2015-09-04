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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;

/** Schematic symbol.  */
public class AWTSymbol implements Symbol {

  private Collection<Shape> shapes = new ArrayList<Shape>();

  public void add(Shape shape) {
    shapes.add(notNull(shape));
    if (bounds == null) {
      bounds = shape.getBounds();
    } else {
      bounds = bounds.union(shape.getBounds());
    }
  }

  private Rectangle bounds;

  // size
  public int getWidth() {
    return bounds == null ? 0 : bounds.width;
  }

  public int getHeight() {
    return bounds == null ? 0 : bounds.height;
  }

  private Collection<Terminal> terminals = new ArrayList<Terminal>();

  // terminals
  public Collection<Terminal> getTerminals() {
    return terminals;
  }

  public void add(Terminal terminal) {
    terminals.add(notNull(terminal));
  }

  // paint
  public void paint(Graphics2D g) {
    for (Shape shape : shapes) {
      g.draw(shape);
    }
  }

}
