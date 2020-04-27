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
package net.sourceforge.plantuml.ugraphic.svg;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.svg.SvgGraphics;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorGradient;

public class DriverPolygonSvg implements UDriver<SvgGraphics> {

	private final ClipContainer clipContainer;

	public DriverPolygonSvg(ClipContainer clipContainer) {
		this.clipContainer = clipContainer;
	}

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, SvgGraphics svg) {
		final UPolygon shape = (UPolygon) ushape;

		final double points[] = shape.getPointArray(x, y);
		assert points.length % 2 == 0;
		final UClip clip = clipContainer.getClip();
		if (clip != null) {
			for (int j = 0; j < points.length; j += 2) {
				if (clip.isInside(points[j], points[j + 1]) == false) {
					return;
				}
			}
		}

		final String color = StringUtils.getAsSvg(mapper, param.getColor());
		final HColor back = param.getBackcolor();
		if (back instanceof HColorGradient) {
			final HColorGradient gr = (HColorGradient) back;
			final String id = svg.createSvgGradient(StringUtils.getAsHtml(mapper.getMappedColor(gr.getColor1())),
					StringUtils.getAsHtml(mapper.getMappedColor(gr.getColor2())), gr.getPolicy());
			svg.setFillColor("url(#" + id + ")");
		} else {
			final String backcolorString = StringUtils.getAsSvg(mapper, back);
			svg.setFillColor(backcolorString);
		}

		svg.setStrokeColor(color);
		svg.setStrokeWidth(param.getStroke().getThickness(), param.getStroke().getDasharraySvg());

		svg.svgPolygon(shape.getDeltaShadow(), points);
	}
}
