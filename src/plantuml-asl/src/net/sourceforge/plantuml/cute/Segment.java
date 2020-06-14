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
package net.sourceforge.plantuml.cute;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Segment {

	private final Point2D a;
	private final Point2D b;
	private final double length;

	public Segment(Point2D a, Point2D b) {
		this.a = a;
		this.b = b;
		this.length = a.distance(b);
		if (length < 0.0001) {
			throw new IllegalArgumentException();
		}
	}

	public Point2D getFromAtoB(double dist) {
		final double dx = b.getX() - a.getX();
		final double dy = b.getY() - a.getY();
		final double coef = dist / length;
		final double x = dx * coef;
		final double y = dy * coef;
		return new Point2D.Double(a.getX() + x, a.getY() + y);
	}

	public Point2D getA() {
		return a;
	}

	public Point2D getB() {
		return b;
	}

	public Point2D getMiddle() {
		return new Point2D.Double((a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2);
	}

	private Point2D orthoDirection() {
		final double dx = b.getX() - a.getX();
		final double dy = b.getY() - a.getY();
		return new Point2D.Double(-dy / length, dx / length);
	}

	public Point2D getOrthoPoint(double value) {
		final Point2D ortho = orthoDirection();
		final double dx = -ortho.getX() * value;
		final double dy = -ortho.getY() * value;
		return new Point2D.Double((a.getX() + b.getX()) / 2 + dx, (a.getY() + b.getY()) / 2 + dy);
	}


	private boolean isLeft(Point2D point) {
		return ((b.getX() - a.getX()) * (point.getY() - a.getY()) - (b.getY() - a.getY()) * (point.getX() - a.getX())) > 0;
	}

	public double getLength() {
		return length;
	}

	public void debugMe(UGraphic ug) {
		final double dx = b.getX() - a.getX();
		final double dy = b.getY() - a.getY();
		ug = ug.apply(new UTranslate(a));
		ug.draw(new ULine(dx, dy));
		
	}

}
