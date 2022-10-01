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
package net.sourceforge.plantuml.ugraphic.eps;

import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.eps.EpsGraphics;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorGradient;

public class DriverPolygonEps implements UDriver<UPolygon, EpsGraphics> {

	private final ClipContainer clipContainer;

	public DriverPolygonEps(ClipContainer clipContainer) {
		this.clipContainer = clipContainer;
	}

	public void draw(UPolygon shape, double x, double y, ColorMapper mapper, UParam param, EpsGraphics eps) {
		final double points[] = new double[shape.getPoints().size() * 2];
		int i = 0;

		for (XPoint2D pt : shape.getPoints()) {
			points[i++] = pt.getX() + x;
			points[i++] = pt.getY() + y;
		}

		final UClip clip = clipContainer.getClip();
		if (clip != null) {
			for (int j = 0; j < points.length; j += 2) {
				if (clip.isInside(points[j], points[j + 1]) == false) {
					return;
				}
			}
		}

		if (shape.getDeltaShadow() != 0) {
			eps.epsPolygonShadow(shape.getDeltaShadow(), points);
		}

		final HColor back = param.getBackcolor();
		if (back instanceof HColorGradient) {
			eps.setStrokeColor(param.getColor().toColor(mapper));
			eps.epsPolygon((HColorGradient) back, mapper, points);
		} else {

			eps.setFillColor(back.toColor(mapper));
			eps.setStrokeColor(param.getColor().toColor(mapper));
			eps.epsPolygon(points);
		}
	}
}
