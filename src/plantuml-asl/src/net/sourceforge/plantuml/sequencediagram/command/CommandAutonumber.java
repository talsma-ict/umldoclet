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
package net.sourceforge.plantuml.sequencediagram.command;

import java.text.DecimalFormat;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.sequencediagram.DottedNumber;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;

public class CommandAutonumber extends SingleLineCommand2<SequenceDiagram> {

	public CommandAutonumber() {
		super(getConcat());
	}

	private static RegexConcat getConcat() {
		return RegexConcat.build(CommandAutonumber.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("autonumber"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("START", "(\\d(?:(?:[^%pLN%s]+|\\d+)*\\d)?)?"), //
				new RegexOptional( //
						new RegexConcat( //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("STEP", "(\\d+)") //
						)), //
				new RegexOptional( //
						new RegexConcat( //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("FORMAT", "[%g]([^%g]+)[%g]") //
						)), //
				RegexLeaf.spaceZeroOrMore(), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(SequenceDiagram diagram, LineLocation location, RegexResult arg) {
		DottedNumber start = DottedNumber.create("1");
		final String arg0 = arg.get("START", 0);
		// System.err.println("arg0=" + arg0);
		if (arg0 != null) {
			start = DottedNumber.create(arg0);
		}
		// System.err.println("start=" + start);
		int inc = 1;
		final String arg1 = arg.get("STEP", 0);
		if (arg1 != null) {
			inc = Integer.parseInt(arg1);
		}

		final String arg2 = arg.get("FORMAT", 0);
		final String df = arg2 == null ? "<b>0</b>" : arg2;
		final DecimalFormat decimalFormat;
		try {
			decimalFormat = new DecimalFormat(df);
		} catch (IllegalArgumentException e) {
			return CommandExecutionResult.error("Error in pattern : " + df);
		}

		diagram.autonumberGo(start, inc, decimalFormat);
		return CommandExecutionResult.ok();
	}

}
