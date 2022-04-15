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
package net.sourceforge.plantuml.project;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.ugraphic.UPolygon;

public class Arrows {

	final static private double delta2 = 4;

	private static UPolygon asToUp() {
		final UPolygon polygon = new UPolygon("asToUp");
		polygon.addPoint(-delta2, 0);
		polygon.addPoint(0, 0);
		polygon.addPoint(delta2, 0);
		polygon.addPoint(0, -4);
		return polygon;
	}

	private static UPolygon asToDown() {
		final UPolygon polygon = new UPolygon("asToDown");
		polygon.addPoint(-delta2, 0);
		polygon.addPoint(0, 0);
		polygon.addPoint(delta2, 0);
		polygon.addPoint(0, 4);
		return polygon;
	}

	private static UPolygon asToRight() {
		final UPolygon polygon = new UPolygon("asToRight");
		polygon.addPoint(0, -delta2);
		polygon.addPoint(0, 0);
		polygon.addPoint(0, delta2);
		polygon.addPoint(4, 0);
		return polygon.translate(-4, 0);
	}

	private static UPolygon asToLeft() {
		final UPolygon polygon = new UPolygon("asToLeft");
		polygon.addPoint(0, -delta2);
		polygon.addPoint(0, 0);
		polygon.addPoint(0, delta2);
		polygon.addPoint(-4, 0);
		return polygon.translate(4, 0);
	}

	public static UPolygon asTo(Direction direction) {
		if (direction == Direction.UP) {
			return asToUp();
		}
		if (direction == Direction.DOWN) {
			return asToDown();
		}
		if (direction == Direction.LEFT) {
			return asToLeft();
		}
		if (direction == Direction.RIGHT) {
			return asToRight();
		}
		throw new IllegalArgumentException();
	}

}
