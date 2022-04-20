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
package net.sourceforge.plantuml.style;

import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.TitledDiagram;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.security.SFile;

public class CommandStyleImport extends SingleLineCommand2<TitledDiagram> {

	public CommandStyleImport() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandStyleImport.class.getName(), //
				RegexLeaf.start(), //
				new RegexLeaf("\\<style"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\w+"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("="), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("[%q%g]?"), //
				new RegexLeaf("PATH", "([^%q%g]*)"), //
				new RegexLeaf("[%q%g]?"), //
				new RegexLeaf("\\>"), RegexLeaf.end()); //
	}

	@Override
	protected CommandExecutionResult executeArg(TitledDiagram diagram, LineLocation location, RegexResult arg) {
		final String path = arg.get("PATH", 0);
		try {
			final SFile f = FileSystem.getInstance().getFile(path);
			BlocLines lines = null;
			if (f.exists()) {
				lines = BlocLines.load(f, location);
			} else {
				final InputStream internalIs = StyleLoader.class.getResourceAsStream("/skin/" + path);
				if (internalIs != null) {
					lines = BlocLines.load(internalIs, location);
				}
			}
			if (lines == null) {
				return CommandExecutionResult.error("Cannot read: " + path);
			}
			final StyleBuilder styleBuilder = diagram.getSkinParam().getCurrentStyleBuilder();
			for (Style modifiedStyle : StyleLoader.getDeclaredStyles(lines, styleBuilder)) {
				diagram.getSkinParam().muteStyle(modifiedStyle);
			}
		} catch (IOException e) {
			return CommandExecutionResult.error("Cannot read: " + path);
		}
		return CommandExecutionResult.ok();
	}
}
