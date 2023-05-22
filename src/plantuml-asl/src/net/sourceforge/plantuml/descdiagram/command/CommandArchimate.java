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
package net.sourceforge.plantuml.descdiagram.command;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.decoration.symbol.USymbols;
import net.sourceforge.plantuml.descdiagram.DescriptionDiagram;
import net.sourceforge.plantuml.klimt.color.ColorParser;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.Colors;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.plasma.Quark;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexOptional;
import net.sourceforge.plantuml.regex.RegexOr;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandArchimate extends SingleLineCommand2<DescriptionDiagram> {

	public CommandArchimate() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandArchimate.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("SYMBOL", "archimate"), //
				RegexLeaf.spaceOneOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexOr(//
						new RegexLeaf("CODE1", CommandCreateElementFull.CODE_WITH_QUOTE), //
						new RegexConcat(//
								new RegexLeaf("DISPLAY2", CommandCreateElementFull.DISPLAY), //
								new RegexOptional( //
										new RegexConcat( //
												RegexLeaf.spaceOneOrMore(), //
												new RegexLeaf("STEREOTYPE2", "(?:\\<\\<([-\\w]+)\\>\\>)") //
										)), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("as"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("CODE2", CommandCreateElementFull.CODE)), //
						new RegexConcat(//
								new RegexLeaf("CODE3", CommandCreateElementFull.CODE), //
								new RegexOptional( //
										new RegexConcat( //
												RegexLeaf.spaceOneOrMore(), //
												new RegexLeaf("STEREOTYPE3", "(?:\\<\\<([-\\w]+)\\>\\>)") //
										)), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("as"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("DISPLAY3", CommandCreateElementFull.DISPLAY)), //
						new RegexConcat(//
								new RegexLeaf("DISPLAY4", CommandCreateElementFull.DISPLAY_WITHOUT_QUOTE), //
								new RegexOptional( //
										new RegexConcat( //
												RegexLeaf.spaceOneOrMore(), //
												new RegexLeaf("STEREOTYPE4", "(?:\\<\\<([-\\w]+)\\>\\>)") //
										)), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("as"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("CODE4", CommandCreateElementFull.CODE)) //
				), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional( //
						new RegexConcat( //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("STEREOTYPE", "(?:\\<\\<([-\\w]+)\\>\\>)") //
						)), RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	@Override
	protected CommandExecutionResult executeArg(DescriptionDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final String codeRaw = arg.getLazzy("CODE", 0);

		final Quark<Entity> quark = diagram.quarkInContext(true, diagram.cleanId(codeRaw));

		String display = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(arg.getLazzy("DISPLAY", 0));
		if (display == null)
			display = quark.getName();

		Entity entity = (Entity) quark.getData();
		if (entity == null)
			entity = diagram.reallyCreateLeaf(quark, Display.getWithNewlines(display), LeafType.DESCRIPTION,
					USymbols.ARCHIMATE);

		final String icon = arg.getLazzy("STEREOTYPE", 0);

		entity.setDisplay(Display.getWithNewlines(display));
		entity.setUSymbol(USymbols.ARCHIMATE);
		if (icon != null)
			entity.setStereotype(
					Stereotype.build("<<$archimate/" + icon + ">>", diagram.getSkinParam().getCircledCharacterRadius(),
							diagram.getSkinParam().getFont(null, false, FontParam.CIRCLED_CHARACTER),
							diagram.getSkinParam().getIHtmlColorSet()));

		final Colors colors = color().getColor(arg, diagram.getSkinParam().getIHtmlColorSet());
		entity.setColors(colors);

		return CommandExecutionResult.ok();
	}
}
