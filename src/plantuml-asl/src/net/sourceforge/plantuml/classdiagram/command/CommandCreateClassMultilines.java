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
package net.sourceforge.plantuml.classdiagram.command;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.cucadiagram.Stereotag;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.skin.VisibilityModifier;

public class CommandCreateClassMultilines extends CommandMultilines2<ClassDiagram> {

	private static final String CODE = CommandLinkClass.getSeparator() + "?[\\p{L}0-9_]+" + "(?:"
			+ CommandLinkClass.getSeparator() + "[\\p{L}0-9_]+)*";
	public static final String CODES = CODE + "(?:\\s*,\\s*" + CODE + ")*";

	enum Mode {
		EXTENDS, IMPLEMENTS
	};

	public CommandCreateClassMultilines() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE);
	}

	@Override
	public String getPatternEnd() {
		return "(?i)^[%s]*\\}[%s]*$";
	}

	private static IRegex getRegexConcat() {
		return RegexConcat.build(CommandCreateClassMultilines.class.getName(), RegexLeaf.start(), //
				new RegexLeaf("VISIBILITY", "(" + VisibilityModifier.regexForVisibilityCharacterInClassName() + ")?"), //
				new RegexLeaf("TYPE", "(interface|enum|abstract[%s]+class|abstract|class|entity)"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexOr(//
						new RegexConcat(//
								new RegexLeaf("DISPLAY1", CommandCreateClass.DISPLAY_WITH_GENERIC), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("as"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("CODE1", "(" + CommandCreateClass.CODE + ")")), //
						new RegexConcat(//
								new RegexLeaf("CODE2", "(" + CommandCreateClass.CODE + ")"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("as"), //
								RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("DISPLAY2", CommandCreateClass.DISPLAY_WITH_GENERIC)), //
						new RegexLeaf("CODE3", "(" + CommandCreateClass.CODE + ")"), //
						new RegexLeaf("CODE4", "[%g]([^%g]+)[%g]")), //
				new RegexOptional(new RegexConcat(RegexLeaf.spaceZeroOrMore(), new RegexLeaf("GENERIC", "\\<("
						+ GenericRegexProducer.PATTERN + ")\\>"))), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<\\<.+\\>\\>)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("TAGS", Stereotag.pattern() + "?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexOptional(new RegexConcat(new RegexLeaf("##"), new RegexLeaf("LINECOLOR",
						"(?:\\[(dotted|dashed|bold)\\])?(\\w+)?"))), //
				new RegexOptional(new RegexConcat(RegexLeaf.spaceOneOrMore(), new RegexLeaf("EXTENDS",
						"(extends)[%s]+(" + CommandCreateClassMultilines.CODES + ")"))), //
				new RegexOptional(new RegexConcat(RegexLeaf.spaceOneOrMore(), new RegexLeaf("IMPLEMENTS",
						"(implements)[%s]+(" + CommandCreateClassMultilines.CODES + ")"))), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("\\{"), //
				RegexLeaf.spaceZeroOrMore(), //
				RegexLeaf.end() //
				);
	}

	@Override
	public boolean syntaxWithFinalBracket() {
		return true;
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	protected CommandExecutionResult executeNow(ClassDiagram diagram, BlocLines lines) {
		lines = lines.trimSmart(1);
		final RegexResult line0 = getStartingPattern().matcher(lines.getFirst499().getTrimmed().getString());
		final IEntity entity = executeArg0(diagram, line0);
		if (entity == null) {
			return CommandExecutionResult.error("No such entity");
		}
		if (lines.size() > 1) {
			lines = lines.subExtract(1, 1);
			final Url url = null;
			// if (lines.size() > 0) {
			// final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), ModeUrl.STRICT);
			// url = urlBuilder.getUrl(lines.getFirst499().toString());
			// } else {
			// url = null;
			// }
			// if (url != null) {
			// lines = lines.subExtract(1, 0);
			// }
			for (StringLocated s : lines) {
				if (s.getString().length() > 0 && VisibilityModifier.isVisibilityCharacter(s.getString())) {
					diagram.setVisibilityModifierPresent(true);
				}
				if (s instanceof StringLocated) {
					entity.getBodier().addFieldOrMethod(((StringLocated) s).getString(), entity);
				} else {
					entity.getBodier().addFieldOrMethod(s.toString(), entity);
				}
			}
			if (url != null) {
				entity.addUrl(url);
			}
		}

		manageExtends("EXTENDS", diagram, line0, entity);
		manageExtends("IMPLEMENTS", diagram, line0, entity);
		addTags(entity, line0.get("TAGS", 0));

		return CommandExecutionResult.ok();
	}

	public static void addTags(IEntity entity, String tags) {
		if (tags == null) {
			return;
		}
		for (String tag : tags.split("[ ]+")) {
			assert tag.startsWith("$");
			tag = tag.substring(1);
			entity.addStereotag(new Stereotag(tag));
		}
	}

	public static void manageExtends(String keyword, ClassDiagram diagram, RegexResult arg, final IEntity entity) {
		if (arg.get(keyword, 0) != null) {
			final Mode mode = arg.get(keyword, 0).equalsIgnoreCase("extends") ? Mode.EXTENDS : Mode.IMPLEMENTS;
			LeafType type2 = LeafType.CLASS;
			if (mode == Mode.IMPLEMENTS) {
				type2 = LeafType.INTERFACE;
			}
			if (mode == Mode.EXTENDS && entity.getLeafType() == LeafType.INTERFACE) {
				type2 = LeafType.INTERFACE;
			}
			final String codes = arg.get(keyword, 1);
			for (String s : codes.split(",")) {
				final Code other = Code.of(StringUtils.trin(s));
				final IEntity cl2 = diagram.getOrCreateLeaf(other, type2, null);
				LinkType typeLink = new LinkType(LinkDecor.NONE, LinkDecor.EXTENDS);
				if (type2 == LeafType.INTERFACE && entity.getLeafType() != LeafType.INTERFACE) {
					typeLink = typeLink.goDashed();
				}
				final Link link = new Link(cl2, entity, typeLink, Display.NULL, 2, null, null,
						diagram.getLabeldistance(), diagram.getLabelangle(), diagram.getSkinParam()
								.getCurrentStyleBuilder());
				diagram.addLink(link);
			}
		}
	}

	private IEntity executeArg0(ClassDiagram diagram, RegexResult arg) {

		final LeafType type = LeafType.getLeafType(StringUtils.goUpperCase(arg.get("TYPE", 0)));
		final String visibilityString = arg.get("VISIBILITY", 0);
		VisibilityModifier visibilityModifier = null;
		if (visibilityString != null) {
			visibilityModifier = VisibilityModifier.getVisibilityModifier(visibilityString + "FOO", false);
		}

		final Code code = Code.of(arg.getLazzy("CODE", 0)).eventuallyRemoveStartingAndEndingDoubleQuote("\"([:");
		final String display = arg.getLazzy("DISPLAY", 0);
		final String genericOption = arg.getLazzy("DISPLAY", 1);
		final String generic = genericOption != null ? genericOption : arg.get("GENERIC", 0);

		final String stereotype = arg.get("STEREO", 0);

		final ILeaf result;
		if (diagram.leafExist(code)) {
			result = diagram.getOrCreateLeaf(code, null, null);
			if (result.muteToType(type, null) == false) {
				return null;
			}
		} else {
			result = diagram.createLeaf(code, Display.getWithNewlines(display), type, null);
		}
		result.setVisibilityModifier(visibilityModifier);
		if (stereotype != null) {
			result.setStereotype(new Stereotype(stereotype, diagram.getSkinParam().getCircledCharacterRadius(), diagram
					.getSkinParam().getFont(null, false, FontParam.CIRCLED_CHARACTER), diagram.getSkinParam()
					.getIHtmlColorSet()));
		}

		final String urlString = arg.get("URL", 0);
		if (urlString != null) {
			final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), ModeUrl.STRICT);
			final Url url = urlBuilder.getUrl(urlString);
			result.addUrl(url);
		}

		Colors colors = color().getColor(arg, diagram.getSkinParam().getIHtmlColorSet());

		final HtmlColor lineColor = diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("LINECOLOR", 1));
		if (lineColor != null) {
			colors = colors.add(ColorType.LINE, lineColor);
		}
		if (arg.get("LINECOLOR", 0) != null) {
			colors = colors.addLegacyStroke(arg.get("LINECOLOR", 0));
		}
		result.setColors(colors);

		// result.setSpecificColorTOBEREMOVED(ColorType.BACK,
		// diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("COLOR", 0)));
		// result.setSpecificColorTOBEREMOVED(ColorType.LINE,
		// diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("LINECOLOR", 1)));
		// result.applyStroke(arg.get("LINECOLOR", 0));

		if (generic != null) {
			result.setGeneric(generic);
		}
		return result;
	}
}
