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

import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

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
class Controller
implements MouseMotionListener, DragGestureListener, DropTargetListener {

  // data model
  private Schema schema;

  // the view
  private SchemaDrawing view;

  private Status status;

  /**
   *  Initialization.
   */
  public Controller(SchemaDrawing view, Schema model) {
    this.view = notNull(view);
    this.schema = notNull(model);
    this.view.addMouseMotionListener(this);
    this.status = new Status();
    // Drag and Drop support
    new java.awt.dnd.DragSource()
      .createDefaultDragGestureRecognizer(
          view, java.awt.dnd.DnDConstants.ACTION_MOVE, this);
    new DropTarget(view, java.awt.dnd.DnDConstants.ACTION_MOVE, this, true, null);
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
      fireHighlightChange(component);
    }
    highlited = component;
    // status dependent action
    status.mouseMoved(e);
  }

  //----------------------------------------------------------- Event Handling.

  private Set<StatusListener> statusListeners = new HashSet<StatusListener>();

  public void addStatusListener(StatusListener listener) {
    statusListeners.add(notNull(listener));
  }

  private void fireHighlightChange(Component component) {
    for (StatusListener listener : statusListeners) {
      listener.onHighlightChange(component);
    }
  }

  //--------------------------------------------------- Drag and Drop Handling.

  private class Dragged extends Status implements DragSourceListener {

    public void dragDropEnd(DragSourceDropEvent event) {
    }

    public void dragEnter(DragSourceDragEvent event) {
    }

    public void dragExit(DragSourceEvent event) {
    }

    public void dragOver(DragSourceDragEvent event) {
    }

    public void dropActionChanged(DragSourceDragEvent event) {
    }
  }

  @Override
  public void dragEnter(DropTargetDragEvent event) { }

  @Override
  public void dragExit(DropTargetEvent event) { }

  @Override
  public void dragOver(DropTargetDragEvent event) { }

  @Override
  public void drop(DropTargetDropEvent event) {
    try {
    event.acceptDrop(event.getDropAction());
    Point2D targetPoint = view.screen2schema(event.getLocation());
    float dx = (float)(targetPoint.getX() - dragSourcePoint.getX());
    float dy = (float)(targetPoint.getY() - dragSourcePoint.getY());
    Transferable transferable = event.getTransferable();
    Component component = (Component)transferable.getTransferData(
        transferable.getTransferDataFlavors()[0]);
    component.move((int)dx, (int)dy);
    event.dropComplete(true);
    view.repaint(view.getBounds());
    System.out.println("drop");
    } catch (Exception e) {
    }
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent event) {}

  private Point2D dragSourcePoint;

  /**
   *  Listener to the drag gesture recognizer.
   */
  @Override
  public void dragGestureRecognized(DragGestureEvent event) {
    System.out.println("Geture recognized"); // TODO:
    // Starts the drag
    dragSourcePoint = view.screen2schema(event.getDragOrigin());
    event.getDragSource().startDrag(
        event, null, new DragHandler(highlited), new Dragged());
  }

  //-------------------------------------------------------------------- Debug.

  public static void main(String[] args) throws Exception {
    javax.swing.JFrame frame = new javax.swing.JFrame("Schema demo");
    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    Schema model = new Schema();
    Component resistor = new Component(new Resistor())
      .move(2, 3)
      .rotate(1);
    model.add(resistor);

    SymbolLibrary library = new SymbolLibrary();
    library.load(new java.io.File("symbols.xml"));
    Component line = new Component(library.get("resistor"));
    model.add(line);

    SchemaDrawing view = new SchemaDrawing(model);
    Controller controller = new Controller(view, model);
    frame.getContentPane().add(view);
    frame.pack();
    frame.setVisible(true);
    javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
    javax.swing.Action action = new RotateAction();
    popup.add(action);
    controller.addStatusListener((StatusListener)action);
    view.setComponentPopupMenu(popup);
  }

}
