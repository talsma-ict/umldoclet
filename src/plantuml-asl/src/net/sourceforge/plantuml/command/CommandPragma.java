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

import java.util.StringTokenizer;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.dot.GraphvizUtils;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOptional;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandPragma extends SingleLineCommand2<TitledDiagram> {

	public static final CommandPragma ME = new CommandPragma();

	private CommandPragma() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandPragma.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("!pragma"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("NAME", "([A-Za-z_][A-Za-z_0-9]*)"), //
				new RegexOptional( //
						new RegexConcat( //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("VALUE", "(.*)") //
						)), RegexLeaf.end()); //
	}

	@Override
	protected CommandExecutionResult executeArg(TitledDiagram system, LineLocation location, RegexResult arg) {
		final String name = StringUtils.goLowerCase(arg.get("NAME", 0));
		final String value = arg.get("VALUE", 0);
		if (name.equalsIgnoreCase("svgsize")) {
			if (value.contains(" ")) {
				final StringTokenizer st = new StringTokenizer(value);
				system.getSkinParam().setSvgSize(st.nextToken(), st.nextToken());
			}
		} else {
			system.getPragma().define(name, value);
			// ::comment when __CORE__
			if (name.equalsIgnoreCase("graphviz_dot") && value.equalsIgnoreCase("jdot"))
				return CommandExecutionResult.error(
						"This directive has been renamed to '!pragma layout smetana'. Please update your diagram.");

			if (name.equalsIgnoreCase("graphviz_dot"))
				return CommandExecutionResult.error("This directive has been renamed to '!pragma layout " + value
						+ "'. Please update your diagram.");

			if (name.equalsIgnoreCase("layout") && value.equalsIgnoreCase("smetana"))
				system.setUseSmetana(true);

			if (name.equalsIgnoreCase("layout") && value.equalsIgnoreCase("elk"))
				system.setUseElk(true);

			if (name.equalsIgnoreCase("layout") && value.equalsIgnoreCase(GraphvizUtils.VIZJS))
				system.getSkinParam().setUseVizJs(true);
			// ::done

		}
		return CommandExecutionResult.ok();
	}

}
