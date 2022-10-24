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

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FloatingNote;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class ETileOptional extends ETile {

	private final double deltax;
	private final double deltay = 16;
	private final ETile orig;
	private final ISkinParam skinParam;
	private String commentAbove;
	private String commentBelow;

	private final boolean specialForAlternate;

	public ETileOptional(ETile orig, ISkinParam skinParam) {
		this.skinParam = skinParam;
		this.orig = orig;
		this.specialForAlternate = orig instanceof ETileAlternation;
		this.deltax = this.specialForAlternate ? 0 : 15;
	}

	@Override
	public double getH1(StringBounder stringBounder) {
		final TextBlock note = getNoteAbove(stringBounder);
		return deltay + orig.getH1(stringBounder) + note.calculateDimension(stringBounder).getHeight();
	}

	private double getDeltaY(StringBounder stringBounder) {
		final TextBlock note = getNoteAbove(stringBounder);
		return deltay + note.calculateDimension(stringBounder).getHeight();
	}

	@Override
	public double getH2(StringBounder stringBounder) {
		final TextBlock note = getNoteBelow(stringBounder);
		return orig.getH2(stringBounder) + note.calculateDimension(stringBounder).getHeight();
	}

	@Override
	public double getWidth(StringBounder stringBounder) {
		return orig.getWidth(stringBounder) + 2 * deltax;
	}

	@Override
	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dim = calculateDimension(stringBounder);
		if (TRACE)
			ug.apply(HColors.BLUE).draw(new URectangle(dim));

		final double linePos = getH1(stringBounder);

		final double posA = specialForAlternate ? 12 : 8;
		final double corner = specialForAlternate ? 12 : 8;
		final double posB = dim.getWidth() - posA;

		final double posYoptionalLine = getDeltaY(stringBounder) - 11;

		CornerCurved.createSE(corner).drawU(ug.apply(new UTranslate(posA, linePos)));
		drawVline(ug, posA, corner + posYoptionalLine, linePos - corner);
		CornerCurved.createNW(corner).drawU(ug.apply(new UTranslate(posA, posYoptionalLine)));

		drawHlineDirected(ug, posYoptionalLine, posA + corner, posB - corner, 0.4);

		CornerCurved.createSW(corner).drawU(ug.apply(new UTranslate(posB, linePos)));
		drawVline(ug, posB, corner + posYoptionalLine, linePos - corner);
		CornerCurved.createNE(corner).drawU(ug.apply(new UTranslate(posB, posYoptionalLine)));

		drawHline(ug, linePos, 0, deltax);
		drawHline(ug, linePos, dim.getWidth() - deltax, dim.getWidth());

		orig.drawU(ug.apply(new UTranslate(deltax, getDeltaY(stringBounder))));

		final TextBlock noteAbove = getNoteAbove(stringBounder);
		if (noteAbove != TextBlockUtils.EMPTY_TEXT_BLOCK) {
			final double pos2 = (dim.getWidth() - noteAbove.calculateDimension(stringBounder).getWidth()) / 2;
			noteAbove.drawU(ug.apply(UTranslate.dx(pos2)));
		}

		final TextBlock noteBelow = getNoteBelow(stringBounder);
		if (noteBelow != TextBlockUtils.EMPTY_TEXT_BLOCK) {
			final XDimension2D dimBelow = noteBelow.calculateDimension(stringBounder);
			final double pos2 = (dim.getWidth() - dimBelow.getWidth()) / 2;
			noteBelow.drawU(ug.apply(new UTranslate(pos2, dim.getHeight() - dimBelow.getHeight())));
		}

	}

	@Override
	public void push(ETile tile) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void addCommentAbove(String comment) {
		this.commentAbove = comment;
	}

	@Override
	protected void addCommentBelow(String comment) {
		this.commentBelow = comment;
	}

	private TextBlock getNoteAbove(StringBounder stringBounder) {
		if (commentAbove == null)
			return TextBlockUtils.EMPTY_TEXT_BLOCK;
		final FloatingNote note = FloatingNote.create(Display.getWithNewlines(commentAbove), skinParam, SName.ebnf);
		return TextBlockUtils.withMargin(note, 0, 0, 0, 10);
	}

	private TextBlock getNoteBelow(StringBounder stringBounder) {
		if (commentBelow == null)
			return TextBlockUtils.EMPTY_TEXT_BLOCK;
		final FloatingNote note = FloatingNote.create(Display.getWithNewlines(commentBelow), skinParam, SName.ebnf);
		return TextBlockUtils.withMargin(note, 0, 0, 10, 0);
	}

}
