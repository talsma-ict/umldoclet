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

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.legacy.StripeSimple;
import net.sourceforge.plantuml.graphic.Splitter;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class CommandCreoleOpenIcon implements Command {

	@Override
	public String startingChars() {
		return "<";
	}

	private static final Pattern2 pattern = MyPattern.cmpile("^(" + Splitter.openiconPattern + ")");

	private CommandCreoleOpenIcon() {
	}

	public static Command create() {
		return new CommandCreoleOpenIcon();
	}

	public int matchingSize(String line) {
		final Matcher2 m = pattern.matcher(line);
		if (m.find() == false)
			return 0;

		return m.group(1).length();
	}

	public String executeAndGetRemaining(String line, StripeSimple stripe) {
		final Matcher2 m = pattern.matcher(line);
		if (m.find() == false)
			throw new IllegalStateException();

		final String src = m.group(2);
		final double scale = Parser.getScale(m.group(3), 1);
		final String colorName = Parser.getColor(m.group(3));
		HColor color = null;
		if (colorName != null) {
			final ISkinSimple skinParam = stripe.getSkinParam();
			color = skinParam.getIHtmlColorSet().getColorOrWhite(skinParam.getThemeStyle(), colorName);
		}
		stripe.addOpenIcon(src, scale, color);
		return line.substring(m.group(1).length());
	}

}
