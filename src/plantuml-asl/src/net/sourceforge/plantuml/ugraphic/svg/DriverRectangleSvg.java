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
package net.sourceforge.plantuml.ugraphic.svg;

import java.awt.geom.Rectangle2D;

import net.sourceforge.plantuml.svg.SvgGraphics;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorGradient;

public class DriverRectangleSvg implements UDriver<URectangle, SvgGraphics> {

	private final ClipContainer clipContainer;

	public DriverRectangleSvg(ClipContainer clipContainer) {
		this.clipContainer = clipContainer;
	}

	public void draw(URectangle rect, double x, double y, ColorMapper mapper, UParam param, SvgGraphics svg) {
		final double rx = rect.getRx();
		final double ry = rect.getRy();
		double width = rect.getWidth();
		double height = rect.getHeight();

		applyFillColor(svg, mapper, param);
		applyStrokeColor(svg, mapper, param);

		svg.setStrokeWidth(param.getStroke().getThickness(), param.getStroke().getDasharraySvg());

		final UClip clip = clipContainer.getClip();
		if (clip != null) {
			final Rectangle2D.Double r = clip.getClippedRectangle(new Rectangle2D.Double(x, y, width, height));
			x = r.x;
			y = r.y;
			width = r.width;
			height = r.height;
			if (height <= 0) {
				return;
			}
		}
		svg.svgRectangle(x, y, width, height, rx / 2, ry / 2, rect.getDeltaShadow(), rect.getComment(),
				rect.getCodeLine());
	}

	public static void applyFillColor(SvgGraphics svg, ColorMapper mapper, UParam param) {
		final HColor background = param.getBackcolor();
		if (background instanceof HColorGradient) {
			final HColorGradient gr = (HColorGradient) background;
			final String id = svg.createSvgGradient(gr.getColor1().toRGB(mapper), gr.getColor2().toRGB(mapper),
					gr.getPolicy());
			svg.setFillColor("url(#" + id + ")");
		} else {
			svg.setFillColor(background.toSvg(mapper));
		}
	}

	public static void applyStrokeColor(SvgGraphics svg, ColorMapper mapper, UParam param) {
		final HColor color = param.getColor();
		if (color instanceof HColorGradient) {
			final HColorGradient gr = (HColorGradient) color;
			final String id = svg.createSvgGradient(gr.getColor1().toRGB(mapper), gr.getColor2().toRGB(mapper),
					gr.getPolicy());
			svg.setStrokeColor("url(#" + id + ")");
		} else {
			svg.setStrokeColor(color.toSvg(mapper));
		}

	}
}
