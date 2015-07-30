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

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Color;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.lang.reflect.Method;

public class ObjectPropertiesTableRenderer
extends DefaultTableCellRenderer
implements TableCellRenderer
{
  private Border unselectedBorder = null;
  private Border selectedBorder = null;
  private Color defaultColor = null;
  private JCheckBox checkBox = new JCheckBox();

  public ObjectPropertiesTableRenderer()
  {
    super();
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
  {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    System.out.println(value);
    if (value == null)
    {
      return super.getTableCellRendererComponent(table, "", isSelected
          , hasFocus, row, column);
    }
    else if (value instanceof Number)
    {
      super.getTableCellRendererComponent(table, value, isSelected
          , hasFocus, row, column);
      setHorizontalAlignment(RIGHT);
      return this;
    }
    else if (value instanceof control4j.gui.Color)
    {
      String strValue = ((control4j.gui.Color)value).getKey();
      super.getTableCellRendererComponent(table, strValue, isSelected,
          hasFocus, row, column);
      setHorizontalAlignment(LEFT);
      return this;
    }
    else if (value instanceof java.awt.Color)
    {
      return colorRenderer.getTableCellRendererComponent(table, value
          , isSelected, hasFocus, row, column);
    }
    else if (value instanceof Method)
    {
      super.getTableCellRendererComponent(table, ((Method)value).getName(),
          isSelected, hasFocus, row, column);
      setHorizontalAlignment(LEFT);
      return this;
    }
    else if (value instanceof Boolean)
    {
      checkBox.setSelected(((Boolean)value).booleanValue());
      return checkBox;
    }
    else
    {
      super.getTableCellRendererComponent(table, value, isSelected,
          hasFocus, row, column);
      setHorizontalAlignment(LEFT);
      return this;
    }
  }

  private static final DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer()
  {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      setBackground((Color)value);
      return this;
    }

  };
}
