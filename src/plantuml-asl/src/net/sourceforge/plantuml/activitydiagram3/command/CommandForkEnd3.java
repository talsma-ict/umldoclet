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
package net.sourceforge.plantuml.activitydiagram3.command;

import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.activitydiagram3.ForkStyle;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandForkEnd3 extends SingleLineCommand2<ActivityDiagram3> {

	public CommandForkEnd3() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandForkEnd3.class.getName(), //
				RegexLeaf.start(), //
				new RegexOr("STYLE", //
						new RegexConcat( //
								new RegexLeaf("end"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("fork") //
						), //
						new RegexConcat( //
								new RegexLeaf("fork"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("end") //
						), //
						new RegexConcat( //
								new RegexLeaf("end"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("merge") //
						) //
				), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("LABEL", "(\\{.+\\})?"), //
				new RegexLeaf(";?"), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, LineLocation location, RegexResult arg) {
		final String style = arg.get("STYLE", 0);
		final ForkStyle forkStyle = style.contains("merge") ? ForkStyle.MERGE : ForkStyle.FORK;
		final String label = arg.get("LABEL", 0);
		return diagram.endFork(forkStyle, label);
	}

}
