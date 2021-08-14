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
package net.sourceforge.plantuml.activitydiagram3.command;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
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
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandElseIf2 extends SingleLineCommand2<ActivityDiagram3> {

	public CommandElseIf2() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandElseIf2.class.getName(), RegexLeaf.start(), //
				ColorParser.exp4(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexConcat( //
						new RegexLeaf("\\("), //
						new RegexOptional(new RegexOr(//
								new RegexLeaf("->"), //
								new RegexLeaf("INCOMING_COLOR", CommandLinkElement.STYLE_COLORS_MULTIPLES))), //
						new RegexLeaf("INCOMING", "(.*?)"), //
						new RegexLeaf("\\)"))), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("else"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("if"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\("), //
				new RegexLeaf("TEST", "(.*?)"), //
				new RegexLeaf("\\)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional( //
						new RegexConcat( //
								new RegexLeaf("then"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexOptional(new RegexConcat( //
										new RegexLeaf("\\("), //
										new RegexOptional(new RegexOr(//
												new RegexLeaf("->"), //
												new RegexLeaf("WHEN_COLOR",
														CommandLinkElement.STYLE_COLORS_MULTIPLES))), //
										new RegexLeaf("WHEN", "(.*?)"), //
										new RegexLeaf("\\)"))) //
						)), //
				new RegexLeaf(";?"), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final String s = arg.get("COLOR", 0);
		final HColor color = s == null ? null
				: diagram.getSkinParam().getIHtmlColorSet().getColor(diagram.getSkinParam().getThemeStyle(), s);

		String test = arg.get("TEST", 0);
		if (test.length() == 0) {
			test = null;
		}

		final LinkRendering incoming = CommandBackward3.getBackRendering(diagram, arg, "INCOMING");
		final LinkRendering when = CommandBackward3.getBackRendering(diagram, arg, "WHEN");

		return diagram.elseIf(incoming, Display.getWithNewlines(test), when, color);
	}

}
