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
package net.sourceforge.plantuml.objectdiagram.command;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.objectdiagram.AbstractClassOrObjectDiagram;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public class CommandCreateEntityObjectMultilines extends CommandMultilines2<AbstractClassOrObjectDiagram> {

	public CommandCreateEntityObjectMultilines() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE);
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandCreateEntityObjectMultilines.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "object"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("NAME", "(?:[%g]([^%g]+)[%g][%s]+as[%s]+)?([%pLN_.]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<\\<.+\\>\\>)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				RegexLeaf.spaceZeroOrMore(), //
				ColorParser.exp1(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\{"), //
				RegexLeaf.end());
	}

	@Override
	public String getPatternEnd() {
		return "^[%s]*\\}[%s]*$";
	}

	@Override
	protected CommandExecutionResult executeNow(AbstractClassOrObjectDiagram diagram, BlocLines lines)
			throws NoSuchColorException {
		lines = lines.trim().removeEmptyLines();
		final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
		final IEntity entity = executeArg0(diagram, line0);
		if (entity == null) {
			return CommandExecutionResult.error("No such entity");
		}
		lines = lines.subExtract(1, 1);
		for (StringLocated s : lines) {
			assert s.getString().length() > 0;
			if (VisibilityModifier.isVisibilityCharacter(s.getString())) {
				diagram.setVisibilityModifierPresent(true);
			}
			entity.getBodier().addFieldOrMethod(s.getString());
		}
		return CommandExecutionResult.ok();
	}

	private IEntity executeArg0(AbstractClassOrObjectDiagram diagram, RegexResult line0) throws NoSuchColorException {
		final String name = line0.get("NAME", 1);
		final Ident ident = diagram.buildLeafIdent(name);
		final Code code = diagram.V1972() ? ident : diagram.buildCode(name);
		final String display = line0.get("NAME", 0);
		final String stereotype = line0.get("STEREO", 0);
		final boolean leafExist = diagram.V1972() ? diagram.leafExistSmart(ident) : diagram.leafExist(code);
		if (leafExist) {
			return diagram.getOrCreateLeaf(diagram.buildLeafIdent(name), code, LeafType.OBJECT, null);
		}
		final IEntity entity = diagram.createLeaf(ident, code, Display.getWithNewlines(display), LeafType.OBJECT, null);
		if (stereotype != null) {
			entity.setStereotype(Stereotype.build(stereotype, diagram.getSkinParam().getCircledCharacterRadius(),
					diagram.getSkinParam().getFont(null, false, FontParam.CIRCLED_CHARACTER),
					diagram.getSkinParam().getIHtmlColorSet()));
		}
		final String s = line0.get("COLOR", 0);
		entity.setSpecificColorTOBEREMOVED(ColorType.BACK, s == null ? null
				: diagram.getSkinParam().getIHtmlColorSet().getColor(diagram.getSkinParam().getThemeStyle(), s));
		return entity;
	}

}
