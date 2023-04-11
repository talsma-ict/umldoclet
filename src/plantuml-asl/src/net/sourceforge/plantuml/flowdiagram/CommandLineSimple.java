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
package net.sourceforge.plantuml.flowdiagram;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.golem.TileGeometry;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOptional;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandLineSimple extends SingleLineCommand2<FlowDiagram> {

	public CommandLineSimple() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandLineSimple.class.getName(), RegexLeaf.start(), //
				new RegexOptional( //
						new RegexConcat( //
								new RegexLeaf("ORIENTATION", "([nsew])"), //
								RegexLeaf.spaceOneOrMore() //
						)), //
				new RegexLeaf("ID_DEST", "(\\w+)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("LABEL", "[%g](.*)[%g]"), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(FlowDiagram diagram, LineLocation location, RegexResult arg) {
		final String idDest = arg.get("ID_DEST", 0);
		final String label = arg.get("LABEL", 0);
		final String orientationString = arg.get("ORIENTATION", 0);
		TileGeometry orientation = TileGeometry.SOUTH;
		if (orientationString != null) {
			orientation = TileGeometry.fromString(orientationString);
		}
		diagram.lineSimple(orientation, idDest, label);
		return CommandExecutionResult.ok();
	}

}
