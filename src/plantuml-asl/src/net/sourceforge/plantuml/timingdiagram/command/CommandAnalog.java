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
package net.sourceforge.plantuml.timingdiagram.command;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.timingdiagram.PlayerAnalog;
import net.sourceforge.plantuml.timingdiagram.TimingDiagram;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandAnalog extends SingleLineCommand2<TimingDiagram> {

	public CommandAnalog() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandAnalog.class.getName(), RegexLeaf.start(), //
				new RegexOptional( //
						new RegexConcat( //
								new RegexLeaf("COMPACT", "(compact)"), //
								RegexLeaf.spaceOneOrMore())), //
				new RegexLeaf("analog"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("FULL", "[%g]([^%g]+)[%g]"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREOTYPE", "(\\<\\<.*\\>\\>)?"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexOptional(//
						new RegexConcat( //
								new RegexLeaf("between"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("START", "(-?[0-9]*\\.?[0-9]+)"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("and"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("END", "(-?[0-9]*\\.?[0-9]+)"), //
								RegexLeaf.spaceOneOrMore())), //
				new RegexLeaf("as"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("CODE", "([%pLN_.@]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREOTYPE2", "(\\<\\<.*\\>\\>)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				RegexLeaf.end());
	}

	@Override
	final protected CommandExecutionResult executeArg(TimingDiagram diagram, LineLocation location, RegexResult arg) {
		final String compact = arg.get("COMPACT", 0);
		final String code = arg.get("CODE", 0);
		final String full = arg.get("FULL", 0);

		Stereotype stereotype = null;
		if (arg.get("STEREOTYPE", 0) != null)
			stereotype = Stereotype.build(arg.get("STEREOTYPE", 0));
		else if (arg.get("STEREOTYPE2", 0) != null)
			stereotype = Stereotype.build(arg.get("STEREOTYPE2", 0));

		final PlayerAnalog player = diagram.createAnalog(code, full, compact != null, stereotype);
		final String start = arg.get("START", 0);
		final String end = arg.get("END", 0);
		if (start != null && end != null) {
			player.setStartEnd(Double.parseDouble(start), Double.parseDouble(end));
		}
		return CommandExecutionResult.ok();
	}

}
