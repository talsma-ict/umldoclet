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
package net.sourceforge.plantuml.svek;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class CircleAndArrow implements UDrawable {

	private final AffineTransform at;
	private final AffineTransform at2;
	private int radius;
	private final Point2D center;
	private final Point2D p1;
	private final Point2D p2;
	private Point2D p3;
	private Point2D p4;

	public CircleAndArrow(Point2D p1, Point2D p2) {
		this.center = new Point2D.Double((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
		at = AffineTransform.getTranslateInstance(-center.getX(), -center.getY());
		at2 = AffineTransform.getTranslateInstance(center.getX(), center.getY());
		radius = (int) (p1.distance(p2) / 2);
		if (radius % 2 == 0) {
			radius--;
		}
		this.p1 = putOnCircle(p1);
		this.p2 = putOnCircle(p2);

		this.p3 = at.transform(this.p1, null);
		this.p3 = new Point2D.Double(p3.getY(), -p3.getX());
		this.p3 = at2.transform(p3, null);

		this.p4 = at.transform(this.p2, null);
		this.p4 = new Point2D.Double(p4.getY(), -p4.getX());
		this.p4 = at2.transform(p4, null);
	}

	private Point2D putOnCircle(Point2D p) {
		p = at.transform(p, null);
		final double coef = p.distance(new Point2D.Double()) / radius;
		p = new Point2D.Double(p.getX() / coef, p.getY() / coef);
		return at2.transform(p, null);
	}

	public void drawU(UGraphic ug) {
		final UShape circle = new UEllipse(radius * 2, radius * 2);
		ug.apply(new UTranslate(center.getX() - radius, center.getY() - radius)).draw(circle);
		// drawLine(ug, x, y, p1, p2);
		// drawLine(ug, x, y, p3, p4);
	}

	static private void drawLine(UGraphic ug, double x, double y, Point2D p1, Point2D p2) {
		final double dx = p2.getX() - p1.getX();
		final double dy = p2.getY() - p1.getY();
		ug.apply(new UTranslate(x + p1.getX(), y + p1.getY())).draw(new ULine(dx, dy));

	}

}
