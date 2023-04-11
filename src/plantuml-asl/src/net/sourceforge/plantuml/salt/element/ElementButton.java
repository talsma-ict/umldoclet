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
package net.sourceforge.plantuml.salt.element;

import net.sourceforge.plantuml.klimt.UStroke;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.font.UFont;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.URectangle;
import net.sourceforge.plantuml.style.ISkinSimple;

public class ElementButton extends AbstractElementText implements Element {

	private final double stroke = 2.5;
	private final double marginX = 2;
	private final double marginY = 2;

	public ElementButton(String text, UFont font, ISkinSimple spriteContainer) {
		super(text, font, true, spriteContainer);
	}

	public XDimension2D getPreferredDimension(StringBounder stringBounder, double x, double y) {
		XDimension2D dim = getTextDimensionAt(stringBounder, x + stroke + marginX);
		dim = dim.delta(2 * marginX, 2 * marginY);
		return dim.delta(2 * stroke);
	}

	public void drawU(UGraphic ug, int zIndex, XDimension2D dimToUse) {
		if (zIndex != 0)
			return;

		final XDimension2D dim = getPreferredDimension(ug.getStringBounder(), 0, 0);
		ug = ug.apply(UStroke.withThickness(stroke));
		ug = ug.apply(getColorEE().bg()).apply(getBlack());
		ug.apply(new UTranslate(stroke, stroke))
				.draw(URectangle.build(dim.getWidth() - 2 * stroke, dim.getHeight() - 2 * stroke).rounded(10));
		final XDimension2D dimPureText = getPureTextDimension(ug.getStringBounder());
		drawText(ug, (dim.getWidth() - dimPureText.getWidth()) / 2, stroke + marginY);
	}
}
