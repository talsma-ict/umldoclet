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
package net.sourceforge.plantuml.ebnf;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;

public class ETileConcatenation extends ETile {
    // ::remove folder when __HAXE__

	private final double marginx = 20;
	private final List<ETile> tiles = new ArrayList<>();

	@Override
	public void push(ETile tile) {
		tiles.add(0, tile);
	}

	public void overideFirst(ETile tile) {
		tiles.set(0, tile);
	}

	@Override
	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();

		final double fullLinePos = getH1(stringBounder);
		double x = 0;
		drawHline(ug, fullLinePos, 0, x);
		for (int i = 0; i < tiles.size(); i++) {
			final ETile tile = tiles.get(i);
			final double linePos = tile.getH1(stringBounder);
			tile.drawU(ug.apply(new UTranslate(x, fullLinePos - linePos)));
			x += tile.calculateDimension(stringBounder).getWidth();
			if (i != tiles.size() - 1) {
				drawHlineDirected(ug, fullLinePos, x, x + marginx, 0.5);
				x += marginx;
			}
		}

	}

	@Override
	public double getH1(StringBounder stringBounder) {
		double result = 0;

		for (ETile tile : tiles)
			result = Math.max(result, tile.getH1(stringBounder));

		return result;
	}

	@Override
	public double getH2(StringBounder stringBounder) {
		double result = 0;

		for (ETile tile : tiles)
			result = Math.max(result, tile.getH2(stringBounder));

		return result;
	}

	@Override
	public double getWidth(StringBounder stringBounder) {
		double width = 0;
		for (int i = 0; i < tiles.size(); i++) {
			final ETile tile = tiles.get(i);
			width += tile.getWidth(stringBounder);
			if (i != tiles.size() - 1)
				width += marginx;
		}
		return width;
	}

	public ETile getFirst() {
		return tiles.get(0);
	}

}
