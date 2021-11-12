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
package net.sourceforge.plantuml.timingdiagram.command;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.timingdiagram.TimingDiagram;

public class CommandClock extends SingleLineCommand2<TimingDiagram> {

	public CommandClock() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandClock.class.getName(), RegexLeaf.start(), //
				new RegexOptional( //
						new RegexConcat( //
								new RegexLeaf("COMPACT", "(compact)"), //
								RegexLeaf.spaceOneOrMore())), //
				new RegexLeaf("TYPE", "clock"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexOptional(new RegexConcat( //
						new RegexLeaf("FULL", "[%g]([^%g]+)[%g]"), //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("as"), //
						RegexLeaf.spaceOneOrMore())), //
				new RegexLeaf("CODE", "([%pLN_.@]+)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("with"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("period"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("PERIOD", "([0-9]+)"), //
				new RegexOptional(new RegexConcat( //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("pulse"), //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("PULSE", "([0-9]+)") //
				)), //
				new RegexOptional(new RegexConcat( //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("offset"), //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("OFFSET", "([0-9]+)") //
				)), //
				RegexLeaf.end());
	}

	@Override
	final protected CommandExecutionResult executeArg(TimingDiagram diagram, LineLocation location, RegexResult arg) {
		final String compact = arg.get("COMPACT", 0);
		final String code = arg.get("CODE", 0);
		final int period = Integer.parseInt(arg.get("PERIOD", 0));
		final int pulse = getInt(arg.get("PULSE", 0));
		final int offset = getInt(arg.get("OFFSET", 0));
		String full = arg.get("FULL", 0);
		if (full == null)
			full = "";
		return diagram.createClock(code, full, period, pulse, offset, compact != null);
	}

	private int getInt(String value) {
		if (value == null)
			return 0;
		return Integer.parseInt(value);
	}

}
