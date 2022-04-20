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
package net.sourceforge.plantuml.wbs;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class Fork extends WBSTextBlock {

	private final TextBlock main;
	private final List<ITF> right = new ArrayList<>();

	public Fork(ISkinParam skinParam, WElement idea) {
		super(idea.withBackColor(skinParam), idea.getStyleBuilder(), idea.getLevel());
		if (idea.getLevel() != 0)
			throw new IllegalArgumentException();

		this.main = buildMain(idea);
		for (WElement child : idea.getChildren(Direction.RIGHT))
			this.right.add(ITFComposed.build2(skinParam, child));

	}

	final private double delta1x = 20;
	final private double deltay = 40;

	public void drawU(final UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D mainDim = main.calculateDimension(stringBounder);

		final double y0 = mainDim.getHeight();
		final double y1 = y0 + deltay / 2;
		final double y2 = y0 + deltay;
		final double mainWidth = mainDim.getWidth();

		if (right.size() == 0) {
			main.drawU(ug);
			drawLine(ug, mainWidth / 2, y0, mainWidth / 2, y1);
			return;
		}

		double x = 0;
		final double firstX = right.get(0).getT1(stringBounder).getX();
		double lastX = firstX;

		for (ITF child : right) {
			lastX = x + child.getT1(stringBounder).getX();
			drawLine(ug, lastX, y1, lastX, y2);
			child.drawU(ug.apply(new UTranslate(x, y2)));
			x += child.calculateDimension(stringBounder).getWidth() + delta1x;
		}

		final double posMain;
		if (lastX > firstX) {
			drawLine(ug, firstX, y1, lastX, y1);
			posMain = firstX + (lastX - firstX - mainWidth) / 2;
		} else {
			assert lastX == firstX;
			final Dimension2D fullDim = calculateDimension(stringBounder);
			posMain = (fullDim.getWidth() - mainWidth) / 2;
			drawLine(ug, firstX, y1, posMain + mainWidth / 2, y1);
		}
		main.drawU(ug.apply(UTranslate.dx(posMain)));
		drawLine(ug, posMain + mainWidth / 2, y0, posMain + mainWidth / 2, y1);

	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		double width = 0;
		double height = 0;
		for (ITF child : right) {
			final Dimension2D childDim = child.calculateDimension(stringBounder);
			height = Math.max(height, childDim.getHeight());
			width += childDim.getWidth();
		}
		final Dimension2D mainDim = main.calculateDimension(stringBounder);
		height += mainDim.getHeight();
		height += deltay;
		width = Math.max(width, mainDim.getWidth());
		return new Dimension2DDouble(width, height);
	}

}
