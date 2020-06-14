/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.jungle;

import java.awt.geom.Dimension2D;
import java.util.Arrays;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GTileLeftRight extends AbstractTextBlock implements GTile {

	private final GTile left;
	private final GTile right;
	private final double space;
	private final double step = 5;

	public GTileLeftRight(GTile left, GTile right, double space) {
		this.left = left;
		this.right = right;
		this.space = space;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final GTileGeometry dimLeft = left.calculateDimension(stringBounder);
		final GTileGeometry dimRight = right.calculateDimension(stringBounder);
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final double deltaH1 = dimTotal.getHeight() - dimLeft.getHeight();
		final double deltaH2 = dimTotal.getHeight() - dimRight.getHeight();
		left.drawU(ug.apply(new UTranslate(0, deltaH1 / 2)));
		final double dx2 = dimLeft.getWidth() + space;
		right.drawU(ug.apply(new UTranslate(dx2, deltaH2 / 2)));

		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
		final double step = dimLeft.getHeight() / (dimRight.getWestPositions().size() + 1);
		double ystart = step + deltaH1 / 2;
		for (Double w2 : dimRight.getWestPositions()) {
			line(ug, dimLeft.getWidth(), ystart, dx2, w2 + deltaH2 / 2);
			ystart += step;
		}

	}

	private void line(UGraphic ug, double x1, double y1, double x2, double y2) {
		// final ULine line = new ULine(x2 - x1, y2 - y1);
		// ug.apply(new UTranslate(x1, y1)).draw(line);
		final UPath path = new UPath();
		path.moveTo(x1, y1);
		path.lineTo(x1 + step, y1);
		path.lineTo(x2 - step, y2);
		path.lineTo(x2, y2);
		ug.apply(new UTranslate(0, 0)).draw(path);
	}

	public GTileGeometry calculateDimension(StringBounder stringBounder) {
		final GTileGeometry dimLeft = left.calculateDimension(stringBounder);
		final Dimension2D dimRight = right.calculateDimension(stringBounder);
		final Dimension2D dimTotal = new Dimension2DDouble(dimLeft.getWidth() + space + dimRight.getHeight(), Math.max(
				dimLeft.getHeight(), dimRight.getHeight()));
		final double deltaH1 = dimTotal.getHeight() - dimLeft.getHeight();
		final double west = dimLeft.getWestPositions().get(0) + deltaH1 / 2;
		return new GTileGeometry(dimTotal, Arrays.asList(west));
	}
}
