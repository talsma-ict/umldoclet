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
 * Contribution   :  Serge Wenger
 */
package net.sourceforge.plantuml.statediagram.command;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.descdiagram.command.CommandLinkElement;

public class CommandLinkStateReverse extends CommandLinkStateCommon {

	public CommandLinkStateReverse() {
		super(getRegex());
	}

	static RegexConcat getRegex() {
		return RegexConcat.build(CommandLinkStateReverse.class.getName(), RegexLeaf.start(), //
				getStatePattern("ENT2"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexConcat(
						//
						new RegexLeaf("ARROW_CIRCLE_END", "(o[%s]+)?"), //
						new RegexLeaf("\\<"), //
						new RegexLeaf("ARROW_BODY2", "(-*)"), //
						new RegexLeaf("ARROW_STYLE2", "(?:\\[(" + CommandLinkElement.LINE_STYLE + ")\\])?"), //
						new RegexLeaf("ARROW_DIRECTION", "(left|right|up|down|le?|ri?|up?|do?)?"), //
						new RegexLeaf("ARROW_STYLE1", "(?:\\[(" + CommandLinkElement.LINE_STYLE + ")\\])?"), //
						new RegexLeaf("ARROW_BODY1", "(-+)"), //
						new RegexLeaf("ARROW_CROSS_START", "(x)?")), //
				RegexLeaf.spaceZeroOrMore(), //
				getStatePattern("ENT1"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional( //
						new RegexConcat( //
								new RegexLeaf(":"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("LABEL", "(.+)") //
						)), RegexLeaf.end());
	}

	@Override
	protected Direction getDefaultDirection() {
		return Direction.LEFT;
	}

}
