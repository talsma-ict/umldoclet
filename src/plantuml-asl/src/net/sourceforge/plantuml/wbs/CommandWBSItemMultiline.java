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
package net.sourceforge.plantuml.wbs;

import java.util.List;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Trim;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.mindmap.IdeaShape;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandWBSItemMultiline extends CommandMultilines2<WBSDiagram> {

	public CommandWBSItemMultiline() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE, Trim.BOTH);
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandWBSItemMultiline.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "([ \t]*[*+-]+)"), //
				new RegexOptional(new RegexLeaf("BACKCOLOR", "\\[(#\\w+)\\]")), //
				new RegexLeaf("SHAPE", "(_)?"), //
				new RegexLeaf(":"), //
				new RegexLeaf("DATA", "(.*)"), //
				RegexLeaf.end());
	}

	@Override
	public String getPatternEnd() {
		return "^(.*);(?:\\s*\\<\\<(.+)\\>\\>)?$";
	}

	static IRegex getRegexConcatOld() {
		return RegexConcat.build(CommandWBSItemMultiline.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "([ \t]*[*+-]+)"), //
				new RegexOptional(new RegexLeaf("BACKCOLOR", "\\[(#\\w+)\\]")), //
				new RegexLeaf("SHAPE", "(_)?"), //
				new RegexLeaf("DIRECTION", "([<>])?"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("LABEL", "([^%s].*)"), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeNow(WBSDiagram diagram, BlocLines lines) throws NoSuchColorException {
		final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());

		final List<String> lineLast = StringUtils.getSplit(MyPattern.cmpile(getPatternEnd()),
				lines.getLast().getString());
		lines = lines.removeStartingAndEnding(line0.get("DATA", 0), 1);

		final String stereotype = lineLast.get(1);
		if (stereotype != null)
			lines = lines.overrideLastLine(lineLast.get(0));

		final String type = line0.get("TYPE", 0);
		final String stringColor = line0.get("BACKCOLOR", 0);
		HColor backColor = null;
		if (stringColor != null)
			backColor = diagram.getSkinParam().getIHtmlColorSet().getColor(stringColor);

		Direction dir = Direction.RIGHT;

		return diagram.addIdea(null, backColor, diagram.getSmartLevel(type), lines.toDisplay(), stereotype, dir,
				IdeaShape.fromDesc(line0.get("SHAPE", 0)));

	}

}
