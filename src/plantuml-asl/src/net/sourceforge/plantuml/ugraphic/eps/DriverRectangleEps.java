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
package net.sourceforge.plantuml.ugraphic.eps;

import java.awt.geom.Rectangle2D;

import net.sourceforge.plantuml.eps.EpsGraphics;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorGradient;

public class DriverRectangleEps implements UDriver<URectangle, EpsGraphics> {

	private final ClipContainer clipContainer;

	public DriverRectangleEps(ClipContainer clipContainer) {
		this.clipContainer = clipContainer;
	}

	public void draw(URectangle rect, double x, double y, ColorMapper mapper, UParam param, EpsGraphics eps) {
		double width = rect.getWidth();
		double height = rect.getHeight();

		final UClip clip = clipContainer.getClip();
		if (clip != null) {
			final Rectangle2D.Double orig = new Rectangle2D.Double(x, y, width, height);
			final Rectangle2D.Double r = clip.getClippedRectangle(orig);
			if (r.height < 0) {
				return;
			}
			x = r.x;
			y = r.y;
			width = r.width;
			height = r.height;
		}

		final double rx = rect.getRx();
		final double ry = rect.getRy();

		// Shadow
		if (rect.getDeltaShadow() != 0) {
			eps.epsRectangleShadow(x, y, width, height, rx / 2, ry / 2, rect.getDeltaShadow());
		}

		final HColor back = param.getBackcolor();
		if (back instanceof HColorGradient) {
			eps.setStrokeColor(mapper.toColor(param.getColor()));
			eps.epsRectangle(x, y, width, height, rx / 2, ry / 2, (HColorGradient) back, mapper);
		} else {
			eps.setStrokeColor(mapper.toColor(param.getColor()));
			eps.setFillColor(mapper.toColor(param.getBackcolor()));
			eps.setStrokeWidth(param.getStroke().getThickness(), param.getStroke().getDashVisible(), param
					.getStroke().getDashSpace());
			eps.epsRectangle(x, y, width, height, rx / 2, ry / 2);
		}
	}
}
