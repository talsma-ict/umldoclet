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
import net.sourceforge.plantuml.project.core.Task;
import net.sourceforge.plantuml.project.lang.CenterBorderColor;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandColorTask extends SingleLineCommand2<GanttDiagram> {

	public CommandColorTask() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandColorTask.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("CODE", "\\[([%pLN_.]+)\\]"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("COLORS", "#(\\w+)(?:/(#?\\w+))?"), //
				RegexLeaf.spaceZeroOrMore(), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(GanttDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {

		final String code = arg.get("CODE", 0);
		final Task task = diagram.getExistingTask(code);
		if (task == null) {
			return CommandExecutionResult.error("No such task " + code);
		}

		final String color1 = arg.get("COLORS", 0);
		final String color2 = arg.get("COLORS", 1);
		final HColor col1 = color1 == null ? null
				: diagram.getIHtmlColorSet().getColor(diagram.getSkinParam().getThemeStyle(), color1);
		final HColor col2 = color2 == null ? null
				: diagram.getIHtmlColorSet().getColor(diagram.getSkinParam().getThemeStyle(), color2);
		task.setColors(new CenterBorderColor(col1, col2));

		return CommandExecutionResult.ok();
	}

}
