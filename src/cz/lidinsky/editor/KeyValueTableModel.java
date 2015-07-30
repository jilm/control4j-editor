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

import control4j.gui.GuiObject;
import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.reflect.ObjectMapDecorator;
import cz.lidinsky.tools.reflect.ObjectMapUtils;
import cz.lidinsky.tools.reflect.Setter;
import cz.lidinsky.tools.reflect.Getter;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *  Model for table that is cappable to show results of scanner.
 *  The table has only two columns, named key and value.
 *  The datatype of each cell in value column don't have to be the
 *  same.
 */
public class KeyValueTableModel implements TableModel {

  /**
   *  Source of the data.
   */
  private ObjectMapDecorator<Object> map;

  /**
   *  Index which map keys of the object to the rows of the table.
   */
  private ArrayList<String> keyIndex = new ArrayList<String>();

  /**
   *  Initialize the internal data structures.
   */
  public KeyValueTableModel() {
    map = new ObjectMapDecorator(Object.class)
      .setGetterFilter(
          ObjectMapUtils.hasAnnotationPredicate(Getter.class))
      .setSetterFilter(
          ObjectMapUtils.hasAnnotationPredicate(Setter.class))
      .setGetterKeyTransformer(
          ObjectMapUtils.getGetterValueTransformer())
      .setSetterKeyTransformer(
          ObjectMapUtils.getSetterValueTransformer())
      .setSetterFactory(
          ObjectMapUtils.setterClosureFactory(true))
      .setGetterFactory(
          ObjectMapUtils.getterFactory(true));
  }

  /**
   *  Sets new data to be shown in the table. Previously shown
   *  data are removed.
   *
   *  @param object
   *             the data to be shown
   */
  public void setData(GuiObject object) {
    map.setDecorated(object);
    keyIndex.clear();
    keyIndex.addAll(map.keySet());
    fireTableChanged();
  }

  /**
   *  Return datatype for the specific cell.
   *
   *  @param row
   *             index of the row
   *
   *  @param column
   *             index of the column, may be 0 ro 1
   */
  public Class<?> getCellClass(int row, int column) {
    if (column == 0) {
      return String.class;
    } else {
      return map.getDataType(keyIndex.get(row));
    }
  }

  //-------------------------------------- TableModel Interface Implementation.

  /**
   *  Returns the data type of the column. For index 0 it returns String,
   *  and for index 1 it returns Object. If you need the datatype more
   *  specificaly, call the method getCellClass.
   *
   *  @param columnIndex
   *             index of the column, may be 0 or 1
   *
   *  @see #getCellClass
   */
  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0) {
      return String.class;
    } else {
      return Object.class;
    }
  }

  /**
   *  Always returns number 2.
   */
  public int getColumnCount() {
    return 2;
  }

  /**
   *  Returns names "Key" and "Value".
   */
  public String getColumnName(int columnIndex) {
    if (columnIndex == 0) {
      return "Key";
    } else {
      return "Value";
    }
  }

  public int getRowCount() {
    return keyIndex.size();
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (columnIndex == 0) {
      return keyIndex.get(rowIndex);
    } else {
      return map.get(keyIndex.get(rowIndex));
    }
  }

  /**
   *  Returns true, if there is a Setter method defined for the key.
   *  else returns false.
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (columnIndex == 0) {
      return false;
    } else {
      return map.isWritable(keyIndex.get(rowIndex));
    }
  }

  /**
   *  @throws IllegalArgumentException
   *             if the Setter method invocation fails
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    try {
      map.put(keyIndex.get(rowIndex), aValue);
      fireTableChanged(rowIndex);
    } catch (Exception e) {
      throw new CommonException()
        .setCause(e)
        .set("message",
            "An Exception while writing a value into the guiObject!")
        .set("value", aValue)
        .set("row", rowIndex)
        .set("column", columnIndex)
        .set("object", map.getDecorated())
        .set("key", keyIndex.get(rowIndex));
    }
  }

  //----------------------------------------------------------- Event Handling.

  /**
   *  List of event listeners
   */
  private Set<TableModelListener> listeners
                   = new HashSet<TableModelListener>();

  /**
   *  The table has changed completely.
   */
  protected void fireTableChanged() {
    TableModelEvent event = new TableModelEvent(this);
    for (TableModelListener listener : listeners) {
      listener.tableChanged(event);
    }
  }

  /**
   *  Only the value in the given row has been changed.
   */
  protected void fireTableChanged(int row) {
    TableModelEvent event = new TableModelEvent(this, row);
    for (TableModelListener listener : listeners) {
      listener.tableChanged(event);
    }
  }

  public void addTableModelListener(TableModelListener l) {
    if (l != null) listeners.add(l);
  }

  public void removeTableModelListener(TableModelListener l) {
    if (l != null) listeners.remove(l);
  }

}
