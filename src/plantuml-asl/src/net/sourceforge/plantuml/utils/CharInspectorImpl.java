/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.utils;

public class CharInspectorImpl implements CharInspector {
	// ::remove file when __HAXE__

	final private BlocLines data;
	final private boolean insertNewlines;
	private int line = 0;
	private int pos = 0;

	CharInspectorImpl(BlocLines input, boolean insertNewlines) {
		this.data = input;
		this.insertNewlines = insertNewlines;
	}

	@Override
	public char peek(int ahead) {
		if (line == -1)
			return 0;
		final String currentLine = getCurrentLine();
		if (pos + ahead >= currentLine.length())
			return '\0';
		return currentLine.charAt(pos + ahead);
	}

	private String getCurrentLine() {
		if (insertNewlines)
			return data.getAt(line).getTrimmed().getString() + "\n";
		return data.getAt(line).getTrimmed().getString();
	}

	@Override
	public void jump() {
		if (line == -1)
			throw new IllegalStateException();
		pos++;
		if (pos >= getCurrentLine().length()) {
			line++;
			pos = 0;
		}
		while (line < data.size() && getCurrentLine().length() == 0)
			line++;
		if (line >= data.size())
			line = -1;
	}
}
