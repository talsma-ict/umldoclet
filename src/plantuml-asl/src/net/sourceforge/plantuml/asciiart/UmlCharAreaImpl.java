/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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

import java.util.Collection;

public class UmlCharAreaImpl extends BasicCharAreaImpl implements UmlCharArea {

	public void drawBoxSimple(int x, int y, int width, int height) {
		this.drawHLine('-', y, x + 1, x + width - 1);
		this.drawHLine('-', y + height - 1, x + 1, x + width - 1);

		this.drawVLine('|', x, y + 1, y + height - 1);
		this.drawVLine('|', x + width - 1, y + 1, y + height - 1);

		this.drawChar(',', x, y);
		this.drawChar('.', x + width - 1, y);
		this.drawChar('`', x, y + height - 1);
		this.drawChar('\'', x + width - 1, y + height - 1);
	}

	public void drawBoxSimpleUnicode(int x, int y, int width, int height) {
		this.drawHLine('\u2500', y, x + 1, x + width - 1);
		this.drawHLine('\u2500', y + height - 1, x + 1, x + width - 1);

		this.drawVLine('\u2502', x, y + 1, y + height - 1);
		this.drawVLine('\u2502', x + width - 1, y + 1, y + height - 1);

		this.drawChar('\u250c', x, y);
		this.drawChar('\u2510', x + width - 1, y);
		this.drawChar('\u2514', x, y + height - 1);
		this.drawChar('\u2518', x + width - 1, y + height - 1);
	}

	public void drawShape(AsciiShape shape, int x, int y) {
		shape.draw(this, x, y);
	}

	public void drawStringsLR(Collection<? extends CharSequence> strings, int x, int y) {
		int i = 0;
		if (x < 0) {
			x = 0;
		}
		for (CharSequence s : strings) {
			this.drawStringLR(s.toString(), x, y + i);
			i++;
		}

	}

	public void drawNoteSimple(int x, int y, int width, int height) {
		this.drawHLine('-', y, x + 1, x + width - 1);
		this.drawHLine('-', y + height - 1, x + 1, x + width - 1);

		this.drawVLine('|', x, y + 1, y + height - 1);
		this.drawVLine('|', x + width - 1, y + 1, y + height - 1);

		this.drawChar(',', x, y);

		this.drawStringLR("!. ", x + width - 3, y);
		this.drawStringLR("|_\\", x + width - 3, y + 1);

		this.drawChar('`', x, y + height - 1);
		this.drawChar('\'', x + width - 1, y + height - 1);
	}

	public void drawNoteSimpleUnicode(int x, int y, int width, int height) {
		this.drawChar('\u2591', x + width - 2, y + 1);

		this.drawHLine('\u2550', y, x + 1, x + width - 1, '\u2502', '\u2567');
		this.drawHLine('\u2550', y + height - 1, x + 1, x + width - 1, '\u2502', '\u2564');

		this.drawVLine('\u2551', x, y + 1, y + height - 1);
		this.drawVLine('\u2551', x + width - 1, y + 1, y + height - 1);

		this.drawChar('\u2554', x, y);
		this.drawChar('\u2557', x + width - 1, y);
		this.drawChar('\u255a', x, y + height - 1);
		this.drawChar('\u255d', x + width - 1, y + height - 1);
	}
}
