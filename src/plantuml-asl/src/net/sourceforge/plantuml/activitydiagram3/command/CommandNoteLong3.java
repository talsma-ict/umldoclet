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
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Trim;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteType;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandNoteLong3 extends CommandMultilines2<ActivityDiagram3> {

	public CommandNoteLong3() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE, Trim.BOTH);
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	@Override
	public String getPatternEnd() {
		return "^end[%s]?note$";
	}

	@Override
	protected CommandExecutionResult executeNow(final ActivityDiagram3 diagram, BlocLines lines)
			throws NoSuchColorException {
		// final List<? extends CharSequence> in =
		// StringUtils.removeEmptyColumns2(lines.subList(1, lines.size() - 1));
		final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
		lines = lines.subExtract(1, 1);
		lines = lines.removeEmptyColumns();
		final NotePosition position = NotePosition.defaultLeft(line0.get("POSITION", 0));
		final NoteType type = NoteType.defaultType(line0.get("TYPE", 0));
		final Display note = lines.toDisplay();
		final Colors colors = color().getColor(line0, diagram.getSkinParam().getIHtmlColorSet());
		return diagram.addNote(note, position, type, colors);
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandNoteLong3.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "(note|floating note)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("POSITION", "(left|right)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.end());
	}
}
