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

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlMode;
import net.sourceforge.plantuml.activitydiagram.ActivityDiagram;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Ident;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;
import net.sourceforge.plantuml.utils.UniqueSequence;

public final class CommandFactoryNoteActivity implements SingleMultiFactoryCommand<ActivityDiagram> {

	private IRegex getRegexConcatMultiLine() {
		return RegexConcat.build(CommandFactoryNoteActivity.class.getName() + "multi", RegexLeaf.start(), //
				new RegexLeaf("note"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("POSITION", "(right|left|top|bottom)"), //
				RegexLeaf.spaceZeroOrMore(), //
				ColorParser.exp1(), //
				RegexLeaf.spaceZeroOrMore(), RegexLeaf.end());
	}

	private IRegex getRegexConcatSingleLine() {
		return RegexConcat.build(CommandFactoryNoteActivity.class.getName() + "single", RegexLeaf.start(), //
				new RegexLeaf("note"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("POSITION", "(right|left|top|bottom)"), //
				RegexLeaf.spaceZeroOrMore(), //
				ColorParser.exp1(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("NOTE", "(.*)"), RegexLeaf.end());
	}

	public Command<ActivityDiagram> createMultiLine(boolean withBracket) {
		return new CommandMultilines2<ActivityDiagram>(getRegexConcatMultiLine(),
				MultilinesStrategy.KEEP_STARTING_QUOTE) {

			@Override
			public String getPatternEnd() {
				return "^[%s]*end[%s]?note$";
			}

			public final CommandExecutionResult executeNow(final ActivityDiagram diagram, BlocLines lines)
					throws NoSuchColorException {
				// StringUtils.trim(lines, true);
				final RegexResult arg = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
				lines = lines.subExtract(1, 1);
				lines = lines.removeEmptyColumns();

				Display strings = lines.toDisplay();

				Url url = null;
				if (strings.size() > 0) {
					final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"),
							UrlMode.STRICT);
					url = urlBuilder.getUrl(strings.get(0).toString());
				}
				if (url != null) {
					strings = strings.subList(1, strings.size());
				}

				// final String s = StringUtils.getMergedLines(strings);

				final String codeString = UniqueSequence.getString("GMN");
				final Ident ident = diagram.buildLeafIdent(codeString);
				final Code code = diagram.V1972() ? ident : diagram.buildCode(codeString);
				final IEntity note = diagram.createLeaf(ident, code, strings, LeafType.NOTE, null);
				if (url != null) {
					note.addUrl(url);
				}
				return executeInternal(diagram, arg, note);
			}
		};
	}

	public Command<ActivityDiagram> createSingleLine() {
		return new SingleLineCommand2<ActivityDiagram>(getRegexConcatSingleLine()) {

			@Override
			protected CommandExecutionResult executeArg(final ActivityDiagram diagram, LineLocation location,
					RegexResult arg) throws NoSuchColorException {
				final String tmp = UniqueSequence.getString("GN");
				final Ident ident = diagram.buildLeafIdent(tmp);
				final Code code = diagram.V1972() ? ident : diagram.buildCode(tmp);
				final IEntity note = diagram.createNote(ident, code, Display.getWithNewlines(arg.get("NOTE", 0)));
				return executeInternal(diagram, arg, note);
			}
		};
	}

	private CommandExecutionResult executeInternal(ActivityDiagram diagram, RegexResult arg, IEntity note)
			throws NoSuchColorException {

		final String s = arg.get("COLOR", 0);
		note.setSpecificColorTOBEREMOVED(ColorType.BACK, s == null ? null
				: diagram.getSkinParam().getIHtmlColorSet().getColor(diagram.getSkinParam().getThemeStyle(), s));

		IEntity activity = diagram.getLastEntityConsulted();
		if (activity == null) {
			activity = diagram.getStart();
		}

		final Link link;

		final Position position = Position.valueOf(StringUtils.goUpperCase(arg.get("POSITION", 0)))
				.withRankdir(diagram.getSkinParam().getRankdir());

		final LinkType type = new LinkType(LinkDecor.NONE, LinkDecor.NONE).goDashed();

		if (position == Position.RIGHT) {
			link = new Link(activity, note, type, Display.NULL, 1, diagram.getSkinParam().getCurrentStyleBuilder());
		} else if (position == Position.LEFT) {
			link = new Link(note, activity, type, Display.NULL, 1, diagram.getSkinParam().getCurrentStyleBuilder());
		} else if (position == Position.BOTTOM) {
			link = new Link(activity, note, type, Display.NULL, 2, diagram.getSkinParam().getCurrentStyleBuilder());
		} else if (position == Position.TOP) {
			link = new Link(note, activity, type, Display.NULL, 2, diagram.getSkinParam().getCurrentStyleBuilder());
		} else {
			throw new IllegalArgumentException();
		}
		diagram.addLink(link);
		return CommandExecutionResult.ok();
	}

}
