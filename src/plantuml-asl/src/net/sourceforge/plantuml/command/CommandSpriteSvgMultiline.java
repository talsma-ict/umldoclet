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
package net.sourceforge.plantuml.command;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.emoji.SvgNanoParser;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandSpriteSvgMultiline extends CommandMultilines2<TitledDiagram> {

	public CommandSpriteSvgMultiline() {
		super(getRegexConcat(), MultilinesStrategy.KEEP_STARTING_QUOTE, Trim.BOTH);
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandSpriteSvgMultiline.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("sprite"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("\\$?"), //
				new RegexLeaf("NAME", "([-%pLN_]+)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("SVGSTART", "(\\<svg\\b.*)"), //
				RegexLeaf.end());
	}

	@Override
	public String getPatternEnd() {
		return "(.*\\</svg\\>)$";
	}

	@Override
	protected CommandExecutionResult executeNow(TitledDiagram system, BlocLines lines) throws NoSuchColorException {

		final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
		final String svgStart = line0.get("SVGSTART", 0);
		lines = lines.subExtract(1, 0);

		final StringBuilder svg = new StringBuilder(svgStart);
		for (StringLocated sl : lines)
			svg.append(sl.getString());

		final SvgNanoParser nanoParser = new SvgNanoParser(svg.toString(), true);
		system.addSprite(line0.get("NAME", 0), nanoParser);

		return CommandExecutionResult.ok();
	}
}
