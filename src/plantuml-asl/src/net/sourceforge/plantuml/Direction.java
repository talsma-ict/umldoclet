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
package net.sourceforge.plantuml;

import java.awt.geom.Point2D;

public enum Direction {
	RIGHT, LEFT, DOWN, UP;

	public Direction getInv() {
		if (this == RIGHT) {
			return LEFT;
		}
		if (this == LEFT) {
			return RIGHT;
		}
		if (this == DOWN) {
			return UP;
		}
		if (this == UP) {
			return DOWN;
		}
		throw new IllegalStateException();
	}
	
	public String getShortCode() {
		return name().substring(0, 1);
	}

	public static Direction fromChar(char c) {
		if (c == '<') {
			return Direction.LEFT;
		}
		if (c == '>') {
			return Direction.RIGHT;
		}
		if (c == '^') {
			return Direction.UP;
		}
		return Direction.DOWN;
	}

	public Direction clockwise() {
		if (this == RIGHT) {
			return DOWN;
		}
		if (this == LEFT) {
			return UP;
		}
		if (this == DOWN) {
			return LEFT;
		}
		if (this == UP) {
			return RIGHT;
		}
		throw new IllegalStateException();
	}

	public static Direction leftOrRight(Point2D p1, Point2D p2) {
		if (p1.getX() < p2.getX()) {
			return Direction.LEFT;
		}
		if (p1.getX() > p2.getX()) {
			return Direction.RIGHT;
		}
		throw new IllegalArgumentException();
	}

	public static Direction fromVector(Point2D p1, Point2D p2) {
		final double x1 = p1.getX();
		final double y1 = p1.getY();
		final double x2 = p2.getX();
		final double y2 = p2.getY();
		if (x1 == x2 && y1 == y2) {
			return null;
		}
		if (x1 == x2) {
			if (y2 > y1) {
				return Direction.DOWN;
			}
			return Direction.UP;
		}
		if (y1 == y2) {
			if (x2 > x1) {
				return Direction.RIGHT;
			}
			return Direction.LEFT;
		}
		throw new IllegalArgumentException("Not a H or V line!");

	}
}
