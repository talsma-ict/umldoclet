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
package net.sourceforge.plantuml.wbs;

import java.awt.geom.Dimension2D;
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
	private final List<ITF> right = new ArrayList<ITF>();

	public Fork(ISkinParam skinParam, WElement idea) {
		super(skinParam, idea.getStyleBuilder(), idea.getLevel());
		if (idea.getLevel() != 0) {
			throw new IllegalArgumentException();
		}
		this.main = buildMain(idea);
		for (WElement child : idea.getChildren(Direction.RIGHT)) {
			this.right.add(ITFComposed.build2(skinParam, child));
		}
	}

	final private double delta1x = 20;
	final private double deltay = 40;

	public void drawU(final UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D fullDim = calculateDimension(stringBounder);
		final Dimension2D mainDim = main.calculateDimension(stringBounder);
		final double dx = (fullDim.getWidth() - mainDim.getWidth()) / 2;
		main.drawU(ug.apply(UTranslate.dx(dx)));
		drawLine(ug, dx + mainDim.getWidth() / 2, mainDim.getHeight(), dx + mainDim.getWidth() / 2,
				mainDim.getHeight() + deltay / 2);
		double x = 0;
		final double y = mainDim.getHeight() + deltay;
		if (right.size() == 0) {
			return;
		}
		final double firstX = right.get(0).getT1(stringBounder).getX();
		double lastX = firstX;
		for (ITF child : right) {
			lastX = x + child.getT1(stringBounder).getX();
			drawLine(ug, lastX, mainDim.getHeight() + deltay / 2, lastX, y);
			child.drawU(ug.apply(new UTranslate(x, y)));
			x += child.calculateDimension(stringBounder).getWidth() + delta1x;
		}
		lastX = Math.max(lastX, dx + mainDim.getWidth() / 2);
		drawLine(ug, firstX, mainDim.getHeight() + deltay / 2, lastX, mainDim.getHeight() + deltay / 2);

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
