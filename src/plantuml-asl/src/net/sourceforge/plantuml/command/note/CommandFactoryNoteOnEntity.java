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
package net.sourceforge.plantuml.command.note;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlMode;
import net.sourceforge.plantuml.baraye.IEntity;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.classdiagram.command.CommandCreateClassMultilines;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.Trim;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkArg;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.cucadiagram.Stereotag;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public final class CommandFactoryNoteOnEntity implements SingleMultiFactoryCommand<AbstractEntityDiagram> {

	private final IRegex partialPattern;
	private final String key;

	public CommandFactoryNoteOnEntity(String key, IRegex partialPattern) {
		this.partialPattern = partialPattern;
		this.key = key;
	}

	private IRegex getRegexConcatSingleLine(IRegex partialPattern) {
		return RegexConcat.build(CommandFactoryNoteOnEntity.class.getName() + key + "single", RegexLeaf.start(), //
				new RegexLeaf("note"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("POSITION", "(right|left|top|bottom)"), //
				new RegexOr(//
						new RegexConcat(RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("of"), //
								RegexLeaf.spaceOneOrMore(), partialPattern), //
						new RegexLeaf("")), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("TAGS1", Stereotag.pattern() + "?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<{2}.*\\>{2})?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("TAGS2", Stereotag.pattern() + "?"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("NOTE", "(.*)"), //
				RegexLeaf.end() //
		);
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	private IRegex getRegexConcatMultiLine(IRegex partialPattern, final boolean withBracket) {
		if (withBracket) {
			return RegexConcat.build(CommandFactoryNoteOnEntity.class.getName() + key + "multi" + withBracket,
					RegexLeaf.start(), //
					new RegexLeaf("note"), //
					RegexLeaf.spaceOneOrMore(), //
					new RegexLeaf("POSITION", "(right|left|top|bottom)"), //
					new RegexOr(//
							new RegexConcat(RegexLeaf.spaceOneOrMore(), //
									new RegexLeaf("of"), //
									RegexLeaf.spaceOneOrMore(), //
									partialPattern), //
							new RegexLeaf("")), //
					RegexLeaf.spaceZeroOrMore(), //
					new RegexLeaf("TAGS1", Stereotag.pattern() + "?"), //
					RegexLeaf.spaceZeroOrMore(), //
					new RegexLeaf("STEREO", "(\\<{2}.*\\>{2})?"), //
					RegexLeaf.spaceZeroOrMore(), //
					new RegexLeaf("TAGS2", Stereotag.pattern() + "?"), //
					RegexLeaf.spaceZeroOrMore(), //
					color().getRegex(), //
					RegexLeaf.spaceZeroOrMore(), //
					new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
					RegexLeaf.spaceZeroOrMore(), //
					new RegexLeaf("\\{"), //
					RegexLeaf.end() //
			);
		}
		return RegexConcat.build(CommandFactoryNoteOnEntity.class.getName() + key + "multi" + withBracket,
				RegexLeaf.start(), //
				new RegexLeaf("note"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("POSITION", "(right|left|top|bottom)"), //
				new RegexOr(//
						new RegexConcat(RegexLeaf.spaceOneOrMore(), //
								new RegexLeaf("of"), //
								RegexLeaf.spaceOneOrMore(), //
								partialPattern), //
						new RegexLeaf("")), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("TAGS1", Stereotag.pattern() + "?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<{2}.*\\>{2})?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("TAGS2", Stereotag.pattern() + "?"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				RegexLeaf.end() //
		);
	}

	public Command<AbstractEntityDiagram> createSingleLine() {
		return new SingleLineCommand2<AbstractEntityDiagram>(getRegexConcatSingleLine(partialPattern)) {

			@Override
			protected CommandExecutionResult executeArg(final AbstractEntityDiagram system, LineLocation location,
					RegexResult arg) throws NoSuchColorException {
				final String s = arg.get("NOTE", 0);
				return executeInternal(arg, system, null, BlocLines.getWithNewlines(s));
			}
		};
	}

	public Command<AbstractEntityDiagram> createMultiLine(final boolean withBracket) {
		return new CommandMultilines2<AbstractEntityDiagram>(getRegexConcatMultiLine(partialPattern, withBracket),
				MultilinesStrategy.KEEP_STARTING_QUOTE, Trim.BOTH) {

			@Override
			public String getPatternEnd() {
				if (withBracket)
					return "^(\\})$";

				return "^[%s]*(end[%s]?note)$";
			}

			protected CommandExecutionResult executeNow(final AbstractEntityDiagram system, BlocLines lines)
					throws NoSuchColorException {
				// StringUtils.trim(lines, false);
				final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
				lines = lines.subExtract(1, 1);
				lines = lines.removeEmptyColumns();

				Url url = null;
				if (line0.get("URL", 0) != null) {
					final UrlBuilder urlBuilder = new UrlBuilder(system.getSkinParam().getValue("topurl"),
							UrlMode.STRICT);
					url = urlBuilder.getUrl(line0.get("URL", 0));
				}

				return executeInternal(line0, system, url, lines);
			}
		};
	}

	private CommandExecutionResult executeInternal(RegexResult line0, AbstractEntityDiagram diagram, Url url,
			BlocLines strings) throws NoSuchColorException {

		final String pos = line0.get("POSITION", 0);

		final String idShort = line0.get("ENTITY", 0);
		final IEntity cl1;
		if (idShort == null) {
			cl1 = diagram.getLastEntity();
			if (cl1 == null)
				return CommandExecutionResult.error("Nothing to note to");

		} else {
			final Ident ident = diagram.buildLeafIdent(idShort);
			final Code code = diagram.V1972() ? ident : diagram.buildCode(idShort);
			if (diagram.isGroup(code)) {
				cl1 = diagram.V1972() ? diagram.getGroupStrict(ident) : diagram.getGroup(code);
			} else {
				if (diagram.V1972() && diagram.leafExistSmart(diagram.cleanIdent(ident)))
					cl1 = diagram.getLeafSmart(diagram.cleanIdent(ident));
				else
					cl1 = diagram.getOrCreateLeaf(ident, code, null, null);
			}
		}

		final Position position = Position.valueOf(StringUtils.goUpperCase(pos))
				.withRankdir(diagram.getSkinParam().getRankdir());
		Colors colors = color().getColor(line0, diagram.getSkinParam().getIHtmlColorSet());

		final String stereotypeString = line0.get("STEREO", 0);
		Stereotype stereotype = null;
		if (stereotypeString != null) {
			stereotype = Stereotype.build(stereotypeString);
			colors = colors.applyStereotypeForNote(stereotype, diagram.getSkinParam(), ColorParam.noteBackground,
					ColorParam.noteBorder);
		}

		if (diagram.getPragma().useKermor() && cl1.isGroup()) {
			cl1.addNote(strings.toDisplay(), position, colors);
			return CommandExecutionResult.ok();
		}

		final String tmp = diagram.getUniqueSequence("GMN");
		final Ident idNewLong = diagram.buildLeafIdent(tmp);
		final IEntity note;
		if (diagram.V1972())
			note = diagram.createLeaf(idNewLong, idNewLong, strings.toDisplay(), LeafType.NOTE, null);
		else
			note = diagram.createLeaf(idNewLong, diagram.buildCode(tmp), strings.toDisplay(), LeafType.NOTE, null);

		if (stereotypeString != null)
			note.setStereotype(stereotype);

		note.setColors(colors);
		if (url != null)
			note.addUrl(url);

		CommandCreateClassMultilines.addTags(note, line0.getLazzy("TAGS", 0));

		final Link link;

		final LinkType type = new LinkType(LinkDecor.NONE, LinkDecor.NONE).goDashed();
		if (position == Position.RIGHT) {
			link = new Link(diagram.getIEntityFactory(), diagram.getSkinParam().getCurrentStyleBuilder(), cl1, note,
					type, LinkArg.noDisplay(1));
			link.setHorizontalSolitary(true);
		} else if (position == Position.LEFT) {
			link = new Link(diagram.getIEntityFactory(), diagram.getSkinParam().getCurrentStyleBuilder(), note, cl1,
					type, LinkArg.noDisplay(1));
			link.setHorizontalSolitary(true);
		} else if (position == Position.BOTTOM) {
			link = new Link(diagram.getIEntityFactory(), diagram.getSkinParam().getCurrentStyleBuilder(), cl1, note,
					type, LinkArg.noDisplay(2));
		} else if (position == Position.TOP) {
			link = new Link(diagram.getIEntityFactory(), diagram.getSkinParam().getCurrentStyleBuilder(), note, cl1,
					type, LinkArg.noDisplay(2));
		} else {
			throw new IllegalArgumentException();
		}
		diagram.addLink(link);
		return CommandExecutionResult.ok();
	}

}
