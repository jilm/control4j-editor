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

package cz.lidinsky.editor.actions;

import cz.lidinsky.editor.Action;
import cz.lidinsky.editor.Editor;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;


public class Load extends Action {

  @Override
  public void actionPerformed(ActionEvent event) {
    if (Editor.getInstance().getFileHandling().askUser()) {
      JFileChooser fileChooser = new JFileChooser();
      int result = fileChooser.showOpenDialog(
          Editor.getInstance().getMainFrame());
      if (result == JFileChooser.APPROVE_OPTION) {
        try {
          java.io.File file = fileChooser.getSelectedFile();
          //java.io.InputStream is = new java.io.FileInputStream(file);
          Editor.getInstance().getFileHandling().load(file);
        } catch (java.io.FileNotFoundException e) {
          // TODO:
        } catch (java.io.IOException e) {
          // TODO:
        }
      }
    }
  }

}
