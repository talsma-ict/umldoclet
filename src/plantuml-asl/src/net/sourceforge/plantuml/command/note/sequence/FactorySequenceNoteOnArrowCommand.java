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
package net.sourceforge.plantuml.command.note.sequence;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.note.SingleMultiFactoryCommand;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.sequencediagram.AbstractMessage;
import net.sourceforge.plantuml.sequencediagram.EventWithDeactivate;
import net.sourceforge.plantuml.sequencediagram.GroupingLeaf;
import net.sourceforge.plantuml.sequencediagram.Note;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteStyle;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public final class FactorySequenceNoteOnArrowCommand implements SingleMultiFactoryCommand<SequenceDiagram> {

	private IRegex getRegexConcatMultiLine() {
		return RegexConcat.build(FactorySequenceNoteOnArrowCommand.class.getName() + "multi", RegexLeaf.start(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STYLE", "(note|hnote|rnote)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<{2}.*\\>{2})?"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("POSITION", "(right|left|bottom|top)"), //
				RegexLeaf.spaceZeroOrMore(), //
				ColorParser.exp1(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), RegexLeaf.end());
	}

	private IRegex getRegexConcatSingleLine() {
		return RegexConcat.build(FactorySequenceNoteOnArrowCommand.class.getName() + "single", RegexLeaf.start(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STYLE", "(note|hnote|rnote)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<{2}.*\\>{2})?"), //
				RegexLeaf.spaceOneOrMore(), //
				new RegexLeaf("POSITION", "(right|left|bottom|top)"), //
				RegexLeaf.spaceZeroOrMore(), //
				ColorParser.exp1(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("NOTE", "(.*)"), RegexLeaf.end());
	}

	public Command<SequenceDiagram> createSingleLine() {
		return new SingleLineCommand2<SequenceDiagram>(getRegexConcatSingleLine()) {

			@Override
			protected CommandExecutionResult executeArg(final SequenceDiagram system, LineLocation location,
					RegexResult arg) throws NoSuchColorException {
				return executeInternal(system, arg, BlocLines.getWithNewlines(arg.get("NOTE", 0)));
			}

		};
	}

	public Command<SequenceDiagram> createMultiLine(boolean withBracket) {
		return new CommandMultilines2<SequenceDiagram>(getRegexConcatMultiLine(),
				MultilinesStrategy.KEEP_STARTING_QUOTE) {

			@Override
			public String getPatternEnd() {
				return "(?i)^[%s]*end[%s]?note$";
			}

			protected CommandExecutionResult executeNow(final SequenceDiagram diagram, BlocLines lines) throws NoSuchColorException {
				final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
				lines = lines.subExtract(1, 1);
				lines = lines.removeEmptyColumns();
				return executeInternal(diagram, line0, lines);
			}

		};
	}

	private CommandExecutionResult executeInternal(SequenceDiagram diagram, final RegexResult line0, BlocLines lines) throws NoSuchColorException {
		final EventWithDeactivate m = diagram.getLastEventWithDeactivate();
		if (m instanceof AbstractMessage || m instanceof GroupingLeaf) {
			final NotePosition position = NotePosition.valueOf(StringUtils.goUpperCase(line0.get("POSITION", 0)));
			Url url = null;
			if (line0.get("URL", 0) != null) {
				final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), ModeUrl.STRICT);
				url = urlBuilder.getUrl(line0.get("URL", 0));
			}

			final NoteStyle style = NoteStyle.getNoteStyle(line0.get("STYLE", 0));
			final Display display = diagram.manageVariable(lines.toDisplay());
			final String backcolor0 = line0.get("COLOR", 0);
			Colors colors = Colors.empty().add(ColorType.BACK,
					backcolor0 == null ? null : HColorSet.instance().getColor(backcolor0));
			final Note note = new Note(display, position, style, diagram.getSkinParam().getCurrentStyleBuilder());
			final String stereotypeString = line0.get("STEREO", 0);
			if (stereotypeString != null) {
				final Stereotype stereotype = new Stereotype(stereotypeString);
				colors = colors.applyStereotypeForNote(stereotype, diagram.getSkinParam(), FontParam.NOTE,
						ColorParam.noteBackground, ColorParam.noteBorder);
				note.setStereotype(stereotype);
			}
			note.setUrl(url);
			note.setColors(colors);
			if (m instanceof AbstractMessage) {
				((AbstractMessage) m).setNote(note);
			} else {
				((GroupingLeaf) m).setNote(note);
			}
		}

		return CommandExecutionResult.ok();
	}

}
