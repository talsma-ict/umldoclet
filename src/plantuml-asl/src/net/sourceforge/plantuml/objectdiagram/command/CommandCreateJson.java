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
package net.sourceforge.plantuml.objectdiagram.command;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.baraye.IEntity;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.command.CommandControl;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Trim;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.BodierJSon;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.json.Json.DefaultHandler;
import net.sourceforge.plantuml.json.JsonParser;
import net.sourceforge.plantuml.json.JsonValue;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.utils.BlocLines;
import net.sourceforge.plantuml.utils.StringLocated;

public class CommandCreateJson extends CommandMultilines2<AbstractEntityDiagram> {

	public CommandCreateJson() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE, Trim.BOTH);
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandCreateJson.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("TYPE", "json"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("NAME", "(?:[%g]([^%g]+)[%g][%s]+as[%s]+)?([%pLN_.]+)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<\\<.+\\>\\>)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				UrlBuilder.OPTIONAL, //
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
	protected CommandExecutionResult executeNow(AbstractEntityDiagram diagram, BlocLines lines)
			throws NoSuchColorException {
		lines = lines.trim().removeEmptyLines();
		final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
		final IEntity entity1 = executeArg0(diagram, line0);
		if (entity1 == null)
			return CommandExecutionResult.error("No such entity");

		final JsonValue json = getJsonValue(lines);

		if (json == null)
			return CommandExecutionResult.error("Bad data");
		((BodierJSon) entity1.getBodier()).setJson(json);

		return CommandExecutionResult.ok();
	}

	@Override
	protected CommandControl finalVerification(BlocLines lines) {
		final JsonValue json = getJsonValue(lines);
		if (json == null)
			return CommandControl.OK_PARTIAL;

		return super.finalVerification(lines);
	}

	private JsonValue getJsonValue(BlocLines lines) {
		try {
			final String sb = getJsonString(lines);
			final DefaultHandler handler = new DefaultHandler();
			new JsonParser(handler).parse(sb);
			final JsonValue json = handler.getValue();
			return json;
		} catch (Exception e) {
			return null;
		}
	}

	private String getJsonString(BlocLines lines) {
		lines = lines.subExtract(1, 1);
		final StringBuilder sb = new StringBuilder("{");
		for (StringLocated sl : lines) {
			final String line = sl.getString();
			assert line.length() > 0;
			sb.append(line);
		}
		sb.append("}");
		return sb.toString();
	}

	private IEntity executeArg0(AbstractEntityDiagram diagram, RegexResult line0) throws NoSuchColorException {
		final String name = line0.get("NAME", 1);
		final Ident ident = diagram.buildLeafIdent(name);
		final Code code = diagram.buildCode(name);
		final String display = line0.get("NAME", 0);
		final String stereotype = line0.get("STEREO", 0);
		final boolean leafExist = diagram.leafExist(code);
		if (leafExist)
			return diagram.getOrCreateLeaf(diagram.buildLeafIdent(name), code, LeafType.JSON, null);

		final IEntity entity = diagram.createLeaf(ident, code, Display.getWithNewlines(display), LeafType.JSON, null);
		if (stereotype != null)
			entity.setStereotype(Stereotype.build(stereotype, diagram.getSkinParam().getCircledCharacterRadius(),
					diagram.getSkinParam().getFont(null, false, FontParam.CIRCLED_CHARACTER),
					diagram.getSkinParam().getIHtmlColorSet()));

		final String s = line0.get("COLOR", 0);
		entity.setSpecificColorTOBEREMOVED(ColorType.BACK,
				s == null ? null : diagram.getSkinParam().getIHtmlColorSet().getColor(s));
		return entity;
	}

}
