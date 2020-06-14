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
package net.sourceforge.plantuml.sequencediagram.command;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColorSet;
import net.sourceforge.plantuml.sequencediagram.EventWithDeactivate;
import net.sourceforge.plantuml.sequencediagram.LifeEventType;
import net.sourceforge.plantuml.sequencediagram.Message;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.skin.ArrowBody;
import net.sourceforge.plantuml.skin.ArrowConfiguration;

public class CommandReturn extends SingleLineCommand2<SequenceDiagram> {

	public CommandReturn() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandReturn.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("PARALLEL", "(&[%s]*)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("return"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional( //
						new RegexConcat( //
								new RegexLeaf("COLOR", "(#\\w+)"), //
								RegexLeaf.spaceOneOrMore() //
						)), //
				new RegexLeaf("MESSAGE", "(.*)"), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(SequenceDiagram diagram, LineLocation location, RegexResult arg) {

		Message message1 = diagram.getActivatingMessage();
		boolean doDeactivation = true;
		if (message1 == null) {
			final EventWithDeactivate last = diagram.getLastEventWithDeactivate();
			if (last instanceof Message == false) {
				return CommandExecutionResult.error("Nowhere to return to.");
			}
			message1 = (Message) last;
			doDeactivation = false;
		}

		ArrowConfiguration arrow = message1.getArrowConfiguration().withBody(ArrowBody.DOTTED);
		final String color = arg.get("COLOR", 0);
		if (color != null) {
			arrow = arrow.withColor(HtmlColorSet.getInstance().getColorIfValid(color));
		}

		final Display display = Display.getWithNewlines(arg.get("MESSAGE", 0));
		final Message message2 = new Message(diagram.getSkinParam().getCurrentStyleBuilder(),
				message1.getParticipant2(), message1.getParticipant1(), display, arrow,
				diagram.getNextMessageNumber());
		final boolean parallel = arg.get("PARALLEL", 0) != null;
		if (parallel) {
			message2.goParallel();
		}
		diagram.addMessage(message2);

		if (doDeactivation) {
			final String error = diagram.activate(message1.getParticipant2(), LifeEventType.DEACTIVATE, null);
			if (error != null) {
				return CommandExecutionResult.error(error);
			}
		}
		return CommandExecutionResult.ok();

	}

}
