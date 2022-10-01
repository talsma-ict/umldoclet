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
package net.sourceforge.plantuml.statediagram.command;

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlMode;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.statediagram.StateDiagram;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandCreateState extends SingleLineCommand2<StateDiagram> {

	public CommandCreateState() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandCreateState.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("state"), //
				RegexLeaf.spaceOneOrMore(), //

				new RegexOr(//
						new RegexConcat(//
								new RegexLeaf("CODE1", "([%pLN_.]+)"), //
								RegexLeaf.spaceOneOrMore(), new RegexLeaf("as"), RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("DISPLAY1", "[%g]([^%g]+)[%g]")), //
						new RegexConcat(//
								new RegexLeaf("DISPLAY2", "[%g]([^%g]+)[%g]"), //
								RegexLeaf.spaceOneOrMore(), new RegexLeaf("as"), RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("CODE2", "([%pLN_.]+)")), //
						new RegexLeaf("CODE3", "([%pLN_.]+)"), //
						new RegexLeaf("CODE4", "[%g]([^%g]+)[%g]")), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREOTYPE", "(\\<\\<.*\\>\\>)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexLeaf("LINECOLOR", "##(?:\\[(dotted|dashed|bold)\\])?(\\w+)?")), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional( //
						new RegexConcat( //
								new RegexLeaf(":"), //
								RegexLeaf.spaceZeroOrMore(), //
								new RegexLeaf("ADDFIELD", "(.*)") //
						)), RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	@Override
	protected CommandExecutionResult executeArg(StateDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		final String idShort = arg.getLazzy("CODE", 0);
		final Ident ident = diagram.buildLeafIdent(idShort);
		final Code code = diagram.V1972() ? ident : diagram.buildCode(idShort);
		String display = arg.getLazzy("DISPLAY", 0);
		if (display == null) {
			display = code.getName();
		}
		final String stereotype = arg.get("STEREOTYPE", 0);
		final LeafType type = getTypeFromStereotype(stereotype);
		if (diagram.checkConcurrentStateOk(ident, code) == false) {
			return CommandExecutionResult.error("The state " + code.getName()
					+ " has been created in a concurrent state : it cannot be used here.");
		}
		final IEntity ent = diagram.getOrCreateLeaf(diagram.buildLeafIdent(idShort), code, type, null);
		ent.setDisplay(Display.getWithNewlines(display));

		if (stereotype != null) {
			ent.setStereotype(Stereotype.build(stereotype));
		}
		final String urlString = arg.get("URL", 0);
		if (urlString != null) {
			final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), UrlMode.STRICT);
			final Url url = urlBuilder.getUrl(urlString);
			ent.addUrl(url);
		}

		Colors colors = color().getColor(arg, diagram.getSkinParam().getIHtmlColorSet());
		final String s = arg.get("LINECOLOR", 1);

		final HColor lineColor = s == null ? null
				: diagram.getSkinParam().getIHtmlColorSet().getColor(s);
		if (lineColor != null) {
			colors = colors.add(ColorType.LINE, lineColor);
		}
		if (arg.get("LINECOLOR", 0) != null) {
			colors = colors.addLegacyStroke(arg.get("LINECOLOR", 0));
		}
		ent.setColors(colors);

		// ent.setSpecificColorTOBEREMOVED(ColorType.BACK,
		// diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("COLOR",
		// 0)));
		// ent.setSpecificColorTOBEREMOVED(ColorType.LINE,
		// diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("LINECOLOR",
		// 1)));
		// ent.applyStroke(arg.get("LINECOLOR", 0));

		final String addFields = arg.get("ADDFIELD", 0);
		if (addFields != null) {
			ent.getBodier().addFieldOrMethod(addFields);
		}
		return CommandExecutionResult.ok();
	}

	private LeafType getTypeFromStereotype(String stereotype) {
		if ("<<choice>>".equalsIgnoreCase(stereotype)) {
			return LeafType.STATE_CHOICE;
		}
		if ("<<fork>>".equalsIgnoreCase(stereotype)) {
			return LeafType.STATE_FORK_JOIN;
		}
		if ("<<join>>".equalsIgnoreCase(stereotype)) {
			return LeafType.STATE_FORK_JOIN;
		}
		if ("<<start>>".equalsIgnoreCase(stereotype)) {
			return LeafType.CIRCLE_START;
		}
		if ("<<end>>".equalsIgnoreCase(stereotype)) {
			return LeafType.CIRCLE_END;
		}
		return null;
	}

}
