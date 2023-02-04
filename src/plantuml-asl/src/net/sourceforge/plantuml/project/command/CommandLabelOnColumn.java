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

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.LabelPosition;
import net.sourceforge.plantuml.project.LabelStrategy;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandLabelOnColumn extends SingleLineCommand2<GanttDiagram> {

	public CommandLabelOnColumn() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandLabelOnColumn.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("labels?"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("on"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("POSITION", "(first|last)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("column"), //
				new RegexOptional(new RegexConcat( //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("and"), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("ALIGNED", "(left|right)"), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("aligned") //
				)), RegexLeaf.end()); //
	}

	@Override
	protected CommandExecutionResult executeArg(GanttDiagram diagram, LineLocation location, RegexResult arg) {
		final LabelPosition position = "first".equalsIgnoreCase(arg.get("POSITION", 0)) ? LabelPosition.FIRST_COLUMN
				: LabelPosition.LAST_COLUMN;
		final HorizontalAlignment alignment = "right".equalsIgnoreCase(arg.get("ALIGNED", 0))
				? HorizontalAlignment.RIGHT
				: HorizontalAlignment.LEFT;
		final LabelStrategy strategy = new LabelStrategy(position, alignment);
		diagram.setLabelStrategy(strategy);
		return CommandExecutionResult.ok();
	}

}
