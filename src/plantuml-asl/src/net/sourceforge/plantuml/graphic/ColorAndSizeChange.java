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

import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

class ColorAndSizeChange implements FontChange {

	static final Pattern2 colorPattern = MyPattern.cmpile("color\\s*=\\s*[%g]?(#[0-9a-fA-F]{6}|\\w+)[%g]?");

	static final Pattern2 sizePattern = MyPattern.cmpile("size\\s*=\\s*[%g]?(\\d+)[%g]?");

	private final HColor color;
	private final Integer size;

	ColorAndSizeChange(String s) {
		final Matcher2 matcherColor = colorPattern.matcher(s);
		if (matcherColor.find()) {
			final String s1 = matcherColor.group(1);
			color = HColorSet.instance().getColorOrWhite(s1);
		} else {
			color = null;
		}
		final Matcher2 matcherSize = sizePattern.matcher(s);
		if (matcherSize.find()) {
			size = Integer.valueOf(matcherSize.group(1));
		} else {
			size = null;
		}
	}

	HColor getColor() {
		return color;
	}

	Integer getSize() {
		return size;
	}

	public FontConfiguration apply(FontConfiguration initial) {
		FontConfiguration result = initial;
		if (color != null) {
			result = result.changeColor(color);
		}
		if (size != null) {
			result = result.changeSize(size);
		}
		return result;
	}

}
