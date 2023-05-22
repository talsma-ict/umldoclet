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

import net.sourceforge.plantuml.abel.Entity;
import net.sourceforge.plantuml.abel.LeafType;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Trim;
import net.sourceforge.plantuml.decoration.symbol.USymbols;
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
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.stereo.Stereotype;
import net.sourceforge.plantuml.url.UrlBuilder;
import net.sourceforge.plantuml.utils.BlocLines;

public class CommandArchimateMultilines extends CommandMultilines2<AbstractEntityDiagram> {

	public CommandArchimateMultilines() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE, Trim.BOTH);
	}

	@Override
	public String getPatternEnd() {
		return "^(.*)\\]$";
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandArchimateMultilines.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("archimate"), //
				RegexLeaf.spaceOneOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("CODE", "([%pLN_.]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional( //
						new RegexConcat( //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("STEREOTYPE", "(?:\\<\\<([-\\w]+)\\>\\>)") //
						)), //
				RegexLeaf.spaceZeroOrMore(), //
				UrlBuilder.OPTIONAL, //
				RegexLeaf.spaceZeroOrMore(), //
				ColorParser.exp1(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\["), //
				new RegexLeaf("DESC", "(.*)"), //
				RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	@Override
	protected CommandExecutionResult executeNow(AbstractEntityDiagram diagram, BlocLines lines)
			throws NoSuchColorException {
		lines = lines.trim();
		final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
		final String codeRaw = line0.getLazzy("CODE", 0);

		final Quark<Entity> quark = diagram.quarkInContext(false, diagram.cleanId(codeRaw));
		if (quark.getData() != null)
			return CommandExecutionResult.error("Already exists " + quark.getName());

		final String icon = line0.getLazzy("STEREOTYPE", 0);

		final Entity entity = diagram.reallyCreateLeaf(quark, Display.getWithNewlines(quark), LeafType.DESCRIPTION,
				USymbols.RECTANGLE);

		lines = lines.subExtract(1, 1);
		Display display = lines.toDisplay();

		entity.setDisplay(display);
		entity.setUSymbol(USymbols.RECTANGLE);
		if (icon != null) {
			entity.setStereotype(
					Stereotype.build("<<$archimate/" + icon + ">>", diagram.getSkinParam().getCircledCharacterRadius(),
							diagram.getSkinParam().getFont(null, false, FontParam.CIRCLED_CHARACTER),
							diagram.getSkinParam().getIHtmlColorSet()));
		}

		final Colors colors = color().getColor(line0, diagram.getSkinParam().getIHtmlColorSet());
		entity.setColors(colors);

		return CommandExecutionResult.ok();
	}

}
