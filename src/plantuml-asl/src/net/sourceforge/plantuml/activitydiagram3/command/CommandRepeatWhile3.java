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
package net.sourceforge.plantuml.activitydiagram3.command;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.descdiagram.command.CommandLinkElement;
import net.sourceforge.plantuml.graphic.Rainbow;

public class CommandRepeatWhile3 extends SingleLineCommand2<ActivityDiagram3> {

	public CommandRepeatWhile3() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandRepeatWhile3.class.getName(), //
				RegexLeaf.start(), //
				new RegexLeaf("repeat"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("while"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOr(//
						new RegexConcat(new RegexLeaf("TEST3", "\\((.*?)\\)"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("(is|equals?)"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("WHEN3", "\\((.+?)\\)"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("(not)"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("OUT3", "\\((.+?)\\)")), //
						new RegexConcat(new RegexLeaf("TEST4", "\\((.*?)\\)"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("(not)"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("OUT4", "\\((.+?)\\)")), //
						new RegexConcat(new RegexLeaf("TEST2", "\\((.*?)\\)"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("(is|equals?)"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("WHEN2", "\\((.+?)\\)") //
						), //
						new RegexOptional(new RegexLeaf("TEST1", "\\((.*)\\)")) //
				), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexConcat( //
						new RegexOr(//
								new RegexLeaf("->"), //
								new RegexLeaf("COLOR", CommandLinkElement.STYLE_COLORS_MULTIPLES)), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexOr(//
								new RegexLeaf("LABEL", "(.*)"), //
								new RegexLeaf("")) //
						)), //
				new RegexLeaf(";?"), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, LineLocation location, RegexResult arg) {
		final Display test = Display.getWithNewlines(arg.getLazzy("TEST", 0));
		final Display yes = Display.getWithNewlines(arg.getLazzy("WHEN", 0));
		final Display out = Display.getWithNewlines(arg.getLazzy("OUT", 0));

		final String colorString = arg.get("COLOR", 0);
		final Rainbow rainbow;
		if (colorString == null) {
			rainbow = Rainbow.none();
		} else {
			rainbow = Rainbow.build(diagram.getSkinParam(), colorString, diagram.getSkinParam()
					.colorArrowSeparationSpace());
		}

		final Display linkLabel = Display.getWithNewlines(arg.get("LABEL", 0));
		return diagram.repeatWhile(test, yes, out, linkLabel, rainbow);
	}

}
