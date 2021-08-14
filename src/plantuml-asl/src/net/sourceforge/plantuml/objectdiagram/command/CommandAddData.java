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
package net.sourceforge.plantuml.objectdiagram.command;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.objectdiagram.AbstractClassOrObjectDiagram;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandAddData extends SingleLineCommand2<AbstractClassOrObjectDiagram> {

	public CommandAddData() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandAddData.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("NAME", "([%pLN_.]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("DATA", "(.*)"), RegexLeaf.end()); //
	}

	@Override
	protected CommandExecutionResult executeArg(AbstractClassOrObjectDiagram diagram, LineLocation location,
			RegexResult arg) throws NoSuchColorException {
		final String name = arg.get("NAME", 0);
		final IEntity entity = diagram.getOrCreateLeaf(diagram.buildLeafIdent(name),
				diagram.buildCode(name), null, null);

		final String field = arg.get("DATA", 0);
		if (field.length() > 0 && VisibilityModifier.isVisibilityCharacter(field)) {
			diagram.setVisibilityModifierPresent(true);
		}
		entity.getBodier().addFieldOrMethod(field);
		return CommandExecutionResult.ok();
	}
}
