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
package net.sourceforge.plantuml.command.note.sequence;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlMode;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.Trim;
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
import net.sourceforge.plantuml.sequencediagram.Note;
import net.sourceforge.plantuml.sequencediagram.NoteStyle;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.ugraphic.color.NoSuchColorException;

public final class FactorySequenceNoteAcrossCommand implements SingleMultiFactoryCommand<SequenceDiagram> {

	private IRegex getRegexConcatMultiLine() {
		return RegexConcat.build(FactorySequenceNoteAcrossCommand.class.getName() + "multi", RegexLeaf.start(), //
				new RegexLeaf("VMERGE", "(/)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STYLE", "(note|hnote|rnote)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<{2}.*\\>{2})?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("ACROSS", "(accross|across)"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), RegexLeaf.end() //
		);
	}

	private IRegex getRegexConcatSingleLine() {
		return RegexConcat.build(FactorySequenceNoteAcrossCommand.class.getName() + "single", RegexLeaf.start(), //
				new RegexLeaf("VMERGE", "(/)?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STYLE", "(note|hnote|rnote)"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("STEREO", "(\\<{2}.*\\>{2})?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("ACROSS", "(accross|across)"), //
				RegexLeaf.spaceZeroOrMore(), //
				color().getRegex(), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf(":"), //
				RegexLeaf.spaceZeroOrMore(), //
				new RegexLeaf("NOTE", "(.*)"), RegexLeaf.end());
	}

	private static ColorParser color() {
		return ColorParser.simpleColor(ColorType.BACK);
	}

	public Command<SequenceDiagram> createSingleLine() {
		return new SingleLineCommand2<SequenceDiagram>(getRegexConcatSingleLine()) {

			@Override
			protected CommandExecutionResult executeArg(final SequenceDiagram system, LineLocation location,
					RegexResult arg) throws NoSuchColorException {
				final BlocLines strings = BlocLines.getWithNewlines(arg.get("NOTE", 0));

				return executeInternal(system, arg, strings);
			}

		};
	}

	public Command<SequenceDiagram> createMultiLine(boolean withBracket) {
		return new CommandMultilines2<SequenceDiagram>(getRegexConcatMultiLine(),
				MultilinesStrategy.KEEP_STARTING_QUOTE, Trim.BOTH) {

			@Override
			public String getPatternEnd() {
				return "^end[%s]?(note|hnote|rnote)$";
			}

			protected CommandExecutionResult executeNow(final SequenceDiagram system, BlocLines lines)
					throws NoSuchColorException {
				final RegexResult line0 = getStartingPattern().matcher(lines.getFirst().getTrimmed().getString());
				lines = lines.subExtract(1, 1);
				lines = lines.removeEmptyColumns();
				return executeInternal(system, line0, lines);
			}

		};
	}

	private CommandExecutionResult executeInternal(SequenceDiagram diagram, final RegexResult line0, BlocLines lines)
			throws NoSuchColorException {
		// final Participant p1 = diagram.getOrCreateParticipant(StringUtils
		// .eventuallyRemoveStartingAndEndingDoubleQuote(line0.get("P1", 0)));
		// final Participant p2 = diagram.getOrCreateParticipant(StringUtils
		// .eventuallyRemoveStartingAndEndingDoubleQuote(line0.get("P2", 0)));

		final String across = line0.get("ACROSS", 0);
		if (across.equalsIgnoreCase("accross")) {
			return CommandExecutionResult.error("Use 'across' instead of 'accross'");
		}

		if (lines.size() > 0) {
			final boolean tryMerge = line0.get("VMERGE", 0) != null;
			final Display display = diagram.manageVariable(lines.toDisplay());
			final Note note = new Note((Participant) null, (Participant) null, display,
					diagram.getSkinParam().getCurrentStyleBuilder());
			Colors colors = color().getColor(line0, diagram.getSkinParam().getIHtmlColorSet());
			final String stereotypeString = line0.get("STEREO", 0);
			if (stereotypeString != null) {
				final Stereotype stereotype = Stereotype.build(stereotypeString);
				colors = colors.applyStereotypeForNote(stereotype, diagram.getSkinParam(), ColorParam.noteBackground,
						ColorParam.noteBorder);
				note.setStereotype(stereotype);
			}
			note.setColors(colors);
			// note.setSpecificColorTOBEREMOVED(ColorType.BACK,
			// diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(line0.get("COLOR",
			// 0)));
			note.setNoteStyle(NoteStyle.getNoteStyle(line0.get("STYLE", 0)));
			if (line0.get("URL", 0) != null) {
				final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), UrlMode.STRICT);
				final Url urlLink = urlBuilder.getUrl(line0.get("URL", 0));
				note.setUrl(urlLink);
			}
			diagram.addNote(note, tryMerge);
		}
		return CommandExecutionResult.ok();
	}

}
