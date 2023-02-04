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
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.project.GanttDiagram;
import net.sourceforge.plantuml.project.core.PrintScale;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandPrintScale extends SingleLineCommand2<GanttDiagram> {

	public CommandPrintScale() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandPrintScale.class.getName(), RegexLeaf.start(), //
				new RegexOr(new RegexLeaf("projectscale"), //
						new RegexLeaf("ganttscale"), //
						new RegexLeaf("printscale")), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexOr("SCALE", //
						new RegexLeaf("yearly"), //
						new RegexLeaf("quarterly"), //
						new RegexLeaf("monthly"), //
						new RegexLeaf("daily"), //
						new RegexLeaf("weekly")), //
				new RegexOptional(new RegexConcat( //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("DATE", "(with)"), //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("calendar"), //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("date"))), //
				new RegexOptional(new RegexConcat( //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("zoom"), //
						RegexLeaf.spaceOneOrMore(), //
						new RegexLeaf("ZOOM", "([.\\d]+)"))), //
				RegexLeaf.end()); //
	}

	@Override
	protected CommandExecutionResult executeArg(GanttDiagram diagram, LineLocation location, RegexResult arg) {
		final String scaleString = arg.get("SCALE", 0);
		final PrintScale scale = PrintScale.fromString(scaleString);
		diagram.setPrintScale(scale);

		final String zoom = arg.get("ZOOM", 0);
		if (zoom != null)
			diagram.setFactorScale(Double.parseDouble(zoom));

		final String withCalendarDate = arg.get("DATE", 0);
		if (withCalendarDate != null)
			diagram.setWithCalendarDate(true);

		return CommandExecutionResult.ok();
	}

}
