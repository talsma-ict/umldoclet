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
package net.sourceforge.plantuml.classdiagram.command;

import net.sourceforge.plantuml.baraye.CucaDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandHideShowSpecificClass extends SingleLineCommand2<CucaDiagram> {

	public CommandHideShowSpecificClass() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandHideShowSpecificClass.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("COMMAND", "(hide|show)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("CODE", "(" + CommandCreateClass.CODE + ")"), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(CucaDiagram diagram, LineLocation location, RegexResult arg) {

		// final String codeString = arg.get("CODE", 0);
		// if (codeString.equals("class")) {
		// diagram.hideOrShow(LeafType.CLASS, arg.get("COMMAND",
		// 0).equalsIgnoreCase("show"));
		// } else if (codeString.equals("interface")) {
		// diagram.hideOrShow(LeafType.INTERFACE, arg.get("COMMAND",
		// 0).equalsIgnoreCase("show"));
		// } else {
		// final Code code = diagram.buildCode(codeString);
		// IEntity hidden = diagram.getEntityFactory().getLeafsget(code);
		// if (hidden == null) {
		// hidden = diagram.getEntityFactory().getGroupsget(code);
		// }
		// if (hidden == null) {
		// return CommandExecutionResult.error("Class/Package does not exist : " +
		// code.getFullName());
		// }
		// diagram.hideOrShow(hidden, arg.get("COMMAND", 0).equalsIgnoreCase("show"));
		// }
		// return CommandExecutionResult.ok();
		throw new UnsupportedOperationException();
	}
}
