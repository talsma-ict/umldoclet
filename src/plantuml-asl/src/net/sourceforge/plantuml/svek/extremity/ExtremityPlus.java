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
package net.sourceforge.plantuml.svek.extremity;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.UEllipse;
import net.sourceforge.plantuml.klimt.shape.ULine;

class ExtremityPlus extends Extremity {

	private final UEllipse circle;
	private final double px;
	private final double py;
	private static final double radius = 8;
	private final double angle;
	private final HColor backgroundColor;

	private ExtremityPlus(double x, double y, double angle, HColor backgroundColor) {
		this.angle = angle;
		this.circle = UEllipse.build(2 * radius, 2 * radius);
		this.px = x;
		this.py = y;
		this.backgroundColor = backgroundColor;
	}

	@Override
	public XPoint2D somePoint() {
		return new XPoint2D(px, py);
	}

	public static UDrawable create(XPoint2D p1, double angle, HColor backgroundColor) {
		final double x = p1.getX() - radius + radius * Math.sin(angle);
		final double y = p1.getY() - radius - radius * Math.cos(angle);
		return new ExtremityPlus(x, y, angle, backgroundColor);
	}

	public void drawU(UGraphic ug) {
		ug.apply(backgroundColor.bg()).apply(new UTranslate(px + 0, py + 0)).draw(circle);
		drawLine(ug, 0, 0, getPointOnCircle(angle - Math.PI / 2), getPointOnCircle(angle + Math.PI / 2));
		drawLine(ug, 0, 0, getPointOnCircle(angle), getPointOnCircle(angle + Math.PI));
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
