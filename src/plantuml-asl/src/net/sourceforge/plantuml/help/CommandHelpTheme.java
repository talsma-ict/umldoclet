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
package net.sourceforge.plantuml.help;

import java.io.IOException;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.theme.ThemeUtils;
import net.sourceforge.plantuml.utils.LineLocation;
import net.sourceforge.plantuml.utils.Log;

public class CommandHelpTheme extends SingleLineCommand2<Help> {

	public CommandHelpTheme() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandHelpTheme.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("help"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("themes?"), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(Help diagram, LineLocation location, RegexResult arg) {
		diagram.add("<b>Help on themes");
		diagram.add(" ");
		diagram.add(" The possible themes are :");

		try {
			for (String theme : ThemeUtils.getAllThemeNames()) {
				diagram.add("* " + theme);
			}
		} catch (IOException e) {
			final String message = "Unexpected error listing themes: " + e.getMessage();
			Log.error(message);
			Logme.error(e);
			return CommandExecutionResult.error(message);
		}

		return CommandExecutionResult.ok();
	}
}
