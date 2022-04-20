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
package net.sourceforge.plantuml.graphic;

import java.awt.geom.Point2D;

class CoordinateChange {

	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;

	private final double vect_u_x;
	private final double vect_u_y;
	private final double vect_v_x;
	private final double vect_v_y;
	private final double len;

	public static CoordinateChange create(Point2D p1, Point2D p2) {
		return new CoordinateChange(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public CoordinateChange(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.len = Point2D.distance(x1, y1, x2, y2);
		if (this.len == 0) {
			throw new IllegalArgumentException();
		}

		this.vect_u_x = (x2 - x1) / len;
		this.vect_u_y = (y2 - y1) / len;

		this.vect_v_x = -this.vect_u_y;
		this.vect_v_y = this.vect_u_x;

	}

	public Point2D getTrueCoordinate(double a, double b) {
		final double x = a * vect_u_x + b * vect_v_x;
		final double y = a * vect_u_y + b * vect_v_y;
		return new Point2D.Double(x1 + x, y1 + y);
	}

	public final double getLength() {
		return len;
	}

}
