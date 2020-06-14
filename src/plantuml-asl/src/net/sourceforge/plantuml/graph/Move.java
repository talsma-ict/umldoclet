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
package net.sourceforge.plantuml.graph;

public class Move {

	private final int row;
	private final int col;
	private final int delta;

	@Override
	public String toString() {
		return row + "." + col + "->" + row + "." + (col + delta);
	}

	public Move(int row, int col, int delta) {
		if (delta != 1 && delta != -1) {
			throw new IllegalArgumentException();
		}
		this.row = row;
		this.col = col;
		this.delta = delta;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getNewCol() {
		return col + delta;
	}

	public int getDelta() {
		return delta;
	}

	public Move getBackMove() {
		return new Move(row, col + delta, -delta);
	}

}
