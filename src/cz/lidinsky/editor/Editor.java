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

import java.util.HashMap;
import java.util.Properties;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JTable;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Container;
import java.awt.Component;

import control4j.gui.Screens;
import control4j.gui.components.Screen;
import control4j.gui.components.Circle;
import control4j.gui.components.*;
//import control4j.gui.Writer;
import control4j.gui.changers.*;
import control4j.gui.GuiObject;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;
import control4j.gui.Changer;

import control4j.scanner.Scanner;
import static control4j.tools.Logger.*;

import cz.lidinsky.tools.tree.DFSIterator;
import cz.lidinsky.tools.tree.Node;
import cz.lidinsky.tools.CommonException;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.PredicateUtils;

/**
 *
 *  GUI visual editor.
 *
 */
public class Editor
  implements
    TreeSelectionListener,
    TreeModelListener,
    FileListener {

  /** Main frame of the Editor. */
  private JFrame frame;

  private Tree guiStructureTree;
  /** Data model. */
  private TreeModel treeModel;
  private JTable propertyTable;
  private KeyValueTableModel propertyTableModel;
  private ComponentToTreeLink componentToTreeLink;
  private JSplitPane split;

  private FileHandling file;

  private EditHandling edit;
  //private LayoutHandling layout;

  /** filename that was given from the command line */
  private String filename;

  /** this object is singleton */
  private static Editor instance;

  /** The name of the file with the application settings. */
  private static final String settingsFilename = "setting.properties";

  /** Settings of the application. */
  private Properties settings = new Properties();

  /**
   *  Entry point of the editor.
   */
  public static void main(String[] args) throws Exception {
    // create an instance of the editor
    instance = new Editor();
    // load settings file
    java.io.InputStream settingsIS
      = instance.getClass().getResourceAsStream(settingsFilename);
    instance.settings.load(settingsIS);
    settingsIS.close();
    // create and show gui
    instance.createMainFrame();
    // load file from the command line
    if (args.length > 0) instance.filename = args[0];
    javax.swing.SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          if (instance.filename != null) {
            try {
              java.io.File file = new java.io.File(instance.filename);
              instance.file.load(file);
            } catch (Exception e) {
              throw new CommonException()
                .setCause(e)
                .set("message", "Exception while loading a file with gui!")
                .set("file name", instance.filename);
            }
          } else {
            instance.file.fileNew();
          }
        }
      }
    );
    // show the window
    instance.show();
  }

  /**
   *  An empty, private constructor to prevent class instantiation.
   */
  private Editor() {
    super();
  }

  /**
   *  Returns instance of this class.
   */
  public static Editor getInstance() {
    return instance;
  }

  /**
   *  Returns the root frame of the editor window.
   */
  public static JFrame getMainFrame() {
    return instance.frame;
  }

  /**
   *  Create main frame of the editor
   */
  protected void createMainFrame() {

    // Create and set up the window
    frame = new JFrame("GUI Editor");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // split the main window
    split = new JSplitPane();
    frame.add(split);

    // split the right side
    JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    split.setBottomComponent(rightSplit);

    // Add component tree navigator
    treeModel = new TreeModel();
    guiStructureTree = new Tree(treeModel);
    guiStructureTree.getSelectionModel().setSelectionMode(
      TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    guiStructureTree.setDragEnabled(true);
    guiStructureTree.setTransferHandler(new TreeTransferHandler());
    JScrollPane treeScroll = new JScrollPane(guiStructureTree);
    rightSplit.setLeftComponent(treeScroll);
    treeModel.addTreeModelListener(this);

    // add screens component
    split.setLeftComponent(treeModel.createRootVisualComponent());
    treeModel.configureRootVisualComponent();

    // Create file handling class
    file = new FileHandling(frame);
    file.addFileEventListener(treeModel);
    file.addFileEventListener(this);

    // Create edit handling class
    edit = new EditHandling(guiStructureTree);

    // Create layout handling class
    //layout = new LayoutHandling(guiStructureTree);

    // Add property editor table
    propertyTableModel = new KeyValueTableModel();
    propertyTable = new JTable(propertyTableModel);
    propertyTable.setDefaultRenderer(Object.class
            , new ObjectPropertiesTableRenderer());
    TableCellEditor tableCellEditor = new TableCellEditor();
    propertyTable.setDefaultEditor(Object.class, tableCellEditor);
    guiStructureTree.addTreeSelectionListener(tableCellEditor);
    JScrollPane tableScroll = new JScrollPane(propertyTable);
    rightSplit.setBottomComponent(tableScroll);
    guiStructureTree.addTreeSelectionListener(this);
    propertyTableModel.addTableModelListener(guiStructureTree);

    // Add a link component -> tree
    componentToTreeLink = new ComponentToTreeLink(guiStructureTree);

    // Add a menu
    Menu menuBar = new Menu(settings);
    frame.setJMenuBar(menuBar);
    // Edit menu
    edit.addMenu(menuBar);
  }

  /**
   *  Show the main window.
   */
  protected void show() {
    javax.swing.SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          frame.pack();
          frame.setVisible(true);
        }
      }
    );
  }

  /**
   *  If selection in the tree window is changed, show approprite
   *  values in the property table.
   *
   *  <p>Moreover show appropriate selected screen.
   *
   *  <p>This method is listening tree selection event.
   */
  public void valueChanged(TreeSelectionEvent e) {
    GuiObject selected = guiStructureTree.getSelectedGO();
    if (selected != null) {
      propertyTableModel.setData(selected);
      // select appropriate selected screen
      //if (selectedPath.getPathCount() > 1) {
        //Screen screen
          //= (Screen)((Node<GuiObject>)selectedPath.getPathComponent(1))
          //.getDecorated();
        //((Screens)treeModel.getRoot().getDecorated()).showScreen(screen);
      //}
      // TODO:
    } else {
      propertyTableModel.setData(null);
    }
    // cancel table editing
    if (propertyTable.isEditing()) {
      propertyTable.getCellEditor().cancelCellEditing();
    }
  }

  /**
   *
   */
  public String letSelectComponent()
  {
    // get array of component names
    Collection<String> componentNames
      = ComponentFactory.getInstance().getComponentList();
    String[] componentNamesArray = componentNames.toArray(
        new String[componentNames.size()]);
    System.out.println(componentNamesArray.toString());
    // show input dialog
    String selected = (String)JOptionPane.showInputDialog(
      frame, "Select component", "Components", JOptionPane.QUESTION_MESSAGE,
      null, componentNamesArray, componentNamesArray[0]);
    return selected;
  }

  /**
   *
   */
  protected String letSelectChanger()
  {
    // get array of changer names
    String[] changerNames = ChangerFactory.getInstance().getList();
    // show input dialog
    String selected = (String)JOptionPane.showInputDialog(
      frame, "Select changer", "Changers", JOptionPane.QUESTION_MESSAGE,
      null, changerNames, changerNames[0]);
    return selected;
  }

  public void treeNodesChanged(TreeModelEvent e)
  {

  }

  /**
   *  Add a link component -> tree to each new node.
   *  And select last inserted node.
   */
  public void treeNodesInserted(TreeModelEvent e) {
    // add a link component -> tree
    TreePath parentPath = e.getTreePath();
    Node<GuiObject> parent = (Node<GuiObject>)parentPath.getLastPathComponent();
    int[] indexes = e.getChildIndices();
    for (int i : indexes) {
      Node<GuiObject> child = parent.getChild(i);
      setComponent2TreeLink(child);
    }
    // select last inserted node
    Node<GuiObject> child = parent.getChild(indexes[indexes.length-1]);
    guiStructureTree.setSelectionPath(parentPath.pathByAddingChild(child));
  }

  private void setComponent2TreeLink(Node<GuiObject> node) {
    if (node.getDecorated().isVisual()) {
      JComponent visualComponent
        = ((VisualObject)node.getDecorated()).getVisualComponent();
      if (visualComponent != null) {
        visualComponent.addMouseListener(componentToTreeLink);
        for (Node<GuiObject> child : node.getChildren()) {
          setComponent2TreeLink(child);
        }
      }
    }
  }

  public void treeNodesRemoved(TreeModelEvent e)
  {
  }

  /**
   *  If there is new Screens object, add it to the window
   */
  public void treeStructureChanged(TreeModelEvent e)
  {
  }

  /**
   *
   */
  public void fileChanged(FileEvent e)
  {
    // release old screens object
    //if (screens != null)
      //screens.releaseVisualComponent();
    // show new screens object in the main window
    //screens = e.getScreens();
    //JComponent visualComponent = screens.createVisualComponent();
    //split.setLeftComponent(visualComponent);
    //screens.configureVisualComponent();
    // add a link component -> tree structure to each of the components
    //Iterable<GuiObject> components
      //= IteratorUtils.asIterable( IteratorUtils.filteredIterator(
            //new DFSIterator<GuiObject>(screens),
            //PredicateUtils.instanceofPredicate(VisualObject.class)));
    //for (GuiObject component : components) {
      //((VisualObject)component).getVisualComponent()
        //.addMouseListener(componentToTreeLink);
    //}
  }

  public static TreeModel getTreeModel() {
    return instance.treeModel;
  }

  /**
   *  Returns file handling object.
   */
  public FileHandling getFileHandling() {
    return file;
  }

  public static Screens getScreens() {
    return (Screens)(instance.treeModel.getRoot()).getDecorated();
  }

  public static DataModel getDataModel() {
    return instance.treeModel;
  }

  public static Tree getComponentTree() {
    return instance.guiStructureTree;
  }

  public static Properties getSettings() {
    return instance.settings;
  }

}
