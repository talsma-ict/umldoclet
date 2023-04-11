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

import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;
import net.sourceforge.plantuml.klimt.shape.UDrawable;
import net.sourceforge.plantuml.klimt.shape.UEllipse;

class ExtremityCircle extends Extremity {

	private static final double radius = 6;
	private final XPoint2D dest;
	private final boolean fill;
	private final HColor backgroundColor;

	@Override
	public XPoint2D somePoint() {
		return dest;
	}

	public static UDrawable create(XPoint2D center, boolean fill, double angle, HColor backgroundColor) {
		return new ExtremityCircle(center.getX(), center.getY(), fill, angle, backgroundColor);
	}

	private ExtremityCircle(double x, double y, boolean fill, double angle, HColor backgroundColor) {
		this.dest = new XPoint2D(x - radius * Math.cos(angle + Math.PI / 2),
				y - radius * Math.sin(angle + Math.PI / 2));
		this.backgroundColor = backgroundColor;
		this.fill = fill;
		// contact = new XPoint2D(p1.getX() - xContact * Math.cos(angle + Math.PI / 2),
		// p1.getY() - xContact
		// * Math.sin(angle + Math.PI / 2));
	}

	public void drawU(UGraphic ug) {

		ug = ug.apply(UStroke.withThickness(1.5));
		if (fill) {
			ug = ug.apply(HColors.changeBack(ug));
		} else {
			ug = ug.apply(backgroundColor.bg());
		}

		ug = ug.apply(new UTranslate(dest.getX() - radius, dest.getY() - radius));
		ug.draw(UEllipse.build(radius * 2, radius * 2));
	}

}
