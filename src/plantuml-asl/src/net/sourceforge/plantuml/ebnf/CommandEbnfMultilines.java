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
package net.sourceforge.plantuml.ebnf;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Trim;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.utils.BlocLines;

public class CommandEbnfMultilines extends CommandMultilines2<PSystemEbnf> {

	public CommandEbnfMultilines() {
		super(getRegexConcat(), MultilinesStrategy.KEEP_STARTING_QUOTE, Trim.BOTH);
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandEbnfMultilines.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("LINE", "(\\w[-\\w]*[%s]*=.*)"), //
				RegexLeaf.end());
	}

	@Override
	public String getPatternEnd() {
		return "^(.*);$";
	}

	@Override
	protected CommandExecutionResult executeNow(PSystemEbnf diagram, BlocLines lines) throws NoSuchColorException {
		return diagram.addBlocLines(lines, null, null);
	}

}
