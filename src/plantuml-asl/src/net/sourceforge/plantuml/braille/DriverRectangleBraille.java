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
package net.sourceforge.plantuml.braille;

import java.awt.geom.Rectangle2D;

import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;

public class DriverRectangleBraille implements UDriver<BrailleGrid> {

	private final ClipContainer clipContainer;

	public DriverRectangleBraille(ClipContainer clipContainer) {
		this.clipContainer = clipContainer;
	}

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, BrailleGrid grid) {
		final URectangle rect = (URectangle) ushape;

		// final double rx = rect.getRx();
		// final double ry = rect.getRy();
		double width = rect.getWidth();
		double height = rect.getHeight();

		// final String color = StringUtils.getAsSvg(mapper, param.getColor());
		// final HtmlColor back = param.getBackcolor();
		// if (back instanceof HtmlColorGradient) {
		// final HtmlColorGradient gr = (HtmlColorGradient) back;
		// final String id = svg.createSvgGradient(StringUtils.getAsHtml(mapper.getMappedColor(gr.getColor1())),
		// StringUtils.getAsHtml(mapper.getMappedColor(gr.getColor2())), gr.getPolicy());
		// svg.setFillColor("url(#" + id + ")");
		// svg.setStrokeColor(color);
		// } else {
		// final String backcolor = StringUtils.getAsSvg(mapper, back);
		// svg.setFillColor(backcolor);
		// svg.setStrokeColor(color);
		// }
		//
		// svg.setStrokeWidth(param.getStroke().getThickness(), param.getStroke().getDasharraySvg());

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
		grid.rectangle(x, y, width, height);
	}
}
