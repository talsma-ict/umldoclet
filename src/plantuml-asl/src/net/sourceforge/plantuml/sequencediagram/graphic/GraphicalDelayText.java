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
package net.sourceforge.plantuml.sequencediagram.graphic;

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class GraphicalDelayText extends GraphicalElement {

	private final Component compText;

	private final ParticipantBox p1;
	private final ParticipantBox p2;

	public GraphicalDelayText(double startingY, Component compText, ParticipantBox first, ParticipantBox last) {
		super(startingY);
		this.compText = compText;
		this.p1 = first;
		this.p2 = last;
	}

	@Override
	protected void drawInternalU(UGraphic ug, double maxX, Context2D context) {
		final StringBounder stringBounder = ug.getStringBounder();
		final double x1 = p1.getCenterX(stringBounder);
		final double x2 = p2.getCenterX(stringBounder);
		final double middle = (x1 + x2) / 2;
		final double textWidth = compText.getPreferredWidth(stringBounder);
		ug = ug.apply(new UTranslate(middle - textWidth / 2, getStartingY()));
		// ug.translate(x1, getStartingY());
		final Dimension2D dim = new Dimension2DDouble(textWidth, compText.getPreferredHeight(stringBounder));
		compText.drawU(ug, new Area(dim), context);
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return compText.getPreferredHeight(stringBounder);
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return compText.getPreferredWidth(stringBounder);
	}

	@Override
	public double getStartingX(StringBounder stringBounder) {
		return 0;
	}

	public double getEndingY(StringBounder stringBounder) {
		return getStartingY() + compText.getPreferredHeight(stringBounder);
	}

}
