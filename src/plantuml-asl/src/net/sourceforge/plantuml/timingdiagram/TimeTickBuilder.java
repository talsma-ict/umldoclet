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
package net.sourceforge.plantuml.timingdiagram;

import java.math.BigDecimal;

import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;

public class TimeTickBuilder {

	public static IRegex expressionAtWithoutArobase(String name) {
		return new RegexOr( //
				new RegexLeaf(name + "DIGIT", "(\\+?)(-?\\d+\\.?\\d*)"), //
				new RegexLeaf(name + "CLOCK", "([\\p{L}0-9_.@]+)\\*(\\d+)"));
	}

	public static IRegex expressionAtWithArobase(String name) {
		return new RegexConcat( //
				new RegexLeaf("@"), //
				expressionAtWithoutArobase(name));
	}

	public static IRegex optionalExpressionAtWithArobase(String name) {
		return new RegexOptional(expressionAtWithArobase(name));
	}

	public static TimeTick parseTimeTick(String name, RegexResult arg, Clocks clock) {
		final String clockName = arg.get(name + "CLOCK", 0);
		if (clockName != null) {
			final int number = Integer.parseInt(arg.get(name + "CLOCK", 1));
			return clock.getClockValue(clockName, number);
		}
		final String number = arg.get(name + "DIGIT", 1);
		if (number == null) {
			return clock.getNow();
		}
		final boolean isRelative = "+".equals(arg.get(name + "DIGIT", 0));
		BigDecimal value = new BigDecimal(number);
		if (isRelative) {
			value = clock.getNow().getTime().add(value);
		}
		return new TimeTick(value);
	}
}
