/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.asciiart;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

public class TranslatedCharArea implements UmlCharArea {

	private final int dx;
	private final int dy;
	private final UmlCharArea charArea;

	public TranslatedCharArea(UmlCharArea charArea, int dx, int dy) {
		this.charArea = charArea;
		this.dx = dx;
		this.dy = dy;
	}

	public void drawBoxSimple(int x, int y, int width, int height) {
		charArea.drawBoxSimple(x + dx, y + dy, width, height);

	}

	public void drawBoxSimpleUnicode(int x, int y, int width, int height) {
		charArea.drawBoxSimpleUnicode(x + dx, y + dy, width, height);
	}
	
	public void drawNoteSimple(int x, int y, int width, int height) {
		charArea.drawNoteSimple(x + dx, y + dy, width, height);
	}

	public void drawNoteSimpleUnicode(int x, int y, int width, int height) {
		charArea.drawNoteSimpleUnicode(x + dx, y + dy, width, height);
	}


	public void drawShape(AsciiShape shape, int x, int y) {
		charArea.drawShape(shape, x + dx, y + dy);
	}
	
	public void drawChar(char c, int x, int y) {
		charArea.drawChar(c, x + dx, y + dy);
	}

	public void drawHLine(char c, int line, int col1, int col2) {
		charArea.drawHLine(c, line + dy, col1 + dx, col2 + dx);
	}
	public void drawHLine(char c, int line, int col1, int col2, char ifFound, char thenUse) {
		charArea.drawHLine(c, line + dy, col1 + dx, col2 + dx, ifFound, thenUse);
	}

	public void drawStringLR(String string, int x, int y) {
		charArea.drawStringLR(string, x + dx, y + dy);
	}

	public void drawStringTB(String string, int x, int y) {
		charArea.drawStringTB(string, x + dx, y + dy);
	}

	public void drawVLine(char c, int col, int line1, int line2) {
		charArea.drawVLine(c, col + dx, line1 + dy, line2 + dy);
	}

	public int getHeight() {
		return charArea.getHeight();
	}

	public int getWidth() {
		return charArea.getWidth();
	}

	public String getLine(int line) {
		return charArea.getLine(line);
	}

	public List<String> getLines() {
		return charArea.getLines();
	}

	public void print(PrintStream ps) {
		charArea.print(ps);
	}

	public void drawStringsLRSimple(Collection<? extends CharSequence> strings, int x, int y) {
		charArea.drawStringsLRSimple(strings, x + dx, y + dy);
	}

	public void drawStringsLRUnicode(Collection<? extends CharSequence> strings, int x, int y) {
		charArea.drawStringsLRUnicode(strings, x + dx, y + dy);
	}

	public void fillRect(char c, int x, int y, int width, int height) {
		charArea.fillRect(c, x + dx, y + dy, width, height);
	}



}
