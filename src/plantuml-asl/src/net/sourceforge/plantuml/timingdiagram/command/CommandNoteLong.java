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
package net.sourceforge.plantuml.timingdiagram.command;

import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.timingdiagram.Player;
import net.sourceforge.plantuml.timingdiagram.TimeTick;
import net.sourceforge.plantuml.timingdiagram.TimingDiagram;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandNoteLong extends CommandMultilines2<TimingDiagram> {

	public CommandNoteLong() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE);
	}

	@Override
	public String getPatternEnd() {
		return "(?i)^end[%s]?note$";
	}

	@Override
	protected CommandExecutionResult executeNow(final TimingDiagram diagram, BlocLines lines) throws NoSuchColorException {

		final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
		lines = lines.subExtract(1, 1);
		lines = lines.removeEmptyColumns();
		final String code = line0.get("CODE", 0);
		final Player player = diagram.getPlayer(code);
		if (player == null) {
			return CommandExecutionResult.error("Unkown \"" + code + "\"");
		}
		final Display note = lines.toDisplay();
		final TimeTick now = diagram.getNow();
		// final Colors colors = color().getColor(arg, diagram.getSkinParam().getIHtmlColorSet());
		player.addNote(now, note, Position.fromString(line0.get("POSITION", 0)));
		return CommandExecutionResult.ok();
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandNoteLong.class.getName(), RegexLeaf.start(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("note"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("POSITION", "(top|bottom)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("of"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("CODE", CommandTimeMessage.PLAYER_CODE), //
				RegexLeaf.spaceZeroOrMore(), RegexLeaf.end());
	}

}
