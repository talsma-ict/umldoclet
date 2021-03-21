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
package net.sourceforge.plantuml.svek.extremity;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

class ExtremityCircleConnect extends Extremity {

	private final double px;
	private final double py;
	private final Point2D dest;
	private final double radius = 6;
	private final double radius2 = 10;
	private final double ortho;
	private final HColor backgroundColor;

	@Override
	public Point2D somePoint() {
		return dest;
	}

	public ExtremityCircleConnect(Point2D p1, double ortho, HColor backgroundColor) {
		this.px = p1.getX() - radius;
		this.py = p1.getY() - radius;
		this.dest = new Point2D.Double(p1.getX(), p1.getY());
		this.ortho = ortho;
		this.backgroundColor = backgroundColor;
	}

	public void drawU(UGraphic ug) {
		ug = ug.apply(new UStroke(1.5)).apply(backgroundColor.bg());
		ug.apply(new UTranslate(dest.getX() - radius, dest.getY() - radius)).draw(new UEllipse(radius * 2, radius * 2));
		
		final double deg = -ortho * 180 / Math.PI + 90 - 45;
		final UEllipse arc1 = new UEllipse(2 * radius2, 2 * radius2, deg, 90);
		ug.apply(new UTranslate(dest.getX() - radius2, dest.getY() - radius2)).draw(arc1);
	}

	// private Point2D getPointOnCircle(double angle) {
	// final double x = px + radius + radius2 * Math.cos(angle);
	// final double y = py + radius + radius2 * Math.sin(angle);
	// return new Point2D.Double(x, y);
	// }
	//
	// static private void drawLine(UGraphic ug, double x, double y, Point2D p1, Point2D p2) {
	// final double dx = p2.getX() - p1.getX();
	// final double dy = p2.getY() - p1.getY();
	// ug.draw(x + p1.getX(), y + p1.getY(), new ULine(dx, dy));
	//
	// }

}
