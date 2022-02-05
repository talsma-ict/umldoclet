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
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorRuntimeException;

public class CommandCreoleColorAndSizeChange implements Command {

	@Override
	public String startingChars() {
		return "<";
	}

	private final Pattern2 mypattern;

	public static final String fontPattern = "\\<font(?:[%s]+size[%s]*=[%s]*[%g]?(\\d+)[%g]?|[%s]+color[%s]*=[%s]*[%g]?(#[0-9a-fA-F]{6}|\\w+)[%g]?)+[%s]*\\>";

	private static final Pattern2 pattern = MyPattern.cmpile("^(" + fontPattern + "(.*?)\\</font\\>)");

	private static final Pattern2 patternEol = MyPattern.cmpile("^(" + fontPattern + "(.*))$");

	public static Command create() {
		return new CommandCreoleColorAndSizeChange(pattern);
	}

	public static Command createEol() {
		return new CommandCreoleColorAndSizeChange(patternEol);
	}

	private CommandCreoleColorAndSizeChange(Pattern2 pattern) {
		this.mypattern = pattern;
	}

	public int matchingSize(String line) {
		final Matcher2 m = mypattern.matcher(line);
		if (m.find() == false)
			return 0;

		return m.group(1).length();
	}

	public String executeAndGetRemaining(String line, StripeSimple stripe) throws NoSuchColorRuntimeException {
		final Matcher2 m = mypattern.matcher(line);
		if (m.find() == false)
			throw new IllegalStateException();

		final FontConfiguration fc1 = stripe.getActualFontConfiguration();
		FontConfiguration fc2 = fc1;
		if (m.group(2) != null)
			fc2 = fc2.changeSize(Integer.parseInt(m.group(2)));

		try {
			if (m.group(3) != null) {
				final String s = m.group(3);
				final HColor color = HColorSet.instance().getColor(stripe.getSkinParam().getThemeStyle(), s);
				fc2 = fc2.changeColor(color);
			}

			stripe.setActualFontConfiguration(fc2);
			stripe.analyzeAndAdd(m.group(4));
			stripe.setActualFontConfiguration(fc1);
			return line.substring(m.group(1).length());
		} catch (NoSuchColorException e) {
			throw new NoSuchColorRuntimeException();
		}
	}

}
