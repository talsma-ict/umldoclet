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
package net.sourceforge.plantuml.graph;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.geom.Point2DInt;
import net.sourceforge.plantuml.geom.PolylineBreakeable;
import net.sourceforge.plantuml.geom.SpiderWeb;

public class Galaxy4 {

	final private Board board;

	final private Map<ALink, PolylineBreakeable> lines = new LinkedHashMap<ALink, PolylineBreakeable>();
	final private SpiderWeb spiderWeb;

	public Galaxy4(Board board, int widthCell, int heightCell) {
		this.spiderWeb = new SpiderWeb(widthCell, heightCell);
		this.board = board;
	}

	public Point2DInt getMainPoint(int row, int col) {
		return spiderWeb.getMainPoint(row, col);
	}

	public PolylineBreakeable getPolyline(ALink link) {
		return lines.get(link);

	}

	public void addLink(ALink link) {
		final int rowStart = link.getNode1().getRow();
		final int rowEnd = link.getNode2().getRow();
		final int colStart = board.getCol(link.getNode1());
		final int colEnd = board.getCol(link.getNode2());

		final PolylineBreakeable polyline = spiderWeb.addPolyline(rowStart, colStart, rowEnd, colEnd);

		Log.info("link=" + link + " polyline=" + polyline);

		if (polyline == null) {
			Log.info("PENDING " + link + " " + polyline);
		} else {
			lines.put(link, polyline);
		}

	}

	public final Board getBoard() {
		return board;
	}

	public final Map<ALink, PolylineBreakeable> getLines() {
		return Collections.unmodifiableMap(lines);
	}

}
