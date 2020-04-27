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

public enum AsciiShape {

	// int STICKMAN_HEIGHT = 5;
	// int STICKMAN_UNICODE_HEIGHT = 6;

	STICKMAN(3, 5), STICKMAN_UNICODE(3, 6), BOUNDARY(8, 3), DATABASE(10, 6);

	private final int width;
	private final int height;

	private AsciiShape(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void draw(BasicCharArea area, int x, int y) {
		if (this == STICKMAN) {
			drawStickMan(area, x, y);
		} else if (this == STICKMAN_UNICODE) {
			drawStickManUnicode(area, x, y);
		} else if (this == BOUNDARY) {
			drawBoundary(area, x, y);
		} else if (this == DATABASE) {
			drawDatabase(area, x, y);
		}
	}

	private void drawDatabase(BasicCharArea area, int x, int y) {
		area.drawStringLR(" ,.-^^-._", x, y++);
		area.drawStringLR("|-.____.-|", x, y++);
		area.drawStringLR("|        |", x, y++);
		area.drawStringLR("|        |", x, y++);
		area.drawStringLR("|        |", x, y++);
		area.drawStringLR("'-.____.-'", x, y++);
	}

	private void drawDatabaseSmall(BasicCharArea area, int x, int y) {
		area.drawStringLR(" ,.-\"-._ ", x, y++);
		area.drawStringLR("|-.___.-|", x, y++);
		area.drawStringLR("|       |", x, y++);
		area.drawStringLR("|       |", x, y++);
		area.drawStringLR("|       |", x, y++);
		area.drawStringLR("'-.___.-'", x, y++);
	}

	private void drawBoundary(BasicCharArea area, int x, int y) {
		area.drawStringLR("|   ,-.", x, y++);
		area.drawStringLR("+--{   )", x, y++);
		area.drawStringLR("|   `-'", x, y++);
	}

	private void drawStickMan(BasicCharArea area, int x, int y) {
		area.drawStringLR(",-.", x, y++);
		area.drawStringLR("`-'", x, y++);
		area.drawStringLR("/|\\", x, y++);
		area.drawStringLR(" | ", x, y++);
		area.drawStringLR("/ \\", x, y++);
	}

	private void drawStickManUnicode(BasicCharArea area, int x, int y) {
		area.drawStringLR("\u250c\u2500\u2510", x, y++);
		area.drawStringLR("\u2551\"\u2502", x, y++);
		area.drawStringLR("\u2514\u252c\u2518", x, y++);
		area.drawStringLR("\u250c\u253c\u2510", x, y++);
		area.drawStringLR(" \u2502 ", x, y++);
		area.drawStringLR("\u250c\u2534\u2510", x, y++);
	}

	public final int getHeight() {
		return height;
	}

	public final int getWidth() {
		return width;
	}

}
