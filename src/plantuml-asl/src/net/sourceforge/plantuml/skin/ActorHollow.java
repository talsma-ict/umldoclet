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
package net.sourceforge.plantuml.skin;

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ActorHollow extends AbstractTextBlock implements TextBlock {

	private final double headDiam = 9;
	private final double bodyWidth = 25;
	private final double bodyHeight = 21;

	private final double neckHeight = 2;

	private final double armThickness = 5;
	private final double bodyThickness = 6;
	private final double legThickness = 6;

	private final SymbolContext symbolContext;

	public ActorHollow(SymbolContext symbolContext) {
		this.symbolContext = symbolContext;
	}

	public void drawU(UGraphic ug) {

		final UEllipse head = new UEllipse(headDiam, headDiam);
		final double centerX = getPreferredWidth() / 2;

		final UPath path = new UPath();
		path.moveTo(-bodyWidth/2, 0);
		path.lineTo(-bodyWidth / 2, armThickness);
		path.lineTo(-bodyThickness / 2, armThickness);
		path.lineTo(-bodyThickness / 2, bodyHeight - (bodyWidth + legThickness * Math.sqrt(2) - bodyThickness) / 2);
		path.lineTo(-bodyWidth / 2, bodyHeight - legThickness * Math.sqrt(2) / 2);
		path.lineTo(-(bodyWidth / 2 - legThickness * Math.sqrt(2) / 2), bodyHeight);

		path.lineTo(0, bodyHeight - (bodyWidth / 2 - legThickness * Math.sqrt(2) / 2));

		path.lineTo(+(bodyWidth / 2 - legThickness * Math.sqrt(2) / 2), bodyHeight);
		path.lineTo(+bodyWidth / 2, bodyHeight - legThickness * Math.sqrt(2) / 2);
		path.lineTo(+bodyThickness / 2, bodyHeight - (bodyWidth + legThickness * Math.sqrt(2) - bodyThickness) / 2);
		path.lineTo(+bodyThickness / 2, armThickness);
		path.lineTo(+bodyWidth / 2, armThickness);
		path.lineTo(+bodyWidth / 2, 0);
		path.lineTo(-bodyWidth/2, 0);
		path.closePath();

		if (symbolContext.getDeltaShadow() != 0) {
			head.setDeltaShadow(symbolContext.getDeltaShadow());
			path.setDeltaShadow(symbolContext.getDeltaShadow());
		}
		ug = symbolContext.apply(ug);
		ug.apply(new UTranslate(centerX - head.getWidth() / 2, thickness())).draw(head);
		ug.apply(new UTranslate(centerX, head.getHeight() + thickness() + neckHeight)).draw(path);
	}

	private double thickness() {
		return symbolContext.getStroke().getThickness();
	}

	public double getPreferredWidth() {
		return bodyWidth + thickness() * 2;
	}

	public double getPreferredHeight() {
		return headDiam + neckHeight + bodyHeight + thickness() * 2 + symbolContext.getDeltaShadow();
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(getPreferredWidth(), getPreferredHeight());
	}
}
