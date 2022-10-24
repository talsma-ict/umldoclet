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
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class ETileOneOrMore extends ETile {

	private final double deltax = 15;
	private final double deltay = 12;
	private final ETile orig;
	private final UText loop;
	private final FontConfiguration fc;

	public ETileOneOrMore(ETile orig, String loop, FontConfiguration fc, ISkinParam skinParam) {
		this.orig = orig;
		this.fc = fc;
		this.loop = loop == null ? null : new UText(loop, fc);
	}

	public ETileOneOrMore(ETile orig) {
		this(orig, null, null, null);
	}

	@Override
	public double getH1(StringBounder stringBounder) {
		double h1 = deltay + orig.getH1(stringBounder);
		if (loop != null)
			h1 += getBraceHeight();
		return h1;
	}

	private double getBraceHeight() {
		if (loop == null)
			return 0;
		return 15;
	}

	@Override
	public double getH2(StringBounder stringBounder) {
		return orig.getH2(stringBounder);
	}

	@Override
	public double getWidth(StringBounder stringBounder) {
		return orig.getWidth(stringBounder) + 2 * deltax;
	}

	@Override
	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D fullDim = calculateDimension(stringBounder);
		if (TRACE)
			ug.apply(HColors.RED).draw(new URectangle(fullDim));

		final double h1 = getH1(stringBounder);

		CornerCurved.createSW(8).drawU(ug.apply(new UTranslate(8, h1)));
		drawVline(ug, 8, 8 + 5 + getBraceHeight(), h1 - 8);
		CornerCurved.createNW(8).drawU(ug.apply(new UTranslate(8, 5 + getBraceHeight())));

		drawHlineAntiDirected(ug, 5 + getBraceHeight(), deltax, fullDim.getWidth() - deltax, 0.6);

		CornerCurved.createSE(8).drawU(ug.apply(new UTranslate(fullDim.getWidth() - 8, h1)));
		drawVline(ug, fullDim.getWidth() - 8, 8 + 5 + getBraceHeight(), h1 - 8);
		CornerCurved.createNE(8).drawU(ug.apply(new UTranslate(fullDim.getWidth() - 8, 5 + getBraceHeight())));

		drawHline(ug, h1, 0, deltax);
		drawHline(ug, h1, fullDim.getWidth() - deltax, fullDim.getWidth());

		orig.drawU(ug.apply(new UTranslate(deltax, deltay + getBraceHeight())));

		if (loop != null) {
			new Brace(fullDim.getWidth(), 10).drawU(ug.apply(new UTranslate(0, 10)));
			final XDimension2D dimText = stringBounder.calculateDimension(fc.getFont(), loop.getText());
			final double descent = stringBounder.getDescent(fc.getFont(), loop.getText());
			ug.apply(new UTranslate((fullDim.getWidth() - dimText.getWidth()) / 2, descent)).draw(loop);
//			final TextBlock icon = OpenIcon.retrieve("loop-circular").asTextBlock(fc.getColor(), 1.5);
//			icon.drawU(ug);
		}
	}

	@Override
	public void push(ETile tile) {
		throw new UnsupportedOperationException();
	}

}
