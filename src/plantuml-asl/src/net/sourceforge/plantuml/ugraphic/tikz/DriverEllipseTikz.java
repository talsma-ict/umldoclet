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
package net.sourceforge.plantuml.ugraphic.tikz;

import net.sourceforge.plantuml.tikz.TikzGraphics;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;

public class DriverEllipseTikz implements UDriver<TikzGraphics> {

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, TikzGraphics tikz) {
		final UEllipse shape = (UEllipse) ushape;
		final double width = shape.getWidth();
		final double height = shape.getHeight();

		double start = shape.getStart();
		final double extend = shape.getExtend();
		final double cx = x + width / 2;
		final double cy = y + height / 2;
		tikz.setFillColor(mapper.getMappedColor(param.getBackcolor()));
		tikz.setStrokeColor(mapper.getMappedColor(param.getColor()));
		tikz.setStrokeWidth(param.getStroke().getThickness(), param.getStroke().getDashTikz());
		if (start == 0 && extend == 0) {
			tikz.ellipse(cx, cy, width / 2, height / 2);
		} else {
			start = start + 90;
			final double x1 = cx + Math.sin(start * Math.PI / 180.) * width / 2;
			final double y1 = cy + Math.cos(start * Math.PI / 180.) * height / 2;
			final double x2 = cx + Math.sin((start + extend) * Math.PI / 180.) * width / 2;
			final double y2 = cy + Math.cos((start + extend) * Math.PI / 180.) * height / 2;
			// start = start + 360;
			// tikz.arc(x2, y2, (int) start, (int) (start - 45), (width + height) / 4);

			tikz.arc(x1, y1, (int) ((360-(start+270))), (int) (360-((start+270+extend))), (width + height) / 4);
			// tikz.arc(x1, y1, (int) (start + 270 + extend), (int) (start + 270), (width + height) / 4);
			// // http://www.itk.ilstu.edu/faculty/javila/SVG/SVG_drawing1/elliptical_curve.htm
			// // svg.svgEllipse(x1, y1, 1, 1, 0);
			// // svg.svgEllipse(x2, y2, 1, 1, 0);
			// svg.svgArcEllipse(width / 2, height / 2, x1, y1, x2, y2);
		}
	}
}
