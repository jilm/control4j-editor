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
import cz.lidinsky.tools.swing.Transform;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;

public class Resistor implements Symbol {

  public Resistor() {
    terminals = new ArrayList<Terminal>();
    terminals.add(new Terminal(0, 1));
    terminals.add(new Terminal(2, 1));
  }

  public int getWidth() {
    return 2;
  }

  public int getHeight() {
    return 2;
  }

  @Override
  public void paint(Graphics2D g) {
    new Transform(g).scale(1d / 100d);
    g.drawLine(0, 100, 33, 100);
    g.drawLine(167, 100, 200, 100);
    g.drawRect(33, 73, 134, 53);
  }

  private ArrayList<Terminal> terminals;

  @Override
  public Collection<Terminal> getTerminals() {
    return terminals;
  }

}
