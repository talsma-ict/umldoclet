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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ETileAlternation extends ETile {

	private final List<ETile> tiles = new ArrayList<>();
	private final double marginx = 12;
	private boolean inZeroOrMore;

	@Override
	public void push(ETile tile) {
		tiles.add(0, tile);
	}

	@Override
	public double getH1(StringBounder stringBounder) {
		return tiles.get(0).getH1(stringBounder);
	}

	@Override
	public double getH2(StringBounder stringBounder) {
		double height = tiles.get(0).getH2(stringBounder);
		for (int i = 1; i < tiles.size(); i++) {
			final ETile tile = tiles.get(i);
			height += tile.getH1(stringBounder);
			height += tile.getH2(stringBounder);
			height += 10;
		}
		return height;
	}

	@Override
	public double getWidth(StringBounder stringBounder) {
		double width = 0;
		for (ETile tile : tiles) {
			final XDimension2D dim = tile.calculateDimension(stringBounder);
			width = Math.max(width, dim.getWidth());
		}
		width += 2 * 2 * marginx;
		return width;
	}

	@Override
	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();

		double y = 0;
		double lastLinePos = 0;

		final double a = 0;
		final double b = a + marginx;
		final double c = b + marginx;

		final XDimension2D fullDim = calculateDimension(stringBounder);

		final double r = fullDim.getWidth();
		final double q = r - marginx;
		final double p = q - marginx;

		final double linePos = getH1(stringBounder);

		if (inZeroOrMore) {
			for (int i = 0; i < tiles.size(); i++) {
				final ETile tile = tiles.get(i);
				final XDimension2D dim = tile.calculateDimension(stringBounder);
				lastLinePos = y + tile.getH1(stringBounder);
				tile.drawU(ug.apply(new UTranslate(c, y)));

				CornerCurved.createSW(marginx).drawU(ug.apply(new UTranslate(b, lastLinePos)));
				drawHlineDirected(ug, lastLinePos, c + dim.getWidth(), p, 0.3);
				CornerCurved.createSE(marginx).drawU(ug.apply(new UTranslate(q, lastLinePos)));

				y += dim.getHeight() + 10;

			}
			drawVline(ug, b, 0, lastLinePos - marginx);
			drawVline(ug, q, 0, lastLinePos - marginx);

		} else {
			for (int i = 0; i < tiles.size(); i++) {
				final ETile tile = tiles.get(i);
				final XDimension2D dim = tile.calculateDimension(stringBounder);
				lastLinePos = y + tile.getH1(stringBounder);
				tile.drawU(ug.apply(new UTranslate(c, y)));

				if (i == 0) {
					drawHline(ug, lastLinePos, a, c);
					drawHline(ug, lastLinePos, c + dim.getWidth(), r);
				} else if (i > 0 && i < tiles.size() - 1) {
					CornerCurved.createSW(marginx).drawU(ug.apply(new UTranslate(b, lastLinePos)));
					drawHlineDirected(ug, lastLinePos, c + dim.getWidth(), p, 0.5);
					CornerCurved.createSE(marginx).drawU(ug.apply(new UTranslate(q, lastLinePos)));
				} else if (i == tiles.size() - 1) {
					drawHlineDirected(ug, lastLinePos, c + dim.getWidth(), p, 0.5);

				}
				y += dim.getHeight() + 10;
			}

			final double height42 = lastLinePos - linePos;
			final UGraphic ug_b = ug.apply(new UTranslate(b, linePos));
			final UGraphic ug_q = ug.apply(new UTranslate(q, linePos));

			CornerCurved.createSW(marginx).drawU(ug_b.apply(UTranslate.dy(height42)));
			drawVline(ug_b, 0, marginx, height42 - marginx);
			CornerCurved.createNE(marginx).drawU(ug_b);

			CornerCurved.createSE(marginx).drawU(ug_q.apply(UTranslate.dy(height42)));
			drawVline(ug_q, 0, marginx, height42 - marginx);
			CornerCurved.createNW(marginx).drawU(ug_q);

		}

	}

	public void setInZeroOrMore(boolean inZeroOrMore) {
		this.inZeroOrMore = true;

	}

}
