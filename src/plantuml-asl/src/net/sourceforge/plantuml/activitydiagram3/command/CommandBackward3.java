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
package net.sourceforge.plantuml.activitydiagram3.command;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.descdiagram.command.CommandLinkElement;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandBackward3 extends SingleLineCommand2<ActivityDiagram3> {

	public CommandBackward3() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandBackward3.class.getName(), RegexLeaf.start(), //
				new RegexOptional(new RegexConcat( //
						new RegexLeaf("\\("), //
						new RegexOptional(new RegexOr(//
								new RegexLeaf("->"), //
								new RegexLeaf("INCOMING_COLOR", CommandLinkElement.STYLE_COLORS_MULTIPLES))), //
						new RegexLeaf("INCOMING", "(.*?)"), //
						new RegexLeaf("\\)"))), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("backward"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				new RegexLeaf("LABEL", "(.*?)"), //
				new RegexLeaf("STYLE", CommandActivity3.endingGroup()), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexConcat( //
						new RegexLeaf("\\("), //
						new RegexOptional(new RegexOr(//
								new RegexLeaf("->"), //
								new RegexLeaf("OUTCOMING_COLOR", CommandLinkElement.STYLE_COLORS_MULTIPLES))), //
						new RegexLeaf("OUTCOMING", "(.*?)"), //
						new RegexLeaf("\\)"))), //
				RegexLeaf.spaceZeroOrMore(), //
				RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, LineLocation location, RegexResult arg) throws NoSuchColorException {
		final BoxStyle boxStyle;
		final String styleString = arg.get("STYLE", 0);

		if (styleString == null) {
			boxStyle = BoxStyle.PLAIN;
		} else {
			boxStyle = BoxStyle.fromChar(styleString.charAt(0));
		}

		final Display label = Display.getWithNewlines(arg.get("LABEL", 0));

		final LinkRendering in = getBackRendering(diagram, arg, "INCOMING");
		final LinkRendering out = getBackRendering(diagram, arg, "OUTCOMING");

		return diagram.backward(label, boxStyle, in, out);
	}

	static public LinkRendering getBackRendering(ActivityDiagram3 diagram, RegexResult arg, String name) throws NoSuchColorException {
		final LinkRendering in;
		final Rainbow incomingColor = getRainbow(name + "_COLOR", diagram, arg);
		if (incomingColor == null)
			in = LinkRendering.none();
		else
			in = LinkRendering.create(incomingColor);
		final String label = arg.get(name, 0);
		return in.withDisplay(Display.getWithNewlines(label));
	}

	static private Rainbow getRainbow(String key, ActivityDiagram3 diagram, RegexResult arg) throws NoSuchColorException {
		final String colorString = arg.get(key, 0);
		if (colorString == null) {
			return null;
		}
		return Rainbow.build(diagram.getSkinParam(), colorString, diagram.getSkinParam().colorArrowSeparationSpace());
	}

}
