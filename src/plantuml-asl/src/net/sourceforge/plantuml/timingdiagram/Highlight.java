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
package net.sourceforge.plantuml.timingdiagram;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;

public class Highlight {

	private final TimeTick tickFrom;
	private final TimeTick tickTo;
	private final Display caption;
	private final Colors colors;
	private final ISkinParam skinParam;

	public Highlight(ISkinParam skinParam, TimeTick tickFrom, TimeTick tickTo, Display caption, Colors colors) {
		this.tickFrom = tickFrom;
		this.tickTo = tickTo;
		this.caption = caption;
		this.colors = colors;
		this.skinParam = skinParam;
	}

	private StyleSignatureBasic getStyleSignature() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.timingDiagram, SName.highlight);
	}

	private Style getStyle() {
		return getStyleSignature().getMergedStyle(skinParam.getCurrentStyleBuilder());

	}

	private HColor getBackColor() {
		final HColor result = colors.getColor(ColorType.BACK);
		if (result == null) 
			return getStyle().value(PName.BackGroundColor).asColor(skinParam.getThemeStyle(),
					skinParam.getIHtmlColorSet());
		
		return result;
	}

	private HColor getLineColor() {
		final HColor result = colors.getColor(ColorType.LINE);
		if (result == null) 
			return getStyle().value(PName.LineColor).asColor(skinParam.getThemeStyle(), skinParam.getIHtmlColorSet());
		
		return result;
	}

	private UStroke getUStroke() {
		return getStyle().getStroke();
	}

	public final TimeTick getTickFrom() {
		return tickFrom;
	}

	public final TimeTick getTickTo() {
		return tickTo;
	}

	public final Display getCaption() {
		return caption;
	}

	public TextBlock getCaption(ISkinParam skinParam) {
		final FontConfiguration fc = FontConfiguration.create(skinParam, FontParam.TIMING, null);
		return caption.create(fc, HorizontalAlignment.LEFT, skinParam);
	}

	public void drawHighlightsBack(UGraphic ug, TimingRuler ruler, double height) {
		ug = ug.apply(new HColorNone()).apply(getBackColor().bg());
		final double start = ruler.getPosInPixel(this.getTickFrom());
		final double end = ruler.getPosInPixel(this.getTickTo());
		final URectangle rect = new URectangle(end - start, height);
		ug.apply(UTranslate.dx(start)).draw(rect);
	}

	public void drawHighlightsLines(UGraphic ug, TimingRuler ruler, double height) {
		ug = ug.apply(getUStroke());
		ug = ug.apply(getLineColor());
		final ULine line = ULine.vline(height);
		final double start = ruler.getPosInPixel(this.getTickFrom());
		final double end = ruler.getPosInPixel(this.getTickTo());
		ug.apply(UTranslate.dx(start)).draw(line);
		ug.apply(UTranslate.dx(end)).draw(line);
	}

}
