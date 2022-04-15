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
package net.sourceforge.plantuml.salt.element;

import java.util.List;

import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Grid2 {

	private final List<Double> rowsStart;
	private final List<Double> colsStart;
	private final TableStrategy strategy;

	public Grid2(List<Double> rowsStart, List<Double> colsStart, TableStrategy strategy) {
		this.rowsStart = rowsStart;
		this.colsStart = colsStart;
		this.strategy = strategy;
	}

	public void drawU(UGraphic ug) {
		final double xmin = colsStart.get(0);
		final double xmax = colsStart.get(colsStart.size() - 1);
		final double ymin = rowsStart.get(0);
		final double ymax = rowsStart.get(rowsStart.size() - 1);
		if (strategy == TableStrategy.DRAW_OUTSIDE || strategy == TableStrategy.DRAW_OUTSIDE_WITH_TITLE) {
			ug.apply(new UTranslate(xmin, ymin)).draw(ULine.hline(xmax - xmin));
			ug.apply(new UTranslate(xmin, ymax)).draw(ULine.hline(xmax - xmin));
			ug.apply(new UTranslate(xmin, ymin)).draw(ULine.vline(ymax - ymin));
			ug.apply(new UTranslate(xmax, ymin)).draw(ULine.vline(ymax - ymin));
		}
		if (drawHorizontal()) {
			for (Double y : rowsStart) {
				ug.apply(new UTranslate(xmin, y)).draw(ULine.hline(xmax - xmin));
			}
		}
		if (drawVertical()) {
			for (Double x : colsStart) {
				ug.apply(new UTranslate(x, ymin)).draw(ULine.vline(ymax - ymin));
			}
		}
	}

	private boolean drawHorizontal() {
		if (strategy == TableStrategy.DRAW_HORIZONTAL || strategy == TableStrategy.DRAW_ALL) {
			return true;
		}
		return false;
	}

	private boolean drawVertical() {
		if (strategy == TableStrategy.DRAW_VERTICAL || strategy == TableStrategy.DRAW_ALL) {
			return true;
		}
		return false;
	}

}
