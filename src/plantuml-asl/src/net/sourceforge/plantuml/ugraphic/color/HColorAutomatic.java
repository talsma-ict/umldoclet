/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
package net.sourceforge.plantuml.ugraphic.color;

import net.sourceforge.plantuml.ThemeStyle;

public class HColorAutomatic extends HColorAbstract implements HColor {

	private final HColor colorForLight;
	private final HColor colorForDark;
	private final HColor colorForTransparent;
	private final ThemeStyle themeStyle;

	public HColorAutomatic(ThemeStyle themeStyle, HColor colorForLight, HColor colorForDark,
			HColor colorForTransparent) {
		this.themeStyle = themeStyle;
		this.colorForLight = colorForLight;
		this.colorForDark = colorForDark;
		this.colorForTransparent = colorForTransparent;
	}

	public HColor getAppropriateColor(HColor back) {
		if (back == null || HColorUtils.isTransparent(back)) {
			if (colorForTransparent != null) {
				return colorForTransparent;
			}
			return themeStyle == ThemeStyle.LIGHT ? colorForLight : colorForDark;
		}
		if (back.isDark()) {
			return colorForDark;
		}
		return colorForLight;
	}

}
