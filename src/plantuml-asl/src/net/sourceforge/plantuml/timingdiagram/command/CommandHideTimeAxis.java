/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.timingdiagram.TimeAxisStategy;
import net.sourceforge.plantuml.timingdiagram.TimingDiagram;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandHideTimeAxis extends SingleLineCommand2<TimingDiagram> {

	public CommandHideTimeAxis() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandHideTimeAxis.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("COMMAND", "(hide|manual)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("time"), //
				new RegexLeaf(".?"), //
				new RegexLeaf("axis"), //
				RegexLeaf.end());
	}

	@Override
	final protected CommandExecutionResult executeArg(TimingDiagram diagram, LineLocation location, RegexResult arg) {
		final String cmd = arg.get("COMMAND", 0);
		if ("MANUAL".equalsIgnoreCase(cmd))
			return diagram.setTimeAxisStategy(TimeAxisStategy.MANUAL);
		return diagram.setTimeAxisStategy(TimeAxisStategy.HIDDEN);
	}

}
