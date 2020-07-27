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

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;

public class CommandPage extends SingleLineCommand2<AbstractEntityDiagram> {

	public CommandPage() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandPage.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("page"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("NB1", "(\\d+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("x*"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("NB2", "(\\d+)"), RegexLeaf.end()); //
	}

	@Override
	protected CommandExecutionResult executeArg(AbstractEntityDiagram classDiagram, LineLocation location,
			RegexResult arg) {

		final int horizontal = Integer.parseInt(arg.get("NB1", 0));
		final int vertical = Integer.parseInt(arg.get("NB2", 0));
		if (horizontal <= 0 || vertical <= 0) {
			return CommandExecutionResult.error("Argument must be positive");
		}
		classDiagram.setHorizontalPages(horizontal);
		classDiagram.setVerticalPages(vertical);
		return CommandExecutionResult.ok();
	}

}
