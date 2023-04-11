/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.style.NoStyleAvailableException;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandSkinParam extends SingleLineCommand2<TitledDiagram> {

	public static final CommandSkinParam ME = new CommandSkinParam();

	private CommandSkinParam() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandSkinParam.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "(skinparam|skinparamlocked)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("NAME", "([\\w.]*(?:\\<\\<.*\\>\\>)?[\\w.]*)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("VALUE", "([^{}]*)"), RegexLeaf.end()); //
	}

	@Override
	protected CommandExecutionResult executeArg(TitledDiagram diagram, LineLocation location, RegexResult arg) {
		try {
			diagram.setParam(arg.get("NAME", 0), arg.get("VALUE", 0));
			return CommandExecutionResult.ok();
		} catch (NoStyleAvailableException e) {
			// Logme.error(e);
			return CommandExecutionResult.error("General failure: no style available.");
		}

	}

}
