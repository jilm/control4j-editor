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

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

/**
 *  This class may be in the following states:
 *  <ol>
 *    <li> Nothing happening.
 *    <li> A component is hightlited.
 *    <li> A wire is highlited.
 *    <li> A terminal is highlited.
 *    <li> One component is selected.
 *    <li> More than one component is selected.
 *    <li> Selection is dragged.
 *    <li> A wire handler is dragged.
 *    <li> Wire is painted.
 *  </ol>
 */
class Controller implements MouseMotionListener {

  // data model
  private Schema schema;

  // the view
  private SchemaDrawing view;

  private Status status;

  public Controller(SchemaDrawing view, Schema model) {
    this.view = notNull(view);
    this.schema = notNull(model);
    this.view.addMouseMotionListener(this);
    this.status = new Status();
  }

  /**
   *  Initial status.
   */
  private class Status {

    public void mouseMoved(MouseEvent e) {
    }

  }

  //---------------------------------------------------- Mouse Motion Listener.

  public void mouseDragged(MouseEvent e) {
  }

  private Component highlited;

  /**
   *  Highlits components under the mouse cursor.
   */
  public void mouseMoved(MouseEvent e) {
    // highlits component under the cursor
    Point2D point = view.screen2schema(e.getX(), e.getY());
    Component component = schema.getComponent(point);
    boolean changed = highlited != component;
    if (changed) {
      view.highlite(component);
    }
    highlited = component;
    // status dependent action
    status.mouseMoved(e);
  }

  //-------------------------------------------------------------------- Debug.

  public static void main(String[] args) {
    javax.swing.JFrame frame = new javax.swing.JFrame("Schema demo");
    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    Schema model = new Schema();
    Component resistor = new Component(new Resistor())
      .move(2, 3)
      .rotate(1);
    model.add(resistor);
    SchemaDrawing view = new SchemaDrawing(model);
    Controller controller = new Controller(view, model);
    frame.getContentPane().add(view);
    frame.pack();
    frame.setVisible(true);
  }

}
