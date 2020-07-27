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
package net.sourceforge.plantuml.creole.command;

import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.creole.legacy.StripeSimple;
import net.sourceforge.plantuml.graphic.AddStyle;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.FontStyle;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class CommandCreoleStyle implements Command {

	private final Pattern2 p;
	private final FontStyle style;
	private final boolean tryExtendedColor;

	public static CommandCreoleStyle createCreole(FontStyle style) {
		return new CommandCreoleStyle("^(" + style.getCreoleSyntax() + "(.+?)" + style.getCreoleSyntax() + ")", style,
				false);
	}

	public static Command createLegacy(FontStyle style) {
		return new CommandCreoleStyle(
				"^((" + style.getActivationPattern() + ")(.+?)" + style.getDeactivationPattern() + ")", style,
				style.canHaveExtendedColor());
	}

	public static Command createLegacyEol(FontStyle style) {
		return new CommandCreoleStyle("^((" + style.getActivationPattern() + ")(.+))$", style,
				style.canHaveExtendedColor());
	}

	private CommandCreoleStyle(String p, FontStyle style, boolean tryExtendedColor) {
		this.p = MyPattern.cmpile(p);
		this.style = style;
		this.tryExtendedColor = tryExtendedColor;
	}

	private HColor getExtendedColor(Matcher2 m) {
		if (tryExtendedColor) {
			return style.getExtendedColor(m.group(2));
		}
		return null;
	}

	public String executeAndGetRemaining(final String line, StripeSimple stripe) {
		final Matcher2 m = p.matcher(line);
		if (m.find() == false) {
			throw new IllegalStateException();
		}
		final FontConfiguration fc1 = stripe.getActualFontConfiguration();
		final FontConfiguration fc2 = new AddStyle(style, getExtendedColor(m)).apply(fc1);
		stripe.setActualFontConfiguration(fc2);
		final int groupCount = m.groupCount();
		stripe.analyzeAndAdd(m.group(groupCount));
		stripe.setActualFontConfiguration(fc1);
		return line.substring(m.group(1).length());
	}

	public int matchingSize(String line) {
		final Matcher2 m = p.matcher(line);
		if (m.find() == false) {
			return 0;
		}
		return m.group(1).length();
	}

}
