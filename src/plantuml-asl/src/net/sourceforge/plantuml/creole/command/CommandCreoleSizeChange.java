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
package net.sourceforge.plantuml.creole.command;

import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.creole.legacy.StripeSimple;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.Splitter;

public class CommandCreoleSizeChange implements Command {

	@Override
	public String startingChars() {
		return "<";
	}

	private final Pattern2 mypattern;

	private static final Pattern2 pattern = MyPattern
			.cmpile("^(" + Splitter.fontSizePattern2 + "(.*?)\\</size\\>)");
	private static final Pattern2 patternEol = MyPattern.cmpile("^(" + Splitter.fontSizePattern2 + "(.*)$)");

	public static Command create() {
		return new CommandCreoleSizeChange(pattern);
	}

	public static Command createEol() {
		return new CommandCreoleSizeChange(patternEol);
	}

	private CommandCreoleSizeChange(Pattern2 p) {
		this.mypattern = p;
	}

	public int matchingSize(String line) {
		final Matcher2 m = mypattern.matcher(line);
		if (m.find() == false) {
			return 0;
		}
		return m.group(2).length();
	}

	public String executeAndGetRemaining(String line, StripeSimple stripe) {
		final Matcher2 m = mypattern.matcher(line);
		if (m.find() == false) {
			throw new IllegalStateException();
		}
		final int size = Integer.parseInt(m.group(2));
		final FontConfiguration fc1 = stripe.getActualFontConfiguration();
		final FontConfiguration fc2 = fc1.changeSize(size);
		// final FontConfiguration fc2 = new AddStyle(style, null).apply(fc1);
		stripe.setActualFontConfiguration(fc2);
		stripe.analyzeAndAdd(m.group(3));
		stripe.setActualFontConfiguration(fc1);
		return line.substring(m.group(1).length());
	}

}
