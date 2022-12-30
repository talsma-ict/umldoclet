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
package net.sourceforge.plantuml.ebnf;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FloatingNote;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class ETileBox extends ETile {

	private final String value;
	private final FontConfiguration fc;
	private final Style style;
	private final UText utext;
	private final HColorSet colorSet;
	private final Symbol symbol;
	private final ISkinParam skinParam;
	private String commentAbove;
	private String commentBelow;

	public ETileBox(String value, Symbol symbol, FontConfiguration fc, Style style, HColorSet colorSet,
			ISkinParam skinParam) {
		this.symbol = symbol;
		this.skinParam = skinParam;
		this.value = value;
		this.fc = fc;
		this.utext = new UText(value, fc);
		this.style = style;
		this.colorSet = colorSet;
	}

	private double getPureH1(StringBounder stringBounder) {
		final double height = getTextDim(stringBounder).getHeight() + 10;
		return height / 2;
	}

	@Override
	public double getH1(StringBounder stringBounder) {
		double h1 = getPureH1(stringBounder);
		final TextBlock note = getNoteAbove(stringBounder);
		if (note != TextBlockUtils.EMPTY_TEXT_BLOCK)
			h1 += note.calculateDimension(stringBounder).getHeight() + 20;
		return h1;
	}

	@Override
	public double getH2(StringBounder stringBounder) {
		double h2 = getPureH1(stringBounder);
		final TextBlock note = getNoteBelow(stringBounder);
		if (note != TextBlockUtils.EMPTY_TEXT_BLOCK)
			h2 += note.calculateDimension(stringBounder).getHeight() + 20;
		return h2;
	}

	@Override
	public double getWidth(StringBounder stringBounder) {
		double width = getTextDim(stringBounder).getWidth() + 10;
		final TextBlock noteAbove = getNoteAbove(stringBounder);
		if (noteAbove != TextBlockUtils.EMPTY_TEXT_BLOCK)
			width = Math.max(width, noteAbove.calculateDimension(stringBounder).getWidth());

		final TextBlock noteBelow = getNoteBelow(stringBounder);
		if (noteBelow != TextBlockUtils.EMPTY_TEXT_BLOCK)
			width = Math.max(width, noteBelow.calculateDimension(stringBounder).getWidth());

		return width;
	}

	private XDimension2D getTextDim(StringBounder stringBounder) {
		return stringBounder.calculateDimension(fc.getFont(), value);
	}

	private XDimension2D getBoxDim(StringBounder stringBounder) {
		return getTextDim(stringBounder).delta(10);
	}

	@Override
	protected void addCommentAbove(String comment) {
		this.commentAbove = comment;
	}

	@Override
	protected void addCommentBelow(String comment) {
		this.commentBelow = comment;
	}

	@Override
	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dim = calculateDimension(stringBounder);
		final XDimension2D dimText = getTextDim(stringBounder);
		final XDimension2D dimBox = getBoxDim(stringBounder);
		final HColor lineColor = style.value(PName.LineColor).asColor(colorSet);
		final HColor backgroundColor = style.value(PName.BackGroundColor).asColor(colorSet);

		final TextBlock noteAbove = getNoteAbove(stringBounder);
		final TextBlock noteBelow = getNoteBelow(stringBounder);
		final double posy = noteAbove == TextBlockUtils.EMPTY_TEXT_BLOCK ? 0
				: noteAbove.calculateDimension(stringBounder).getHeight() + 20;

		final double posxBox = (dim.getWidth() - dimBox.getWidth()) / 2;

		if (symbol == Symbol.TERMINAL_STRING1 || symbol == Symbol.TERMINAL_STRING2) {
			final URectangle rect = new URectangle(dimBox);
			ug.apply(new UTranslate(posxBox, posy)).apply(lineColor).apply(new UStroke(0.5)).draw(rect);
		} else if (symbol == Symbol.SPECIAL_SEQUENCE) {
			final URectangle rect = new URectangle(dimBox);
			ug.apply(new UTranslate(posxBox, posy)).apply(lineColor).apply(new UStroke(5, 5, 1)).draw(rect);
//			final URectangle rect1 = new URectangle(dimBox.delta(2)).rounded(12);
//			final URectangle rect2 = new URectangle(dimBox.delta(-2)).rounded(8);
//			ug.apply(new UTranslate(posxBox - 1, posy - 1)).apply(lineColor).apply(new UStroke(5.0, 5.0, 1.0)).draw(rect1);
//			ug.apply(new UTranslate(posxBox + 1, posy + 1)).apply(lineColor).apply(new UStroke(0.5)).draw(rect2);
		} else {
			final URectangle rect = new URectangle(dimBox).rounded(10);
			ug.apply(new UTranslate(posxBox, posy)).apply(lineColor).apply(backgroundColor.bg()).apply(new UStroke(1.5))
					.draw(rect);
		}

		ug.apply(new UTranslate(5 + posxBox, posy + 5 + dimText.getHeight() - utext.getDescent(stringBounder)))
				.draw(utext);

		if (noteAbove != TextBlockUtils.EMPTY_TEXT_BLOCK) {
			final double posxAbove = (dim.getWidth() - noteAbove.calculateDimension(stringBounder).getWidth()) / 2;
			noteAbove.drawU(ug.apply(new UTranslate(posxAbove, 0)));
		}

		if (noteBelow != TextBlockUtils.EMPTY_TEXT_BLOCK) {
			final double posxBelow = (dim.getWidth() - noteBelow.calculateDimension(stringBounder).getWidth()) / 2;
			final double posyBelow = dim.getHeight() - noteBelow.calculateDimension(stringBounder).getHeight();
			noteBelow.drawU(ug.apply(new UTranslate(posxBelow, posyBelow)));
		}

		if (posxBox > 0) {
			drawHlineDirected(ug, getH1(stringBounder), 0, posxBox, .5);
			drawHlineDirected(ug, getH1(stringBounder), posxBox + dimBox.getWidth(), dim.getWidth(), .5);
		}

	}

	private TextBlock getNoteAbove(StringBounder stringBounder) {
		if (commentAbove == null)
			return TextBlockUtils.EMPTY_TEXT_BLOCK;

		final FloatingNote note = FloatingNote.createOpale(Display.getWithNewlines(commentAbove), skinParam,
				SName.ebnf);
		final XDimension2D dim = note.calculateDimension(stringBounder);
		final double pos = dim.getWidth() * .5;
		XPoint2D pp1 = new XPoint2D(pos, dim.getHeight());
		XPoint2D pp2 = new XPoint2D(pos, 20 + dim.getHeight());
		note.setOpale(Direction.DOWN, pp1, pp2);
		return note;
	}

	private TextBlock getNoteBelow(StringBounder stringBounder) {
		if (commentBelow == null)
			return TextBlockUtils.EMPTY_TEXT_BLOCK;

		final FloatingNote note = FloatingNote.createOpale(Display.getWithNewlines(commentBelow), skinParam,
				SName.ebnf);
		final XDimension2D dim = note.calculateDimension(stringBounder);
		final double pos = dim.getWidth() * .5;
		XPoint2D pp1 = new XPoint2D(pos, 0);
		XPoint2D pp2 = new XPoint2D(pos, -20);
		note.setOpale(Direction.UP, pp1, pp2);
		return note;
	}

	@Override
	public void push(ETile tile) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String getRepetitionLabel() {
		return value;
	}

}
