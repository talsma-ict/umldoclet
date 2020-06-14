/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.creole;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.graphic.Splitter;

public class CommandCreoleImg implements Command {

	private final Pattern2 pattern;

	private CommandCreoleImg(String p) {
		this.pattern = MyPattern.cmpile(p);
	}

	public static Command create() {
		return new CommandCreoleImg("^(?i)(" + Splitter.imgPatternNoSrcColon + ")");
	}

	public int matchingSize(String line) {
		final Matcher2 m = pattern.matcher(line);
		if (m.find() == false) {
			return 0;
		}
		return m.group(1).length();
	}

	public String executeAndGetRemaining(String line, StripeSimple stripe) {
		final Matcher2 m = pattern.matcher(line);
		if (m.find() == false) {
			throw new IllegalStateException();
		}
		String src = m.group(2);
		final double scale = getScale(m.group(3), 1);
		if (src.toLowerCase().startsWith("src=")) {
			src = src.substring(4);
		}
		src = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(src, "\"");
		stripe.addImage(src, scale);
		return line.substring(m.group(1).length());
	}

	public static double getScale(String s, double def) {
		if (s == null) {
			return def;
		}
		final Pattern p = Pattern.compile("(?:scale=|\\*)([0-9.]+)");
		final Matcher m = p.matcher(s);
		if (m.find()) {
			return Double.parseDouble(m.group(1));
		}
		return def;
	}

	public static String getColor(String s) {
		if (s == null) {
			return null;
		}
		final Pattern p = Pattern.compile("color[= :](#[0-9a-fA-F]{6}|\\w+)");
		final Matcher m = p.matcher(s);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

}
