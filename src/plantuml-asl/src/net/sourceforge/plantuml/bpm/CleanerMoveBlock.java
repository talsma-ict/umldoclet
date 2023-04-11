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
package net.sourceforge.plantuml.bpm;

public class CleanerMoveBlock implements GridCleaner {

	public boolean clean(Grid grid) {
		// System.err.println("CleanerMoveBlock");
		for (Line line : grid.lines().toList()) {
			tryGrid(grid, line);
		}
		return false;
	}

	private void tryGrid(Grid grid, Line line) {
		// System.err.println("TRYING LINE " + line);
		for (Col col1 : grid.cols().toList()) {
			final Placeable cell1 = grid.getCell(line, col1).getData();
			if (cell1 instanceof ConnectorPuzzleEmpty == false) {
				continue;
			}
			final ConnectorPuzzleEmpty puzzle1 = (ConnectorPuzzleEmpty) cell1;
			if (puzzle1.checkDirections("NS") == false) {
				continue;
			}
			final Navigator<Col> it2 = grid.cols().navigator(col1);
			int cpt = 0;
			while (true) {
				final Col col2 = it2.next();
				cpt++;
				if (col2 == null) {
					break;
				}
				if (col1 == col2) {
					continue;
				}
				final Placeable cell2 = grid.getCell(line, col2).getData();
				if (cell2 == null) {
					continue;
				}
				if (cell2 instanceof ConnectorPuzzleEmpty == false) {
					break;
				}
				final ConnectorPuzzleEmpty puzzle2 = (ConnectorPuzzleEmpty) cell2;
				if (puzzle2.checkDirections("NS") == false) {
					continue;
				}
				if (cpt > 1) {
					tryBridge(line, col1, col2);
				}
				break;
			}
		}

	}

	private void tryBridge(Line line, Col col1, final Col col2) {
		// System.err.println("LINE=" + line + " " + col1 + " " + col2 + " ");
	}
}
