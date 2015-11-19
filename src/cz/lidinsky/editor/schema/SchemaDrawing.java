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
import cz.lidinsky.tools.swing.AffineTransform;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
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


  /** Grid size in pixels. */
  private int gridSize = 20;

  private void update() {
    schema2screen.scale((double)gridSize, (double)gridSize);
    screen2schema.scale(1d / (double)gridSize, 1d / (double)gridSize);
  }

  //----------------------------------------------- Coordinate Transformations.

  /**
   *  This transformation scales the coordinate system to fit the grid size.
   *  This transformation transforms from the schema coordinates to the screen
   *  coordinate system.
   */
  private AffineTransform schema2screen = new AffineTransform();

  private AffineTransform screen2schema = new AffineTransform();

  AffineTransform screen2schema() {
    return screen2schema;
  }

  AffineTransform schema2screen() {
    return schema2screen;
  }

  //-------------------------------------------------------------------- Paint.

  @Override
  protected void paintComponent(Graphics g) {
    // paint background
    // paint grid
    paintGrid(g);
    // paint components
    Graphics2D schemaG = new Transform(g.create())
      .concat(schema2screen)
      .getGraphics();
    schemaG.setColor(Color.BLACK);
    for (Component component : schema.getComponents()) {
      paintComponent(schemaG, component);
    }
    // pint highlighted component
    if (highlighted != null) {
      paintHighlitedComponent(schemaG, highlighted);
    }
    // paint highlighted terminal
    if (highlightedTerminal != null) {
      paintHighlightedTerminal(
          schemaG, highlightedTerminal.getX(), highlightedTerminal.getY());
    }
    schemaG.dispose();
  }

  protected void paintGrid(Graphics g) {
    Graphics localG = g.create();
    localG.setColor(Color.LIGHT_GRAY);
    for (int x = 0; x < getWidth(); x += gridSize) {
      localG.drawLine(x, 0, x, getHeight());
    }
    for (int y = 0; y < getHeight(); y += gridSize) {
      localG.drawLine(0, y, getWidth(), y);
    }
    localG.dispose();
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
    localG.setStroke(
        new BasicStroke((float)screen2schema.transform(1f, 1f).getX()));
    localG.setColor(Color.BLACK);
    symbol.paint(localG);
    localG.dispose();
  }

  protected void paintHighlitedComponent(Graphics g, Component component) {
    Symbol symbol = component.getSymbol();
    // paint
    Graphics2D localG = new Transform(g.create())
      .concat(component.getTransform())
      .getGraphics();
    localG.setStroke(
        new BasicStroke((float)screen2schema.transform(1f, 1f).getX()));
    localG.setColor(Color.RED);
    symbol.paint(localG);
    // paint terminals of the highlighted component
    for (Terminal terminal : symbol.getTerminals()) {
      paintTerminal(localG, terminal.getX(), terminal.getY());
    }
    localG.dispose();
  }

  private Shape terminalShape = new Rectangle2D.Float(-0.2f, -0.2f, 0.4f, 0.4f);

  protected void paintTerminal(Graphics g, int x, int y) {
    Graphics2D localG = new Transform(g.create())
      .translate(x, y)
      .getGraphics();
    localG.setColor(Color.RED);
    localG.draw(terminalShape);
    localG.dispose();
  }

  protected void paintHighlightedTerminal(Graphics g, int x, int y) {
    Graphics2D localG = new Transform(g.create())
      .translate(x, y)
      .getGraphics();
    localG.setColor(Color.RED);
    localG.fill(terminalShape);
    localG.dispose();
  }

  //---------------------------------------------------------------- Selection.

  private Component highlighted;

  void highlite(Component component) {
    if (highlighted != component) {
      highlighted = component;
      repaint(getBounds());
    }
  }

  private Terminal highlightedTerminal;

  void setHighlighted(Terminal terminal) {
    highlightedTerminal = terminal;
  }

  /**
   *  Returns the terminal shape in the component coordinates.
   */
  Shape getShape(Component component, Terminal terminal) {
    return schema2screen.concat(component.getTransform())
      .createTransformedShape(terminalShape);
  }

  //--------------------------------------------------------------- Popup Menu.

}
