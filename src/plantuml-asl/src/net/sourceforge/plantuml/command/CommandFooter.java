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
package net.sourceforge.plantuml.command;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;

public class CommandFooter extends SingleLineCommand2<TitledDiagram> {

	public CommandFooter() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandFooter.class.getName(), //
				RegexLeaf.start(), //
				new RegexOptional(new RegexLeaf("POSITION", "(left|right|center)")), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("footer"), //
				new RegexOr( //
						new RegexConcat(RegexLeaf.spaceZeroOrMore(), new RegexLeaf(":"), RegexLeaf.spaceZeroOrMore()), //
						RegexLeaf.spaceOneOrMore()), //
				new RegexOr(//
						new RegexLeaf("LABEL1", "[%g](.*)[%g]"), //
						new RegexLeaf("LABEL2", "(.*[\\p{L}0-9_.].*)")), //
				RegexLeaf.end()); //
	}

	@Override
	protected CommandExecutionResult executeArg(TitledDiagram diagram, LineLocation location, RegexResult arg) {
		final String align = arg.get("POSITION", 0);
		HorizontalAlignment ha = HorizontalAlignment.fromString(align, HorizontalAlignment.CENTER);
		if (UseStyle.useBetaStyle() && align == null) {
			ha = FontParam.FOOTER.getStyleDefinition(null).getMergedStyle(diagram.getCurrentStyleBuilder())
					.getHorizontalAlignment();
		}
		final Display s = Display.getWithNewlines(arg.getLazzy("LABEL", 0));
		diagram.getFooter().putDisplay(s, ha);

		return CommandExecutionResult.ok();
	}
}
