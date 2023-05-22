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
package net.sourceforge.plantuml.klimt.drawing.svg;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.klimt.ClipContainer;
import net.sourceforge.plantuml.klimt.UClip;
import net.sourceforge.plantuml.klimt.UParam;
import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColorGradient;
import net.sourceforge.plantuml.klimt.drawing.UDriver;
import net.sourceforge.plantuml.klimt.font.FontConfiguration;
import net.sourceforge.plantuml.klimt.font.FontStyle;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.klimt.font.UFontContext;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.UText;

public class DriverTextSvg implements UDriver<UText, SvgGraphics> {
	// ::remove file when __HAXE__

	private final StringBounder stringBounder;
	private final ClipContainer clipContainer;

	public DriverTextSvg(StringBounder stringBounder, ClipContainer clipContainer) {
		if (stringBounder == null)
			System.err.println("stringBounder=" + stringBounder);
		this.stringBounder = stringBounder;
		this.clipContainer = clipContainer;
	}

	public void draw(UText shape, double x, double y, ColorMapper mapper, UParam param, SvgGraphics svg) {
		final UClip clip = clipContainer.getClip();
		if (clip != null && clip.isInside(x, y) == false)
			return;

		final FontConfiguration fontConfiguration = shape.getFontConfiguration();
		if (fontConfiguration.getColor().isTransparent())
			return;

		final UFont font = fontConfiguration.getFont();
		String fontWeight = null;
		if (fontConfiguration.containsStyle(FontStyle.BOLD) || font.isBold())
			fontWeight = "bold";

		String fontStyle = null;
		if (fontConfiguration.containsStyle(FontStyle.ITALIC) || font.isItalic())
			fontStyle = "italic";

		String textDecoration = null;
		if (fontConfiguration.containsStyle(FontStyle.UNDERLINE)
				&& fontConfiguration.getUnderlineStroke().getThickness() > 0) {
			textDecoration = "underline";
		} else if (fontConfiguration.containsStyle(FontStyle.STRIKE)) {
			textDecoration = "line-through";
		} else if (fontConfiguration.containsStyle(FontStyle.WAVE)) {
			// Beware that some current SVG implementations do not render the wave properly
			// (e.g. Chrome just draws a straight line)
			// Works ok on Firefox 85.
			textDecoration = "wavy underline";
		}

		String text = shape.getText();
		if (text.matches("^\\s*$"))
			text = text.replace(' ', (char) 160);

		if (text.startsWith(" ")) {
			final double space = stringBounder.calculateDimension(font, " ").getWidth();
			while (text.startsWith(" ")) {
				x += space;
				text = text.substring(1);
			}
		}
		text = StringUtils.trin(text);
		final XDimension2D dim = stringBounder.calculateDimension(font, text);

		String backColor = null;
		final double width = dim.getWidth();
		final double height = dim.getHeight();
		if (fontConfiguration.containsStyle(FontStyle.BACKCOLOR)) {
			final HColor back = fontConfiguration.getExtendedColor();
			if (back instanceof HColorGradient) {
				final HColorGradient gr = (HColorGradient) back;
				final String id = svg.createSvgGradient(gr.getColor1().toRGB(mapper), gr.getColor2().toRGB(mapper),
						gr.getPolicy());
				svg.setFillColor("url(#" + id + ")");
				svg.setStrokeColor(null);
				final double deltaPatch = 2;
				svg.svgRectangle(x, y - height + deltaPatch, width, height, 0, 0, 0, null, null);

			} else {
				backColor = back.toRGB(mapper);
			}
		}

		final HColor textColor = fontConfiguration.getColor();
		svg.setFillColor(textColor.toSvg(mapper));

		svg.text(text, x, y, font.getFamily(UFontContext.SVG), font.getSize(), fontWeight, fontStyle, textDecoration,
				width, fontConfiguration.getAttributes(), backColor);
	}
}
