/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
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
import java.util.List;

public interface BasicCharArea {

	int getWidth();

	int getHeight();

	void drawChar(char c, int x, int y);

	void fillRect(char c, int x, int y, int width, int height);

	void drawStringLR(String string, int x, int y);

	void drawStringTB(String string, int x, int y);

	String getLine(int line);

	void print(PrintStream ps);

	List<String> getLines();

	void drawHLine(char c, int line, int col1, int col2);
	void drawHLine(char c, int line, int col1, int col2, char ifFound, char thenUse);

	void drawVLine(char c, int col, int line1, int line2);

}
