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
package net.sourceforge.plantuml.graphic;

import java.awt.Font;
import java.util.EnumSet;

import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public enum FontStyle {
	PLAIN, ITALIC, BOLD, UNDERLINE, STRIKE, WAVE, BACKCOLOR;

	public UFont mutateFont(UFont font) {
		if (this == PLAIN) {
			return font.withStyle(Font.PLAIN);
		}
		if (this == ITALIC) {
			return font.withStyle(font.getStyle() | Font.ITALIC);
		}
		if (this == BOLD) {
			return font.withStyle(font.getStyle() | Font.BOLD);
		}
		return font;
	}

	public String getActivationPattern() {
		if (this == PLAIN) {
			return "\\<[pP][lL][aA][iI][nN]\\>";
		}
		if (this == ITALIC) {
			return "\\<[iI]\\>";
		}
		if (this == BOLD) {
			return "\\<[bB]\\>";
		}
		if (this == UNDERLINE) {
			return "\\<[uU](?::(#[0-9a-fA-F]{6}|\\w+))?\\>";
		}
		if (this == WAVE) {
			return "\\<[wW](?::(#[0-9a-fA-F]{6}|\\w+))?\\>";
		}
		if (this == BACKCOLOR) {
			// return "\\<[bB][aA][cC][kK](?::(#[0-9a-fA-F]{6}|\\w+))?\\>";
			return "\\<[bB][aA][cC][kK](?::(#?\\w+(?:[-\\\\|/]#?\\w+)?))?\\>";
		}
		if (this == STRIKE) {
			return "\\<(?:s|S|strike|STRIKE|del|DEL)(?::(#[0-9a-fA-F]{6}|\\w+))?\\>";
		}
		return null;
	}

	public boolean canHaveExtendedColor() {
		if (this == UNDERLINE) {
			return true;
		}
		if (this == WAVE) {
			return true;
		}
		if (this == BACKCOLOR) {
			return true;
		}
		if (this == STRIKE) {
			return true;
		}
		return false;
	}

	public String getCreoleSyntax() {
		if (this == ITALIC) {
			return "//";
		}
		if (this == BOLD) {
			return "\\*\\*";
		}
		if (this == UNDERLINE) {
			return "__";
		}
		if (this == WAVE) {
			return "~~";
		}
		if (this == STRIKE) {
			return "--";
		}
		throw new UnsupportedOperationException();
	}

	public HColor getExtendedColor(String s) {
		final Matcher2 m = MyPattern.cmpile(getActivationPattern()).matcher(s);
		if (m.find() == false || m.groupCount() != 1) {
			return null;
		}
		final String color = m.group(1);
		if (HColorSet.instance().getColorIfValid(color) != null) {
			return HColorSet.instance().getColorIfValid(color);
		}
		return null;
	}

	public String getDeactivationPattern() {
		if (this == PLAIN) {
			return "\\</[pP][lL][aA][iI][nN]\\>";
		}
		if (this == ITALIC) {
			return "\\</[iI]\\>";
		}
		if (this == BOLD) {
			return "\\</[bB]\\>";
		}
		if (this == UNDERLINE) {
			return "\\</[uU]\\>";
		}
		if (this == WAVE) {
			return "\\</[wW]\\>";
		}
		if (this == BACKCOLOR) {
			return "\\</[bB][aA][cC][kK]\\>";
		}
		if (this == STRIKE) {
			return "\\</(?:s|S|strike|STRIKE|del|DEL)\\>";
		}
		return null;
	}

	public static FontStyle getStyle(String line) {
		for (FontStyle style : EnumSet.allOf(FontStyle.class)) {
			if (line.matches(style.getActivationPattern()) || line.matches(style.getDeactivationPattern())) {
				return style;
			}
		}
		throw new IllegalArgumentException(line);
	}

}
