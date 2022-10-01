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
package net.sourceforge.plantuml.salt.element;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ElementTextField extends AbstractElementText implements Element {

	public ElementTextField(String text, UFont font, ISkinSimple spriteContainer) {
		super(text, font, true, spriteContainer);
	}

	public XDimension2D getPreferredDimension(StringBounder stringBounder, double x, double y) {
		final XDimension2D dim = getTextDimensionAt(stringBounder, x);
		return XDimension2D.delta(dim, 6, 2);
	}

	public void drawU(UGraphic ug, int zIndex, XDimension2D dimToUse) {
		if (zIndex != 0) {
			return;
		}
		drawText(ug, 3, 0);
		final XDimension2D dim = getPreferredDimension(ug.getStringBounder(), 0, 0);
		final XDimension2D textDim = getTextDimensionAt(ug.getStringBounder(), 0);
		ug.apply(new UTranslate(1, textDim.getHeight())).draw(ULine.hline(dim.getWidth() - 3));
		final double y3 = textDim.getHeight() - 3;
		ug.apply(new UTranslate(1, y3)).draw(ULine.vline(2));
		ug.apply(new UTranslate(3 + textDim.getWidth() + 1, y3)).draw(ULine.vline(2));
	}

}
