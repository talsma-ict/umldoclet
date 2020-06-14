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
package net.sourceforge.plantuml.project3;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;

public class CommandGanttArrow2 extends SingleLineCommand2<GanttDiagram> {

	public CommandGanttArrow2() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandGanttArrow2.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("\\["), //
				new RegexLeaf("TASK1", "([^\\[\\]]+?)"), //
				new RegexLeaf("\\]"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("ARROW", "(-+)"), //
				new RegexLeaf("\\>"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\["), //
				new RegexLeaf("TASK2", "([^\\[\\]]+?)"), //
				new RegexLeaf("\\]"), //
				RegexLeaf.spaceZeroOrMore(), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(GanttDiagram diagram, LineLocation location, RegexResult arg) {

		final String name1 = arg.get("TASK1", 0);
		final String name2 = arg.get("TASK2", 0);
		final Task task1 = diagram.getOrCreateTask(name1, null, false);
		final Task task2 = diagram.getOrCreateTask(name2, null, false);

		diagram.setTaskOrder(task1, task2);

		return CommandExecutionResult.ok();
	}

}
