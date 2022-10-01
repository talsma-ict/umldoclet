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
package net.sourceforge.plantuml.svek.extremity;

import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

class ExtremityCircleCross extends Extremity {

	private final double px;
	private final double py;
	private final XPoint2D dest;
	private final double radius = 7;
	private final HColor backgroundColor;

	@Override
	public XPoint2D somePoint() {
		return dest;
	}

	public ExtremityCircleCross(XPoint2D p1, HColor backgroundColor) {
		this.px = p1.getX() - radius;
		this.py = p1.getY() - radius;
		this.dest = new XPoint2D(p1.getX(), p1.getY());
		this.backgroundColor = backgroundColor;
	}

	public void drawU(UGraphic ug) {
		ug = ug.apply(backgroundColor.bg());
		ug.apply(new UStroke(1.5)).apply(new UTranslate(dest.getX() - radius, dest.getY() - radius)).draw(new UEllipse(radius * 2, radius * 2));
		drawLine(ug, 0, 0, getPointOnCircle(Math.PI / 4), getPointOnCircle(Math.PI + Math.PI / 4));
		drawLine(ug, 0, 0, getPointOnCircle(-Math.PI / 4), getPointOnCircle(Math.PI - Math.PI / 4));
	}

	private XPoint2D getPointOnCircle(double angle) {
		final double x = px + radius + radius * Math.cos(angle);
		final double y = py + radius + radius * Math.sin(angle);
		return new XPoint2D(x, y);
	}

	static private void drawLine(UGraphic ug, double x, double y, XPoint2D p1, XPoint2D p2) {
		final double dx = p2.getX() - p1.getX();
		final double dy = p2.getY() - p1.getY();
		ug.apply(new UTranslate(x + p1.getX(), y + p1.getY())).draw(new ULine(dx, dy));

	}

}
