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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class RotateAction extends AbstractAction implements StatusListener {

  private Component highlighted;

  public RotateAction() {
    putValue(NAME, "Rotate");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (highlighted != null) {
      highlighted.rotate(1);
    }
  }

  public void onHighlightChange(Component component) {
    setEnabled(component != null);
    highlighted = component;
  }

}
