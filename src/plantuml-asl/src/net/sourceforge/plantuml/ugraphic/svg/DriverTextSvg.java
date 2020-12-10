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

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.FontStyle;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.svg.SvgGraphics;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UFontContext;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.color.ColorMapper;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorGradient;

public class DriverTextSvg implements UDriver<SvgGraphics> {

	private final StringBounder stringBounder;
	private final ClipContainer clipContainer;

	public DriverTextSvg(StringBounder stringBounder, ClipContainer clipContainer) {
		this.stringBounder = stringBounder;
		this.clipContainer = clipContainer;
	}

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, SvgGraphics svg) {

		final UClip clip = clipContainer.getClip();
		if (clip != null && clip.isInside(x, y) == false) {
			return;
		}

		final UText shape = (UText) ushape;
		final FontConfiguration fontConfiguration = shape.getFontConfiguration();
		final UFont font = fontConfiguration.getFont();
		String fontWeight = null;
		if (fontConfiguration.containsStyle(FontStyle.BOLD) || font.isBold()) {
			fontWeight = "bold";
		}
		String fontStyle = null;
		if (fontConfiguration.containsStyle(FontStyle.ITALIC) || font.isItalic()) {
			fontStyle = "italic";
		}
		String textDecoration = null;
		if (fontConfiguration.containsStyle(FontStyle.UNDERLINE)) {
			textDecoration = "underline";
		} else if (fontConfiguration.containsStyle(FontStyle.STRIKE)) {
			textDecoration = "line-through";
		}

		String text = shape.getText();
		if (text.startsWith(" ")) {
			final double space = stringBounder.calculateDimension(font, " ").getWidth();
			while (text.startsWith(" ")) {
				x += space;
				text = text.substring(1);
			}
		}
		text = StringUtils.trin(text);
		final Dimension2D dim = stringBounder.calculateDimension(font, text);

		String backColor = null;
		final double width = dim.getWidth();
		final double height = dim.getHeight();
		if (fontConfiguration.containsStyle(FontStyle.BACKCOLOR)) {
			final HColor back = fontConfiguration.getExtendedColor();
			if (back instanceof HColorGradient) {
				final HColorGradient gr = (HColorGradient) back;
				final String id = svg.createSvgGradient(mapper.toRGB(gr.getColor1()), mapper.toRGB(gr.getColor2()),
						gr.getPolicy());
				svg.setFillColor("url(#" + id + ")");
				svg.setStrokeColor(null);
				final double deltaPatch = 2;
				svg.svgRectangle(x, y - height + deltaPatch, width, height, 0, 0, 0, null, null);

			} else {
				backColor = mapper.toRGB(back);
			}
		}

		svg.setFillColor(mapper.toSvg(fontConfiguration.getColor()));
		svg.text(text, x, y, font.getFamily(UFontContext.SVG), font.getSize(), fontWeight, fontStyle, textDecoration,
				width, fontConfiguration.getAttributes(), backColor);
	}
}
