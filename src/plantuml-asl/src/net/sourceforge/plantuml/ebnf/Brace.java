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
package net.sourceforge.plantuml.ebnf;

import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Brace implements UDrawable {

	private final double width;
	private final double height;

	public Brace(double width, double height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void drawU(UGraphic ug) {
		ug = ug.apply(new UStroke(0.5));

		final double cinq = 5;
		CornerCurved.createNW(cinq).drawU(ug);
		CornerCurved.createSE(cinq).drawU(ug.apply(new UTranslate(width / 2, 0)));
		CornerCurved.createSW(cinq).drawU(ug.apply(new UTranslate(width / 2, 0)));
		CornerCurved.createNE(cinq).drawU(ug.apply(new UTranslate(width, 0)));

		ug.apply(new UTranslate(cinq, 0)).draw(new ULine(width / 2 - 2 * cinq, 0));
		ug.apply(new UTranslate(cinq + width / 2, 0)).draw(new ULine(width / 2 - 2 * cinq, 0));
//		ug.apply(new UTranslate(width / 2, -height)).draw(new ULine(width / 2, height));

	}

}
