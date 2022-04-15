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
package net.sourceforge.plantuml.awt.geom;

import net.sourceforge.plantuml.awt.Shape;

public class Line2D implements Shape {

	public double x1;
	public double y1;
	public double x2;
	public double y2;

	public Line2D() {
		this(0, 0, 0, 0);
	}

	public Line2D(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

	}

	public Line2D(Point2D p1, Point2D p2) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public static class Double extends Line2D {

		public Double(double x1, double y1, double x2, double y2) {
			super(x1, y1, x2, y2);
		}

		public Double(Point2D p1, Point2D p2) {
			super(p1, p2);
		}

	}

	public final double getX1() {
		return x1;
	}

	public final double getY1() {
		return y1;
	}

	public final double getX2() {
		return x2;
	}

	public final double getY2() {
		return y2;
	}

	public Point2D getP1() {
		return new Point2D(x1, y1);
	}

	public Point2D getP2() {
		return new Point2D(x2, y2);
	}

}
