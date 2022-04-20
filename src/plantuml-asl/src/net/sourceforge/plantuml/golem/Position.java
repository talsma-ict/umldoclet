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
package net.sourceforge.plantuml.golem;

public class Position {

	private final int xmin;
	private final int ymin;
	private final int xmax;
	private final int ymax;

	public Position(int xmin, int ymin, int xmax, int ymax) {
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}

	@Override
	public boolean equals(Object o) {
		final Position other = (Position) o;
		return this.xmin == other.xmin && this.xmax == other.xmax && this.ymin == other.ymin && this.ymax == other.ymax;
	}

	@Override
	public int hashCode() {
		return xmin + ymin << 8 + xmax << 16 + ymax << 24;
	}

	@Override
	public String toString() {
		return "(" + xmin + "," + ymin + ")-(" + xmax + "," + ymax + ")";
	}

	public Position move(TileGeometry position, int sizeMove) {
		if (position == null || position == TileGeometry.CENTER) {
			throw new IllegalArgumentException();
		}
		switch (position) {
		case NORTH:
			return new Position(xmin, ymin - sizeMove, xmax, ymax - sizeMove);
		case SOUTH:
			return new Position(xmin, ymin + sizeMove, xmax, ymax + sizeMove);
		case WEST:
			return new Position(xmin - sizeMove, ymin, xmax - sizeMove, ymax);
		case EAST:
			return new Position(xmin + sizeMove, ymin, xmax + sizeMove, ymax);
		default:
			throw new IllegalStateException();
		}
	}

	public int getXmin() {
		return xmin;
	}

	public int getXmax() {
		return xmax;
	}

	public int getYmin() {
		return ymin;
	}

	public int getYmax() {
		return ymax;
	}

	public int getCenterX() {
		if ((xmin + xmax + 1) % 2 != 0) {
			throw new IllegalStateException();
		}
		return (xmin + xmax + 1) / 2;
	}

	public int getCenterY() {
		if ((ymin + ymax + 1) % 2 != 0) {
			throw new IllegalStateException();
		}
		return (ymin + ymax + 1) / 2;
	}
}
