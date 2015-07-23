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

package cz.lidinsky.editor;

import java.util.LinkedList;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import control4j.gui.Writer;
import control4j.gui.Screens;

import cz.lidinsky.tools.xml.XMLReader;

/**
 *
 *  Provides file handling of the editor.
 *
 *  <p>This is the only object that can replace screens object.
 *
 */
public class FileHandling
implements FileEvent, DataListener, TreeModelListener
{

  /**
   *  File that was opened or saved last time.
   */
  private File file = null;

  /**
   *  Indicates that file has changed and need to be saved.
   */
  private boolean hasChanged = false;

  /**
   *  Loaded data.
   */
  private Screens screens;

  /**
   *  Listeners to the file events.
   */
  private final LinkedList<FileListener> listeners =
    new LinkedList<FileListener>();

  private JFrame frame;

  /**
   *
   */
  FileHandling(JFrame frame) {
    super();
    this.frame = frame;
  }

  /**
   *  Ask user if he realy wants to close a file without saving changes.
   *
   *  @return true if the action should be performed, and false if
   *             it should be canceled
   */
  public boolean askUser() {
    return true;
  }

  /**
   *  Response method for menu item file/new.
   */
  public void fileNew()
  {
    if (askUser())
    {
      screens = new Screens();
      file = null;
      hasChanged = false;  // nothing interesting to save
      //actualizeMenu();
      fireFileChangedEvent();
    }
  }

  /**
   *
   */
  public void load(final File file) throws IOException {
    try {
      XMLReader reader = new XMLReader();
      reader.addHandler(
          new control4j.application.gui.XMLHandler(
            new Gui2EditorAdapter(
              Editor.getDataModel())));
      reader.load(file);
      //reader.close();
      this.file = file;
    } catch (java.io.IOException e) {
      this.file = null;
      throw e;
    }
  }

  public void save() {
    save(file);
  }

  /**
   *  Response method for menu item file/save.
   */
  public void save(File file) {
    try {
      java.io.OutputStream os = new java.io.FileOutputStream(file);
      Writer writer = new Writer();
      writer.write(screens, os);
      hasChanged = false;
      this.file = file;
    } catch (java.io.FileNotFoundException ex) {
      file = null;
      // TODO:
    } catch (javax.xml.stream.XMLStreamException e) {
      // TODO:
    }
  }

  /**
   *  Response method for menu item file/save as.
   */
  protected void saveAs()
  {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showSaveDialog(frame);
    if (result == JFileChooser.APPROVE_OPTION)
    {
      file = fileChooser.getSelectedFile();
      save();
    }
    //actualizeMenu();
    fireFileChangedEvent();
  }

  /**
   *  Should be called whenever something has changed and file need to
   *  be saved.
   */
  public void dataChanged(Screens screens)
  {
    hasChanged = true;
    this.screens = screens;
  }

  public File getFile() {
    return file;
  }

  public Screens getScreens()
  {
    return screens;
  }

  protected void fireFileChangedEvent()
  {
    //for (FileListener listener : listeners)
      //listener.fileChanged(this);
  }

  public FileListener addFileEventListener(FileListener listener)
  {
    if (listener != null) listeners.add(listener);
    return listener;
  }

  public FileListener removeFileEventListener(FileListener listener)
  {
    if (listener != null) listeners.remove(listener);
    return listener;
  }

  public void treeNodesChanged(TreeModelEvent e)
  {
  }

  public void treeNodesInserted(TreeModelEvent e)
  {
  }

  public void treeNodesRemoved(TreeModelEvent e)
  {
  }

  public void treeStructureChanged(TreeModelEvent e)
  {
  }

}
