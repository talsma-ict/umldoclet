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
package net.sourceforge.plantuml.salt;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.salt.element.Element;

public class Positionner2 {

	private int row;
	private int col;

	private int maxRow;
	private int maxCol;

	private final Map<Element, Cell> positions = new LinkedHashMap<Element, Cell>();

	private Cell last;

	public void add(Terminated<Element> element) {
		addWithoutMove(element.getElement());
		final Terminator terminator = element.getTerminator();
		if (terminator == Terminator.NEWCOL) {
			moveNextColumn();
		} else {
			moveNextRow();
		}
	}

	private void moveNextColumn() {
		col++;
	}

	private void moveNextRow() {
		row++;
		col = 0;
	}

	private void addWithoutMove(Element elmt) {
		last = new Cell(row, col);
		positions.put(elmt, last);
		updateMax();
	}

	public void mergeLeft(Terminator terminator) {
		updateMax();
		if (terminator == Terminator.NEWCOL) {
			col++;
		} else {
			row++;
			col = 0;
		}
		last.mergeLeft();
	}

	private void updateMax() {
		if (row > maxRow) {
			maxRow = row;
		}
		if (col > maxCol) {
			maxCol = col;
		}
	}

	public Map<Element, Cell> getAll() {
		return Collections.unmodifiableMap(positions);
	}

	public final int getNbRows() {
		return maxRow + 1;
	}

	public final int getNbCols() {
		return maxCol + 1;
	}

}
