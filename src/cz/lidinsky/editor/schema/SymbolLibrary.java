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
import static cz.lidinsky.tools.Validate.notBlank;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.svg.SVGHandler;
import cz.lidinsky.tools.svg.SVGLineElement;
import cz.lidinsky.tools.xml.AXMLDefaultUri;
import cz.lidinsky.tools.xml.AXMLStartElement;
import cz.lidinsky.tools.xml.AXMLEndElement;
import cz.lidinsky.tools.xml.IXMLHandler;
import cz.lidinsky.tools.xml.XMLReader;

import java.awt.geom.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.xml.sax.Attributes;

/**
 *  Keeps a collection of schema symbols. Each symbol is identified by a unique
 *  identifier.
 *
 *  <p>The collection may be loaded from a file.
 */
@AXMLDefaultUri("http://www.w3.org/2000/svg")
public class SymbolLibrary implements IXMLHandler {

  public SymbolLibrary() { }

  public static final String SYMBOL_NS = "http://control4j.lidinsky.cz/schema";

  /** Internal storage of all of the symbols. */
  private Map<String, Symbol> library = new HashMap<String, Symbol>();

  public void load(File file) {
    XMLReader reader = new XMLReader();
    reader.addHandler(this);
    reader.load(file);
  }

  public void put(String id, Symbol symbol) {
    library.put(notBlank(id), notNull(symbol));
  }

  public Set<String> getIds() {
    return library.keySet();
  }

  public Symbol get(String id) {
    return library.get(id);
  }

  //------------------------------------- IXMLHandler Interface Implementation.

  @AXMLStartElement("*")
  public boolean startSymbol(Attributes attributes) {
    return true;
  }

  @AXMLEndElement("*")
  public boolean endSymbol() {
    return true;
  }

  private String symbolId;
  private AWTSymbol symbol;

  @AXMLStartElement("g")
  public boolean startG(Attributes attributes) {
    String symbolId = attributes.getValue(SYMBOL_NS, "id");
    if (symbolId != null) {
      this.symbolId = symbolId;
      this.symbol = new AWTSymbol();
    }
    return true;
  }

  @AXMLEndElement("g")
  public boolean endG() {
    if (symbol != null) {
      put(symbolId, symbol);
      symbol = null;
      symbolId = null;
    }
    return true;
  }

  @AXMLStartElement("line")
  public boolean startLine(Attributes attributes) {
    symbol.add(new Line2D.Float(
          Float.parseFloat(attributes.getValue("x1")),
          Float.parseFloat(attributes.getValue("y1")),
          Float.parseFloat(attributes.getValue("x2")),
          Float.parseFloat(attributes.getValue("y2"))));
    return true;
  }

  @AXMLStartElement("rect")
  public boolean startRect(Attributes attributes) {
    symbol.add(new Rectangle2D.Float(
          Float.parseFloat(attributes.getValue("x")),
          Float.parseFloat(attributes.getValue("y")),
          Float.parseFloat(attributes.getValue("width")),
          Float.parseFloat(attributes.getValue("height"))));
    return true;
  }

  @AXMLStartElement("{http://control4j.lidinsky.cz/schema}terminal")
  public boolean startTerminal(Attributes attributes) {
    if (symbol != null) {
      symbol.add(new Terminal(
            Integer.parseInt(attributes.getValue("x")),
            Integer.parseInt(attributes.getValue("y"))));
    }
    return true;
  }

  @AXMLEndElement("{http://control4j.lidinsky.cz/schema}terminal")
  public boolean endTerminal() {
    return true;
  }

  @Override
  public void startProcessing() {}

  @Override
  public void endProcessing() {}

}
