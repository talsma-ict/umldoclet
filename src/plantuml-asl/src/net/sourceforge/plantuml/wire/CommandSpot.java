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
package net.sourceforge.plantuml.wire;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandSpot extends SingleLineCommand2<WireDiagram> {

	public CommandSpot() {
		super(false, getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandSpot.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("spot"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("NAME", "([\\w][.\\w]*)"), //
				new RegexOptional(new RegexConcat(//
						new RegexLeaf("\\("), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("X", "(-?\\d+(%|%[-+]\\d+)?)"), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf(","), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("Y", "(-?\\d+(%|%[-+]\\d+)?)"), //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("\\)") //
				)), //
				new RegexOptional(new RegexConcat( //
						RegexLeaf.spaceZeroOrMore(), //
						new RegexLeaf("COLOR", "(#\\w+)?"))), //

				RegexLeaf.spaceZeroOrMore(), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(WireDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final String name = arg.get("NAME", 0);

		final String stringColor = arg.get("COLOR", 0);
		HColor color = null;
		if (stringColor != null) {
			color = HColorSet.instance().getColor(diagram.getSkinParam().getThemeStyle(), stringColor);
		}

		final String x = arg.get("X", 0);
		final String y = arg.get("Y", 0);

		return diagram.spot(name, color, x, y);
	}

}
