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
package net.sourceforge.plantuml.png;

import java.awt.Font;
import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplaySection;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class PngTitler {

	private final HColor textColor;
	private final HColor hyperlinkColor;
	private final DisplaySection text;
	private final int fontSize;
	private final String fontFamily;
	private final boolean useUnderlineForHyperlink;
	private final Style style;
	private final HColorSet set;
	private final ISkinSimple spriteContainer;

	public PngTitler(HColor textColor, DisplaySection text, int fontSize, String fontFamily, HColor hyperlinkColor,
			boolean useUnderlineForHyperlink, Style style, HColorSet set, ISkinSimple spriteContainer) {
		this.style = style;
		this.set = set;
		this.spriteContainer = spriteContainer;

		if (UseStyle.useBetaStyle()) {
			textColor = style.value(PName.FontColor).asColor(spriteContainer.getThemeStyle(), set);
			fontSize = style.value(PName.FontSize).asInt();
			fontFamily = style.value(PName.FontName).asString();
			hyperlinkColor = style.value(PName.HyperLinkColor).asColor(spriteContainer.getThemeStyle(), set);
		}
		this.textColor = textColor;
		this.text = text;
		this.fontSize = fontSize;
		this.fontFamily = fontFamily;
		this.hyperlinkColor = hyperlinkColor;
		this.useUnderlineForHyperlink = useUnderlineForHyperlink;

	}

	public Dimension2D getTextDimension(StringBounder stringBounder) {
		final TextBlock textBloc = getRibbonBlock();
		if (textBloc == null) {
			return null;
		}
		return textBloc.calculateDimension(stringBounder);
	}

	public TextBlock getRibbonBlock() {
		if (UseStyle.useBetaStyle()) {
			final Display display = text.getDisplay();
			if (display == null) {
				return null;
			}
			return style.createTextBlockBordered(display, set, spriteContainer);
		}
		final UFont normalFont = new UFont(fontFamily, Font.PLAIN, fontSize);
		return text.createRibbon(new FontConfiguration(normalFont, textColor, hyperlinkColor, useUnderlineForHyperlink),
				new SpriteContainerEmpty(), null);
	}
}
