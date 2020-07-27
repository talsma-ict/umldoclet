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

public class SimpleCostComputer implements CostComputer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.plantuml.graph.CostComputer#getCost(net.sourceforge.plantuml.graph.Board)
	 */
	public double getCost(Board board) {
		double result = 0;
		for (ALink link : board.getLinks()) {
			final ANode n1 = link.getNode1();
			final ANode n2 = link.getNode2();
			final int x1 = board.getCol(n1);
			final int y1 = n1.getRow();
			final int x2 = board.getCol(n2);
			final int y2 = n2.getRow();
			result += Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

		}
		return result;
	}

}
