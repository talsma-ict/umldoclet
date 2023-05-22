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
package net.sourceforge.plantuml.descdiagram;

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.GroupType;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.classdiagram.command.GenericRegexProducer;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.decoration.symbol.USymbol;
import net.sourceforge.plantuml.decoration.symbol.USymbols;
import net.sourceforge.plantuml.klimt.color.ColorType;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import net.sourceforge.plantuml.klimt.creole.Display;
import net.sourceforge.plantuml.klimt.font.FontParam;
import net.sourceforge.plantuml.plasma.Quark;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.url.Url;
import net.sourceforge.plantuml.url.UrlBuilder;
import net.sourceforge.plantuml.url.UrlMode;
import net.sourceforge.plantuml.utils.LineLocation;

public class CommandCreateDomain extends SingleLineCommand2<DescriptionDiagram> {
	public static final String DISPLAY_WITH_GENERIC = "[%g](.+?)(?:\\<(" + GenericRegexProducer.PATTERN + ")\\>)?[%g]";
	public static final String CODE = "[^%s{}%g<>]+";

	public CommandCreateDomain() {
		super(getRegexConcat());
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandCreateDomain.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "(requirement|domain)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("DISPLAY", DISPLAY_WITH_GENERIC), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("as"), //
				RegexLeaf.spaceOneOrMore(), new RegexLeaf("CODE", "([a-zA-Z0-9]+)"), RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<\\<.+\\>\\>)?"), //
				// domain: lexical, causal, biddable
				// requirement: FR, NFR, quality
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("GROUP", "(\\{)?"), RegexLeaf.end());
	}

	@Override
	protected CommandExecutionResult executeArg(DescriptionDiagram diagram, LineLocation location, RegexResult arg)
			throws NoSuchColorException {
		String typeString = arg.get("TYPE", 0);
		String displayString = arg.getLazzy("DISPLAY", 0);
		String codeString = arg.getLazzy("CODE", 0);
		if (codeString == null)
			codeString = displayString;

		final String stereotype = arg.get("STEREO", 0);
		final GroupType type = typeString.equalsIgnoreCase("domain") ? GroupType.DOMAIN : GroupType.REQUIREMENT;
		final LeafType type2 = typeString.equalsIgnoreCase("domain") ? LeafType.DOMAIN : LeafType.REQUIREMENT;

		final Quark<Entity> quark = diagram.quarkInContext(true, diagram.cleanId(codeString));
		if (quark.getData() != null)
			return CommandExecutionResult.error("Object already exists : " + codeString);

		Display display = Display.getWithNewlines(displayString);
		final String urlString = arg.get("URL", 0);
		final String group = arg.get("GROUP", 0);
		Entity entity;
		if (group != null) {
			// final Entity currentGroup = diagram.getCurrentGroup();
			diagram.gotoGroup(quark, display, type);
			entity = diagram.getCurrentGroup();
		} else {
			entity = diagram.reallyCreateLeaf(quark, display, type2, null);
		}
		if (stereotype != null)
			entity.setStereotype(Stereotype.build(stereotype, diagram.getSkinParam().getCircledCharacterRadius(),
					diagram.getSkinParam().getFont(null, false, FontParam.CIRCLED_CHARACTER),
					diagram.getSkinParam().getIHtmlColorSet()));

		if (urlString != null) {
			final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), UrlMode.STRICT);
			final Url url = urlBuilder.getUrl(urlString);
			entity.addUrl(url);
		}
		final String s = arg.get("COLOR", 0);
		entity.setSpecificColorTOBEREMOVED(ColorType.BACK,
				s == null ? null : diagram.getSkinParam().getIHtmlColorSet().getColor(s));
		if (typeString.equalsIgnoreCase("domain")) {
			if (stereotype != null && stereotype.equalsIgnoreCase("<<Machine>>"))
				typeString = "machine";

			if (stereotype != null && stereotype.equalsIgnoreCase("<<Causal>>"))
				typeString = "causal";

			if (stereotype != null && stereotype.equalsIgnoreCase("<<Designed>>"))
				typeString = "designed";

			if (stereotype != null && stereotype.equalsIgnoreCase("<<Lexical>>"))
				typeString = "lexical";

			if (stereotype != null && stereotype.equalsIgnoreCase("<<Biddable>>"))
				typeString = "biddable";

		}
		USymbol usymbol = USymbols.fromString(typeString, diagram.getSkinParam());
		entity.setUSymbol(usymbol);
		return CommandExecutionResult.ok();
	}

}
