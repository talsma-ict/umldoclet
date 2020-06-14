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
package net.sourceforge.plantuml.bpm;

public class Coord {

	private final Line line;
	private final Col col;

	public Coord(Line line, Col row) {
		if (line == null || row == null) {
			throw new IllegalArgumentException();
		}
		this.line = line;
		this.col = row;
	}

	public final Col getCol() {
		return col;
	}

	@Override
	public String toString() {
		return "line=" + line + " col=" + col;
	}

	public final Line getLine() {
		return line;
	}

	@Override
	public int hashCode() {
		return line.hashCode() + col.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final Coord other = (Coord) obj;
		return this.line == other.line && this.col == other.col;
	}

}
