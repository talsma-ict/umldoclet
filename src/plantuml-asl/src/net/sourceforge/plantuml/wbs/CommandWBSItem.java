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

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.mindmap.IdeaShape;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.utils.Direction;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandWBSItem extends SingleLineCommand2<WBSDiagram> {

	public CommandWBSItem(int mode) {
		super(false, getRegexConcat(mode));
	}

	static IRegex getRegexConcat(int mode) {
		if (mode == 0)
			return RegexConcat.build(CommandWBSItem.class.getName() + mode, RegexLeaf.start(), //
					new RegexLeaf("TYPE", "([ \t]*[*+-]+)"), //
					new RegexOptional(new RegexLeaf("BACKCOLOR", "\\[(#\\w+)\\]")), //
					new RegexOptional(new RegexLeaf("CODE", "\\(([%pLN_]+)\\)")), //
					new RegexLeaf("SHAPE", "(_)?"), //
					new RegexLeaf("DIRECTION", "([<>])?"), //
					RegexLeaf.spaceOneOrMore(), //
					new RegexLeaf("LABEL", "([^%s].*)"), //
					RegexLeaf.end());

		return RegexConcat.build(CommandWBSItem.class.getName() + mode, RegexLeaf.start(), //
				new RegexLeaf("TYPE", "([ \t]*[*+-]+)"), //
				new RegexOptional(new RegexLeaf("BACKCOLOR", "\\[(#\\w+)\\]")), //
				new RegexLeaf("SHAPE", "(_)?"), //
				new RegexLeaf("DIRECTION", "([<>])?"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("LABEL", "[%g](.*)[%g]"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("as"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("CODE", "([%pLN_]+)"), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(WBSDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final String type = arg.get("TYPE", 0);
		final String label = arg.get("LABEL", 0);
		final String code = arg.get("CODE", 0);
		final String stringColor = arg.get("BACKCOLOR", 0);
		HColor backColor = null;
		if (stringColor != null)
			backColor = diagram.getSkinParam().getIHtmlColorSet().getColor(stringColor);

		Direction dir = type.contains("-") ? Direction.LEFT : Direction.RIGHT;
		final String direction = arg.get("DIRECTION", 0);
		if ("<".equals(direction))
			dir = Direction.LEFT;
		else if (">".equals(direction))
			dir = Direction.RIGHT;

		return diagram.addIdea(code, backColor, diagram.getSmartLevel(type), label, dir,
				IdeaShape.fromDesc(arg.get("SHAPE", 0)));
	}

}
