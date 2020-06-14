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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.Pragma;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class SwimlanesC extends SwimlanesB {

	public SwimlanesC(ISkinParam skinParam, Pragma pragma) {
		super(skinParam, pragma);
	}

	@Override
	protected void drawWhenSwimlanes(UGraphic ug, TextBlock full) {
		super.drawWhenSwimlanes(ug, full);
		double x2 = 0;

		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimensionFull = full.calculateDimension(stringBounder);

		final UTranslate titleHeightTranslate = getTitleHeightTranslate(stringBounder);

		for (Swimlane swimlane : swimlanes) {
			drawSeparation(ug.apply(new UTranslate(x2, 0)), dimensionFull.getHeight() + titleHeightTranslate.getDy());

			x2 += swimlane.getActualWidth();

		}
		drawSeparation(ug.apply(new UTranslate(x2, 0)), dimensionFull.getHeight() + titleHeightTranslate.getDy());

	}

	private void drawSeparation(UGraphic ug, double height) {
		HtmlColor color = skinParam.getHtmlColor(ColorParam.swimlaneBorder, null, false);
		if (color == null) {
			color = ColorParam.swimlaneBorder.getDefaultValue();
		}
		final UStroke thickness = Rose.getStroke(skinParam, LineParam.swimlaneBorder, 2);
		ug.apply(thickness).apply(new UChangeColor(color)).draw(new ULine(0, height));
	}

}
