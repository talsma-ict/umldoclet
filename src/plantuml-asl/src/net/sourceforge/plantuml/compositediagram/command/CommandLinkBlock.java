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
package net.sourceforge.plantuml.compositediagram.command;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.compositediagram.CompositeDiagram;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;

public class CommandLinkBlock extends SingleLineCommand2<CompositeDiagram> {

	public CommandLinkBlock() {
		super(getRegexConcat());
	}

	static IRegex getRegexConcat() {
		return RegexConcat.build(CommandLinkBlock.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("ENT1", "([\\p{L}0-9_.]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("DECO1", "(\\[\\]|\\*\\))?"), //
				new RegexLeaf("QUEUE", "([=-]+|\\.+)"), //
				new RegexLeaf("DECO2", "(\\[\\]|\\(\\*)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("ENT2", "([\\p{L}0-9_.]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional( //
						new RegexConcat( //
								new RegexLeaf(":"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("DISPLAY", "(\\S*+)") //
						)), RegexLeaf.end()); //
	}

	@Override
	protected CommandExecutionResult executeArg(CompositeDiagram diagram, LineLocation location, RegexResult arg) {
		final String ent1 = arg.get("ENT1", 0);
		final String ent2 = arg.get("ENT2", 0);
		final IEntity cl1 = diagram.getOrCreateLeaf(diagram.buildLeafIdent(ent1),
				diagram.buildCode(ent1), null, null);
		final IEntity cl2 = diagram.getOrCreateLeaf(diagram.buildLeafIdent(ent2),
				diagram.buildCode(ent2), null, null);

		final String deco1 = arg.get("DECO1", 0);
		final String deco2 = arg.get("DECO2", 0);
		LinkType linkType = new LinkType(getLinkDecor(deco1), getLinkDecor(deco2));

		// if ("*)".equals(deco1)) {
		// linkType = linkType.getInterfaceProvider();
		// } else if ("(*".equals(deco2)) {
		// linkType = linkType.getInterfaceUser();
		// }

		final String queue = arg.get("QUEUE", 0);

		final Link link = new Link(cl1, cl2, linkType, Display.getWithNewlines(arg.get("DISPLAY", 0)), queue.length(),
				diagram.getSkinParam().getCurrentStyleBuilder());
		diagram.addLink(link);
		return CommandExecutionResult.ok();
	}

	private LinkDecor getLinkDecor(String s) {
		if ("[]".equals(s)) {
			return LinkDecor.SQUARE_toberemoved;
		}
		return LinkDecor.NONE;
	}

}
