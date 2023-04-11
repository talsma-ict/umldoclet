/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.svek.image;

import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public class Circle {

	private XPoint2D center;

	private double radius;

	public Circle() {
		this(new XPoint2D(0, 0));
	}

	public Circle(XPoint2D center) {
		this.center = center;
		this.radius = 0;
	}

	public Circle(XPoint2D p1, XPoint2D p2) {
		center = new XPoint2D((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
		radius = p1.distance(center);
	}

	public static Circle getCircle(XPoint2D p1, XPoint2D p2, XPoint2D p3) {
		if (p3.getY() != p2.getY()) {
			return new Circle(p1, p2, p3);
		}
		return new Circle(p2, p1, p3);
	}

	private Circle(XPoint2D p1, XPoint2D p2, XPoint2D p3) {
		final double num1 = p3.getX() * p3.getX() * (p1.getY() - p2.getY())
				+ (p1.getX() * p1.getX() + (p1.getY() - p2.getY()) * (p1.getY() - p3.getY())) * (p2.getY() - p3.getY())
				+ p2.getX() * p2.getX() * (-p1.getY() + p3.getY());
		final double den1 = 2 * (p3.getX() * (p1.getY() - p2.getY()) + p1.getX() * (p2.getY() - p3.getY())
				+ p2.getX() * (-p1.getY() + p3.getY()));
		final double x = num1 / den1;
		final double den2 = p3.getY() - p2.getY();
		final double y = (p2.getY() + p3.getY()) / 2
				- (p3.getX() - p2.getX()) / den2 * (x - (p2.getX() + p3.getX()) / 2);
		center = new XPoint2D(x, y);
		radius = center.distance(p1);
	}

	public XPoint2D getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}

	public boolean isOutside(XPoint2D point) {
		final double d = center.distance(point);
		if (d > radius) {
			return true;
		}
		return false;
	}

}
