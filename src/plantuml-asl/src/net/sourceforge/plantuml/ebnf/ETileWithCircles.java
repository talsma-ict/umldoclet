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

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.CopyForegroundColorToBackgroundColor;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class ETileWithCircles extends ETile {

	private static final double SIZE = 8;

	private final double deltax = 30;
	private final ETile orig;
	private final HColor lineColor;

	public ETileWithCircles(ETile orig, HColor lineColor) {
		this.orig = orig;
		this.lineColor = lineColor;
	}

	@Override
	public double getWidth(StringBounder stringBounder) {
		return orig.getWidth(stringBounder) + 2 * deltax;
	}

	@Override
	public double getH1(StringBounder stringBounder) {
		return orig.getH1(stringBounder);
	}

	@Override
	public double getH2(StringBounder stringBounder) {
		return orig.getH2(stringBounder);
	}

	@Override
	public void drawU(UGraphic ug) {
		final double linePos = getH1(ug.getStringBounder());
		final XDimension2D fullDim = calculateDimension(ug.getStringBounder());
		ug = ug.apply(lineColor).apply(new UStroke(1.5));
		orig.drawU(ug.apply(UTranslate.dx(deltax)));

		final UEllipse circle = new UEllipse(SIZE, SIZE);

		ug.apply(new UStroke(2)).apply(new UTranslate(0, linePos - SIZE / 2)).draw(circle);
		ug.apply(new UStroke(1)).apply(new CopyForegroundColorToBackgroundColor())
				.apply(new UTranslate(fullDim.getWidth() - SIZE / 2, linePos - SIZE / 2)).draw(circle);

		ug = ug.apply(new UStroke(1.5));
		drawHlineDirected(ug, linePos, SIZE, deltax, 0.5);
		drawHlineDirected(ug, linePos, fullDim.getWidth() - deltax, fullDim.getWidth() - SIZE / 2, 0.5);
	}

	@Override
	public void push(ETile tile) {
		throw new UnsupportedOperationException();
	}

}
