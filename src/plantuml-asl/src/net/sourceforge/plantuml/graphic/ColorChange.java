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
package net.sourceforge.plantuml.graphic;

import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

class ColorChange implements FontChange {

	static private final Pattern2 colorPattern = MyPattern.cmpile(Splitter.fontColorPattern2);

	private final HColor color;

	ColorChange(ThemeStyle themeStyle, String s) {
		final Matcher2 matcherColor = colorPattern.matcher(s);
		if (matcherColor.find() == false) {
			throw new IllegalArgumentException();
		}
		final String s1 = matcherColor.group(1);
		this.color = HColorSet.instance().getColorOrWhite(themeStyle, s1);
	}

	HColor getColor() {
		return color;
	}

	public FontConfiguration apply(FontConfiguration initial) {
		return initial.changeColor(color);
	}

}
