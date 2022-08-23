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

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColors;

class ExtremityCircle extends Extremity {

	private static final double radius = 6;
	private final Point2D dest;
	private final boolean fill;
	private final HColor backgroundColor;

	@Override
	public Point2D somePoint() {
		return dest;
	}

	public static UDrawable create(Point2D center, boolean fill, double angle, HColor backgroundColor) {
		return new ExtremityCircle(center.getX(), center.getY(), fill, angle, backgroundColor);
	}

	private ExtremityCircle(double x, double y, boolean fill, double angle, HColor backgroundColor) {
		this.dest = new Point2D.Double(x - radius * Math.cos(angle + Math.PI / 2), y - radius
				* Math.sin(angle + Math.PI / 2));
		this.backgroundColor = backgroundColor;
		this.fill = fill;
		// contact = new Point2D.Double(p1.getX() - xContact * Math.cos(angle + Math.PI / 2), p1.getY() - xContact
		// * Math.sin(angle + Math.PI / 2));
	}

	public void drawU(UGraphic ug) {

		ug = ug.apply(new UStroke(1.5));
		if (fill) {
			ug = ug.apply(HColors.changeBack(ug));
		} else {
			ug = ug.apply(backgroundColor.bg());
		}

		ug = ug.apply(new UTranslate(dest.getX() - radius, dest.getY() - radius));
		ug.draw(new UEllipse(radius * 2, radius * 2));
	}

}
