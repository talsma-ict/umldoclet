/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.bpm;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;

public class CommandDockedEvent extends SingleLineCommand2<BpmDiagram> {

	public CommandDockedEvent() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandDockedEvent.class.getName(), RegexLeaf.start(), //
				new RegexLeaf(":"), //
				new RegexLeaf("LABEL", "(.*)"), //
				new RegexLeaf("STYLE", ";"), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(BpmDiagram diagram, LineLocation location, RegexResult arg) {

		final String label = arg.get("LABEL", 0);
		final BpmElement element = new BpmElement(null, BpmElementType.DOCKED_EVENT, label);
		final BpmEvent event = new BpmEventAdd(element);
		return diagram.addEvent(event);
	}

}
