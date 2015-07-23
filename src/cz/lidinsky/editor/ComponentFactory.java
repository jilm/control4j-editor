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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Properties;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import control4j.gui.components.*;
import control4j.gui.GuiObject;
import static control4j.tools.Logger.*;

/**
 *
 *  Creates instances of new components. This class is singleton.
 *
 */
public class ComponentFactory {

  private static final ComponentFactory instance = new ComponentFactory();

  private static HashMap<String, String> components;

  private static final String filename = "guicomponents.csv";

  private static final String delimiter = ",";

  private static Set<String> componentKeys;

  private ComponentFactory() {
    loadComponentList();
  }

  /**
   *  Returns instance of the component factory
   */
  public static ComponentFactory getInstance() {
    return instance;
  }

  /**
   *  Returns a list of all the components in human readable form.
   */
  public Collection<String> getComponentList() {
    return componentKeys;
  }

  public GuiObject createInstance(String name) {
    try {
      return (GuiObject)Class.forName(components.get(name))
        .newInstance();
    } catch (InstantiationException e) {
      // should not happen
      assert false;
    } catch (IllegalAccessException e) {
      // should not happen as well
      assert false;
    } catch (ClassNotFoundException e) {
      assert false;
    }
    return null;
  }

  /**
   *  Loads component list from a file.
   */
  private void loadComponentList() {

    // load component keys
    Properties settings = Editor.getSettings();
    String componentList = settings.getProperty("components");
    String[] componentArray = componentList.split(" ");
    componentKeys = new HashSet<String>();
    for (String component : componentArray) {
      componentKeys.add(component);
    }
    // load component class names
    components = new HashMap<String, String>();
    for (String key : componentKeys) {
      String className = settings.getProperty(key + "_component_class");
      components.put(key, className);
    }
  }

}
