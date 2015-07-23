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

package cz.lidinsky.editor;

import org.apache.commons.lang3.StringUtils;

import java.awt.event.ActionEvent;
import java.util.Properties;
import javax.swing.AbstractAction;

public abstract class Action extends AbstractAction {

  public static Action getAction(Properties settings, String key)
    throws
      ClassNotFoundException,
      InstantiationException,
      IllegalAccessException {

    // get class name
    String className = settings.getProperty(key + "_action_class");
    // create class
    Class<Action> actionClass = (Class<Action>)Class.forName(className);
    // create instance
    Action action = actionClass.newInstance();
    // set text param
    setLabel(action, settings.getProperty(key + "_action_text"));
    // return action
    return action;
  }

  protected static void setLabel(final Action action, final String label) {
    if (label != null) {
      int mnemonicIndex = label.indexOf('_');
      if (mnemonicIndex >= 0) {
        String text = StringUtils.remove(label, '_');
        int key = text.codePointAt(mnemonicIndex);
        action.putValue(Action.NAME, text);
        action.putValue(Action.MNEMONIC_KEY, key);
      } else {
        action.putValue(Action.NAME, label);
      }
    }
  }

}
