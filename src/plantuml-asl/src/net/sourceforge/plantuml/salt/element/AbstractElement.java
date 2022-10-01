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

import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public abstract class AbstractElement implements Element {

	final protected HColor getBlack() {
		return HColors.BLACK.withDark(HColors.WHITE);
	}

	final protected HColor getColor88() {
		return buildColor("#8", "#8");
	}

	final protected HColor getColorAA() {
		return buildColor("#A", "#6");
	}

	final protected HColor getColorBB() {
		return buildColor("#B", "#5");
	}

	final protected HColor getColorDD() {
		return buildColor("#D", "#3");
	}

	final protected HColor getColorEE() {
		return buildColor("#E", "#2");
	}

	final protected HColor getWhite() {
		return HColors.WHITE.withDark(HColors.BLACK);
	}

	private HColor buildColor(String color1, String color2) {
		final HColor tmp1 = HColorSet.instance().getColorOrWhite(color1);
		final HColor tmp2 = HColorSet.instance().getColorOrWhite(color2);
		return tmp1.withDark(tmp2);
	}

	final protected FontConfiguration blackBlueTrue(UFont font) {
		return FontConfiguration.blackBlueTrue(font);
	}

}
