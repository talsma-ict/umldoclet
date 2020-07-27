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
package net.sourceforge.plantuml.bpm;

import net.sourceforge.plantuml.bpm.ConnectorPuzzle.Where;

public class CleanerInterleavingLines implements GridCleaner {

	public boolean clean(Grid grid) {
		// System.err.println("running CleanerInterleavingLines");
		boolean result = false;
		Line previous = null;
		for (Line line : grid.lines().toList()) {
			if (previous != null) {
				if (mergeable(grid, previous, line)) {
					// System.err.println("MERGEABLE! " + previous + " " + line);
					mergeLines(grid, previous, line);
					return true;
				}
			}
			previous = line;
		}
		return result;
	}

	private void mergeLines(Grid grid, Line line1, Line line2) {
		for (Col col : grid.cols().toList()) {
			final Cell cell1 = grid.getCell(line1, col);
			final Cell cell2 = grid.getCell(line2, col);
			cell1.setData(merge(cell1.getData(), cell2.getData()));
			cell2.setData(null);
		}
		grid.removeLine(line2);

	}

	private boolean mergeable(Grid grid, Line line1, Line line2) {
		// int c = 0;
		for (Col col : grid.cols().toList()) {
			// System.err.println("c=" + c);
			// c++;
			final Placeable cell1 = grid.getCell(line1, col).getData();
			final Placeable cell2 = grid.getCell(line2, col).getData();
			// System.err.println("cells=" + cell1 + " " + cell2 + " " + mergeable(cell1, cell2));
			if (mergeable(cell1, cell2) == false) {
				return false;
			}
		}
		return true;
	}

	private Placeable merge(Placeable data1, Placeable data2) {
		if (data1 == null) {
			return data2;
		}
		if (data2 == null) {
			return data1;
		}
		assert data1 != null && data2 != null;
		if (data1 instanceof BpmElement) {
			return data1;
		}
		if (data2 instanceof BpmElement) {
			final ConnectorPuzzleEmpty puz1 = (ConnectorPuzzleEmpty) data1;
			if (puz1.checkDirections("SW")) {
				((BpmElement) data2).remove(Where.NORTH);
				((BpmElement) data2).append(Where.WEST);
			}
			return data2;
		}
		assert data1 instanceof ConnectorPuzzleEmpty && data2 instanceof ConnectorPuzzleEmpty;
		final ConnectorPuzzleEmpty puz1 = (ConnectorPuzzleEmpty) data1;
		final ConnectorPuzzleEmpty puz2 = (ConnectorPuzzleEmpty) data2;
		return puz2;
	}

	private boolean mergeable(Placeable data1, Placeable data2) {
		if (data1 == null || data2 == null) {
			return true;
		}
		assert data1 != null && data2 != null;
		if (data1 instanceof ConnectorPuzzleEmpty && data2 instanceof ConnectorPuzzleEmpty) {
			return mergeableCC((ConnectorPuzzleEmpty) data1, (ConnectorPuzzleEmpty) data2);
		}
		if (data1 instanceof ConnectorPuzzleEmpty && data2 instanceof BpmElement) {
			final boolean result = mergeablePuzzleSingle((ConnectorPuzzleEmpty) data1, (BpmElement) data2);
			// System.err.println("OTHER2=" + data2 + " " + data1 + " " + result);
			return result;
		}
		if (data2 instanceof ConnectorPuzzleEmpty && data1 instanceof BpmElement) {
			final boolean result = mergeablePuzzleSingle((BpmElement) data1, (ConnectorPuzzleEmpty) data2);
			// System.err.println("OTHER1=" + data1 + " " + data2 + " " + result);
			return result;
		}
		return false;
	}

	private boolean mergeablePuzzleSingle(ConnectorPuzzleEmpty data1, BpmElement data2) {
		if (data1.checkDirections("NS")) {
			return true;
		}
		if (data1.checkDirections("SW")) {
			return true;
		}
		return false;
	}

	private boolean mergeablePuzzleSingle(BpmElement data1, ConnectorPuzzleEmpty data2) {
		if (data2.checkDirections("NS")) {
			return true;
		}
		return false;
	}

	private boolean mergeableCC(ConnectorPuzzleEmpty puz1, ConnectorPuzzleEmpty puz2) {
		if (puz1.checkDirections("NS") && puz2.checkDirections("NS")) {
			return true;
		}
		if (puz1.checkDirections("NS") && puz2.checkDirections("NE")) {
			return true;
		}
		if (puz1.checkDirections("NS") && puz2.checkDirections("NW")) {
			return true;
		}
		return false;
	}

}
