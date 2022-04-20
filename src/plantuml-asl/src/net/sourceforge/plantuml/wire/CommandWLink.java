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
package net.sourceforge.plantuml.wire;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandWLink extends SingleLineCommand2<WireDiagram> {

	public CommandWLink() {
		super(false, getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandWLink.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("NAME1", "([\\w][.\\w]*)"), //
				new RegexOptional(new RegexConcat(//
						new RegexLeaf("\\("), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("X1", "(-?\\d+(%|%[-+]\\d+)?)"), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf(","), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("Y1", "(-?\\d+(%|%[-+]\\d+)?)"), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("\\)") //
				)), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STYLE", "(\\<?[-=]{1,2}\\>|\\<?[-=]{1,2})"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("NAME2", "([\\w][.\\w]*)"), //
				new RegexOptional(new RegexConcat( //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("COLOR", "(#\\w+)?"))), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("MESSAGE", "(?::[%s]*(.*))?"), //
				RegexLeaf.spaceZeroOrMore(), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(WireDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {

		final String name1 = arg.get("NAME1", 0);
		final String x1 = arg.get("X1", 0);
		final String y1 = arg.get("Y1", 0);

		final String name2 = arg.get("NAME2", 0);
		final String style = arg.get("STYLE", 0);
		final WLinkType type = WLinkType.from(style);
		final WArrowDirection direction = WArrowDirection.from(style);
		final WOrientation orientation = WOrientation.from(style);

		final String stringColor = arg.get("COLOR", 0);
		HColor color = null;
		if (stringColor != null)
			color = HColorSet.instance().getColor(diagram.getSkinParam().getThemeStyle(), stringColor);

		final Display label;
		if (arg.get("MESSAGE", 0) == null) {
			label = Display.NULL;
		} else {
			final String message = arg.get("MESSAGE", 0);
			label = Display.getWithNewlines(message);
		}

		if (orientation == WOrientation.VERTICAL)
			return diagram.vlink(name1, x1, y1, name2, type, direction, color, label);

		return diagram.hlink(name1, x1, y1, name2, type, direction, color, label);
	}

}
