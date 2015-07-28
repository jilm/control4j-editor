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

import java.io.OutputStream;
import java.awt.Color;

import java.util.Map;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import control4j.scanner.Scanner;
import control4j.scanner.Item2;
import control4j.gui.components.Screen;
import control4j.gui.GuiObject;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;
import control4j.gui.Changer;

import cz.lidinsky.tools.tree.Node;

/**
 *
 *  Writes given gui into the output streem in XML format.
 *
 */
public class Writer {

  /**
   *
   */
  public static final String NS = "http://control4j.lidinsky.cz/gui";

  private XMLStreamWriter writer;

  /**
   *  Writes given gui into the given output stream in the XML format.
   */
  public void write(Node<GuiObject> root, OutputStream outputStream)
    throws XMLStreamException {

      // create XML strem writer
      writer = javax.xml.stream.XMLOutputFactory.newFactory()
        .createXMLStreamWriter(outputStream);

      // insert root element
      writer.writeStartDocument();
      writer.writeStartElement("gui", "gui", NS);
      writer.writeNamespace("gui", NS);
      //writePreferences(gui);

      // write children
      for (Node<GuiObject> child : root.getChildren()) {
        writeNode(child);
      }

      // finish the document
      writer.writeEndDocument();
      writer.close();

    }

  protected void writeNode(Node<GuiObject> node)
    throws XMLStreamException {
      // write appropriate start element
      GuiObject object = node.getDecorated();
      if (object instanceof Screen) {
        writeScreen((Screen)object);
      } else if (object instanceof VisualContainer) {
        writeContainer((VisualContainer)object);
      } else if (object instanceof VisualObject) {
        writeObject((VisualObject)object);
      } else if (object instanceof Changer) {
        writeChanger((Changer)object);
      } else {
        throw new AssertionError();
      }
      // write all of the children objects
      for (Node<GuiObject> child : node.getChildren()) {
        writeNode(child);
      }
      // close this node element
      writer.writeEndElement();
    }

  /**
   *
   */
  private void writeScreen(Screen screen)
  throws XMLStreamException {
    writer.writeStartElement("gui", "screen", NS);
    writer.writeAttribute("class", screen.getClass().getName());
    writePreferences(screen);
  }

  /**
   *
   */
  private void writeChanger(Changer changer)
  throws XMLStreamException {
    writer.writeStartElement("gui", "changer", NS);
    writer.writeAttribute("class", changer.getClass().getName());
    writePreferences(changer);
  }

  /**
   *
   */
  private void writeObject(VisualObject object)
  throws XMLStreamException {
    writer.writeStartElement("gui", "component", NS);
    writer.writeAttribute("class", object.getClass().getName());
    writePreferences(object);
  }

  /**
   *
   */
  private void writeContainer(VisualContainer container)
  throws XMLStreamException {
    writer.writeStartElement("gui", "panel", NS);
    writer.writeAttribute("class", container.getClass().getName());
    writePreferences(container);
  }

  /**
   *
   */
  private void writePreferences(GuiObject object)
    throws XMLStreamException {
      // find all of the getters for a given object class
      Map<String, Item2> preferences = Scanner.scanClass(object.getClass());
      for (Item2 preference : preferences.values())
        try
        {
          // get key and value
          String key = preference.getKey();
          Object value = preference.getValue(object);
          if (value == null) continue;
          // convert value into string
          String strValue = null;
          if (value instanceof control4j.gui.Color)
            strValue = ((control4j.gui.Color)value).getKey();
          else if (value instanceof java.awt.Color)
            strValue = Integer.toString(((Color)value).getRGB());
          else
            strValue = value.toString();
          // write preference
          writePreference(key, strValue);
        }
      catch (java.lang.reflect.InvocationTargetException e)
      {
      }
      catch (java.lang.IllegalAccessException e)
      {
      }
    }

  /**
   *
   */
  private void writePreference(String key, String value)
  throws XMLStreamException
  {
    writer.writeEmptyElement("gui", "preference", NS);
    writer.writeAttribute("key", key);
    writer.writeAttribute("value", value);
    //writer.writeEndElement();
  }
}
