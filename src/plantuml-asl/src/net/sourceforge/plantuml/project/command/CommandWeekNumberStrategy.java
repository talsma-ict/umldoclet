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
package net.sourceforge.plantuml.project.command;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.time.DayOfWeek;

public class CommandWeekNumberStrategy extends SingleLineCommand2<GanttDiagram> {

	public CommandWeekNumberStrategy() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandWeekNumberStrategy.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("weeks?"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("starts?"), //
				new RegexLeaf("[^0-9]*?"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("WEEKDAY", "(" + DayOfWeek.getRegexString() + ")"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("[^0-9]*?"), //
				new RegexLeaf("NUM", "([0-9]+)"), //
				new RegexLeaf("[^0-9]*?"), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(GanttDiagram diagram, LineLocation location, RegexResult arg) {

		final DayOfWeek weekDay = DayOfWeek.fromString(arg.get("WEEKDAY", 0));
		final String num = arg.get("NUM", 0);
		diagram.setWeekNumberStrategy(weekDay, Integer.parseInt(num));
		return CommandExecutionResult.ok();
	}

}
