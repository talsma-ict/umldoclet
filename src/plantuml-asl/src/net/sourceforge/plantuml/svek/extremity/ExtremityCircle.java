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
package net.sourceforge.plantuml.svek.extremity;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class ExtremityCircle extends Extremity {

	private final Point2D dest;
	private static final double radius = 6;

	@Override
	public Point2D somePoint() {
		return dest;
	}

	// public static UDrawable createContact(Point2D p1, double angle) {
	// final double x = p1.getX() - radius + radius * Math.sin(angle);
	// final double y = p1.getY() - radius - radius * Math.cos(angle);
	// return new ExtremityCircle(x, y);
	// }

	public static UDrawable create(Point2D center) {
		return new ExtremityCircle(center.getX(), center.getY());
	}

	private ExtremityCircle(double x, double y) {
		this.dest = new Point2D.Double(x, y);
	}

	public void drawU(UGraphic ug) {
		ug.apply(new UStroke(1.5)).apply(new UChangeBackColor(HtmlColorUtils.WHITE))
				.apply(new UTranslate(dest.getX() - radius, dest.getY() - radius))
				.draw(new UEllipse(radius * 2, radius * 2));
	}

}
