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
package net.sourceforge.plantuml.statediagram.command;

import net.sourceforge.plantuml.baraye.CucaDiagram;
import net.sourceforge.plantuml.baraye.IEntity;
import net.sourceforge.plantuml.baraye.Quark;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.statediagram.StateDiagram;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandAddField extends SingleLineCommand2<StateDiagram> {

	public CommandAddField() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandAddField.class.getName(), RegexLeaf.start(), //
				new RegexOr( //
						new RegexLeaf("CODE3", "([%pLN_.]+)"), //
						new RegexLeaf("CODE4", "[%g]([^%g]+)[%g]")), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("FIELD", "(.*)"), RegexLeaf.end());
	}

	private CommandExecutionResult executeArgQuark(StateDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final String codeString = arg.getLazzy("CODE", 0);

		final Quark quark = diagram.currentQuark();
		Quark child = quark.childIfExists(codeString);
		if (child == null && quark.getName().equals(codeString))
			child = quark;
		if (child == null)
			child = quark.child(codeString);

		final IEntity entity = diagram.getOrCreateLeaf(child, child, null, null);

		final String field = arg.get("FIELD", 0);

		entity.getBodier().addFieldOrMethod(field);
		return CommandExecutionResult.ok();
	}

	@Override
	protected CommandExecutionResult executeArg(StateDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		if (CucaDiagram.QUARK)
			return executeArgQuark(diagram, location, arg);

		final String codeString = arg.getLazzy("CODE", 0);
		final String field = arg.get("FIELD", 0);

		Ident ident = diagram.buildLeafIdent(codeString);
		final Code code = diagram.buildCode(codeString);
		final IEntity entity = diagram.getOrCreateLeaf(ident, code, null, null);

		entity.getBodier().addFieldOrMethod(field);
		return CommandExecutionResult.ok();
	}

}
