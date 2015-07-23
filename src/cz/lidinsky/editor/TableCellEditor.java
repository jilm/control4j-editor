package cz.lidinsky.editor;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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

import java.lang.reflect.Method;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
//import javax.swing.table.TableCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import control4j.gui.Changer;
import control4j.scanner.KeyValueTableModel;
import control4j.scanner.Setter;

/**
 *  Choose and return appropriate edit component, for property table.
 *  The decision is based mainly on the datatype of the edited value.
 *  But may depend even on the class to which the property belongs to.
 */
public class TableCellEditor
extends AbstractCellEditor
implements javax.swing.table.TableCellEditor, ActionListener, TreeSelectionListener, java.awt.event.FocusListener
{

  private Color currentColor;
  private JButton button;
  private JColorChooser colorChooser;
  private JDialog dialog;
  private JTextField textField;
  private JComboBox comboBox;
  private JCheckBox checkBox;
  private int type;
  private final static int TEXT = 0;
  private final static int COLOR = 1;
  private final static int COMBO = 2;
  private final static int CHECK = 3;
  private Class valueClass;
  private Object selected;

  /**
   *  Initialize all of the components
   */
  public TableCellEditor()
  {
    // color chooser
    button = new JButton();
    button.setActionCommand("edit");
    button.addActionListener(this);
    button.setBorderPainted(false);
    button.addFocusListener(this);
    colorChooser = new JColorChooser();
    colorChooser.addChooserPanel(new AliasChooser());
    colorChooser.addFocusListener(this);
    dialog = JColorChooser.createDialog(button, "Pick a Color", true, colorChooser, this, null);
    // text editor
    textField = new JTextField();
    textField.addFocusListener(this);
    // combo chooser
    comboBox = new JComboBox();
    comboBox.addFocusListener(this);
    // check box
    checkBox = new JCheckBox();
    checkBox.addFocusListener(this);
  }

  /**
   *  Only for color chooser
   */
  public void actionPerformed(ActionEvent e)
  {
    if ("edit".equals(e.getActionCommand()))
    {
      button.setBackground(currentColor);
      colorChooser.setColor(currentColor);
      dialog.setVisible(true);
      fireEditingStopped();
    }
    else
      currentColor = colorChooser.getColor();
  }

  /**
   *  Returns new value
   */
  public Object getCellEditorValue()
  {
    if (type == TEXT)
    {
      String text = textField.getText();
      if (String.class.isAssignableFrom(valueClass))
        return text;
      if (double.class.isAssignableFrom(valueClass))
        return Double.parseDouble(text);
      if (int.class.isAssignableFrom(valueClass))
        return Integer.parseInt(text);
      return text;
    }
    else if (type == COLOR)
      return currentColor;
    else if (type == CHECK)
      return checkBox.isSelected();
    else
      return comboBox.getSelectedItem();
  }

  /**
   *  Returnes appropriate component based on the value datatype.
   */
  public Component getTableCellEditorComponent(JTable table, Object value,
  boolean isSelected, int row, int column)
  {
    KeyValueTableModel tableModel = (KeyValueTableModel)table.getModel();
    valueClass = tableModel.getCellClass(row, column);
    if (Color.class.isAssignableFrom(valueClass))
    {
      type = COLOR;
      currentColor = (Color)value;
      return button;
    }
    else if (selected instanceof Changer
    && ((String)table.getValueAt(row, 0)).equals("Property"))
    {
      type = COMBO;
      // get appropriate object
      Changer changer = (Changer)selected;
      // get parent of the object
      Object parent = changer.getParent();
      // get list of appropriate methods
      fillComboBox(parent, changer.getPropertyClass());
      // create component
      // return it
      return comboBox;
    }
    else if (Method.class.isAssignableFrom(valueClass)
    && selected instanceof Changer)
    {
      type = COMBO;
      // get appropriate object
      Changer changer = (Changer)selected;
      // get parent of the object
      Object parent = changer.getParent();
      // get list of appropriate methods
      fillComboBox(parent, changer.getPropertyClass());
      // create component
      // return it
      return comboBox;
    }
    else if (valueClass == boolean.class)
    {
      type = CHECK;
      checkBox.setSelected(((Boolean)value).booleanValue());
      return checkBox;
    }
    else
    {
      type = TEXT;
      if (value == null) value = "";
      textField.setText(value.toString());
      return textField;
    }
  }

  private void fillComboBox(Object object, Class _class)
  {
    comboBox.removeAllItems();
    control4j.scanner.MethodIterator methods
      = new control4j.scanner.MethodIterator(object, Setter.class, _class);
    for (Method method : methods)
    {
      Setter setter = method.getAnnotation(Setter.class);
      comboBox.addItem(setter.key());
    }
  }

  public void valueChanged(TreeSelectionEvent e)
  {
    TreePath selectedPath = e.getNewLeadSelectionPath();
    if (selectedPath != null)
      selected = selectedPath.getLastPathComponent();
    else
      selected = null;
  }

  @Override
  protected void fireEditingCanceled()
  {
    super.fireEditingCanceled();
  }

  public void focusGained(java.awt.event.FocusEvent e)
  {
  }

  public void focusLost(java.awt.event.FocusEvent e)
  {
    if (!e.isTemporary())
    {
      if (!(e.getOppositeComponent() instanceof JTable))
      {
        cancelCellEditing();
      }
    }
  }

  /**
   *
   */
  private class AliasChooser extends AbstractColorChooserPanel
  implements javax.swing.event.ListSelectionListener
  {

    protected void buildChooser()
    {
      JList list = new JList(ColorAliases.getAliases());
      list.addListSelectionListener(this);
      add(list);
    }

    public String getDisplayName()
    {
      return "Color class";
    }

    public Icon getLargeDisplayIcon()
    {
      return null;
    }

    public Icon getSmallDisplayIcon()
    {
      return null;
    }

    public void updateChooser()
    {
    }

    public void valueChanged(javax.swing.event.ListSelectionEvent e)
    {
      String alias = (String)((JList)e.getSource()).getSelectedValue();
      javax.swing.colorchooser.ColorSelectionModel model
        = getColorSelectionModel();
      model.setSelectedColor(control4j.gui.Color.getColor(alias));
    }

  }
}
