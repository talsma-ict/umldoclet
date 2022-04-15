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

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ElementLine extends AbstractElement {

	private final char separator;

	public ElementLine(char separator) {
		this.separator = separator;
	}

	public Dimension2D getPreferredDimension(StringBounder stringBounder, double x, double y) {
		return new Dimension2DDouble(10, 6);
	}

	public void drawU(UGraphic ug, int zIndex, Dimension2D dimToUse) {
		if (zIndex != 0) {
			return;
		}
		ug = ug.apply(getColorAA());
		double y2 = dimToUse.getHeight() / 2;
		if (separator == '=') {
			y2 = y2 - 1;
		}
		drawLine(ug, 0, y2, dimToUse.getWidth(), separator);
	}

	private static void drawLine(UGraphic ug, double x, double y, double widthToUse, char separator) {
		if (separator == '=') {
			ug.apply(new UStroke()).apply(new UTranslate(x, y)).draw(ULine.hline(widthToUse));
			ug.apply(new UStroke()).apply(new UTranslate(x, y + 2)).draw(ULine.hline(widthToUse));
		} else if (separator == '.') {
			ug.apply(new UStroke(1, 2, 1)).apply(new UTranslate(x, y)).draw(ULine.hline(widthToUse));
		} else if (separator == '-') {
			ug.apply(new UStroke()).apply(new UTranslate(x, y)).draw(ULine.hline(widthToUse));
		} else {
			ug.apply(new UStroke(1.5)).apply(new UTranslate(x, y)).draw(ULine.hline(widthToUse));
		}
	}

}
