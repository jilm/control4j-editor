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
import cz.lidinsky.tools.swing.Transform;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.JComponent;

/**
 *  Schema swing component; it is responsible for painting the schema.
 *
 */
public class SchemaDrawing extends JComponent {

  /**
   *  Component initialization.
   */
  public SchemaDrawing(Schema<?> schema) {
    this.schema = notNull(schema);
    setPreferredSize(new Dimension(500, 500));
    update();
  }

  //--------------------------------------------------------------- Data Model.

  private Schema<?> schema;

  //-------------------------------------------------------------------- Paint.

  /** Grid size in pixels. */
  private int gridSize = 20;

  /**
   *  This transformation scales the coordinate system to fit the grid size.
   *  This transformation transforms from the schema coordinates to the screen
   *  coordinate system.
   */
  private AffineTransform transform = new AffineTransform();

  private void update() {
    transform.scale((double)gridSize, (double)gridSize);
  }

  Point2D screen2schema(float x, float y) {
    try {
      Point2D point = new Point2D.Float(x, y);
      return transform.inverseTransform(point, point);
    } catch (java.awt.geom.NoninvertibleTransformException e) {
      // should not happen
      throw new AssertionError();
    }
  }

  Point2D screen2schema(Point2D point) {
    return screen2schema((float)point.getX(), (float)point.getY());
  }

  @Override
  protected void paintComponent(Graphics g) {
    // paint background
    // paint grid
    Graphics2D localG = (Graphics2D)g.create();
    paintGrid(localG);
    localG.dispose();
    // paint components
    for (Component component : schema.getComponents()) {
      localG = new Transform(g.create())
        .concat(transform)
        .getGraphics();
      if (component == highlited) {
        localG.setColor(Color.RED);
        paintHighlitedComponent(localG, component);
      } else {
        localG.setColor(Color.BLACK);
        paintComponent(localG, component);
      }
      localG.dispose();
    }
    // paint wires
  }

  protected void paintGrid(Graphics g) {
    g.setColor(Color.LIGHT_GRAY);
    for (int x = 0; x < getWidth(); x += gridSize) {
      g.drawLine(x, 0, x, getHeight());
    }
    for (int y = 0; y < getHeight(); y += gridSize) {
      g.drawLine(0, y, getWidth(), y);
    }
  }

  /**
   *  It paints given component.
   *
   *  <p>Coordinate system.
   */
  protected void paintComponent(Graphics g, Component component) {
    Symbol symbol = component.getSymbol();
    // paint
    Graphics2D localG = new Transform(g.create())
      .concat(component.getTransform())
      .getGraphics();
    localG.setStroke(new BasicStroke((float)screen2schema(1f, 1f).getX()));
    symbol.paint(localG);
    localG.dispose();
  }

  protected void paintHighlitedComponent(Graphics g, Component component) {
    Symbol symbol = component.getSymbol();
    // paint
    Graphics2D localG = new Transform(g.create())
      .concat(component.getTransform())
      .getGraphics();
    symbol.paint(localG);
    //new Transform(g).scale(1 / 100d);
    for (Terminal terminal : symbol.getTerminals()) {
      paintTerminal(localG, terminal.getX(), terminal.getY());
      System.out.println(localG.getTransform().toString());
      System.out.println(" " + terminal.getX() + " " + terminal.getY());
    }
    localG.dispose();
  }

  protected void paintTerminal(Graphics g, int x, int y) {
    g.drawRect(x * 100 - 20, y * 100 - 20, 40, 40);
  }

  //---------------------------------------------------------------- Selection.

  private Component highlited;

  void highlite(Component component) {
    if (highlited != component) {
      highlited = component;
      repaint(getBounds());
    }
  }

  //--------------------------------------------------------------- Popup Menu.

}
