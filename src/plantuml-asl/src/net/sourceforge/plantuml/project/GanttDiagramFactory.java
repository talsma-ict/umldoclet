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
package net.sourceforge.plantuml.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandNope;
import net.sourceforge.plantuml.command.PSystemCommandFactory;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.project.command.CommandColorTask;
import net.sourceforge.plantuml.project.command.CommandFootbox;
import net.sourceforge.plantuml.project.command.CommandGanttArrow;
import net.sourceforge.plantuml.project.command.CommandGanttArrow2;
import net.sourceforge.plantuml.project.command.CommandLabelOnColumn;
import net.sourceforge.plantuml.project.command.CommandLanguage;
import net.sourceforge.plantuml.project.command.CommandNoteBottom;
import net.sourceforge.plantuml.project.command.CommandPrintBetween;
import net.sourceforge.plantuml.project.command.CommandPrintScale;
import net.sourceforge.plantuml.project.command.CommandSeparator;
import net.sourceforge.plantuml.project.command.CommandWeekNumberStrategy;
import net.sourceforge.plantuml.project.command.NaturalCommand;
import net.sourceforge.plantuml.project.lang.SentenceAnd;
import net.sourceforge.plantuml.project.lang.SentenceAndAnd;
import net.sourceforge.plantuml.project.lang.SentenceSimple;
import net.sourceforge.plantuml.project.lang.Subject;
import net.sourceforge.plantuml.project.lang.SubjectDayAsDate;
import net.sourceforge.plantuml.project.lang.SubjectDayOfWeek;
import net.sourceforge.plantuml.project.lang.SubjectDaysAsDates;
import net.sourceforge.plantuml.project.lang.SubjectProject;
import net.sourceforge.plantuml.project.lang.SubjectResource;
import net.sourceforge.plantuml.project.lang.SubjectTask;
import net.sourceforge.plantuml.project.lang.SubjectToday;
import net.sourceforge.plantuml.style.CommandStyleImport;
import net.sourceforge.plantuml.style.CommandStyleMultilinesCSS;

public class GanttDiagramFactory extends PSystemCommandFactory {

	static private final List<Subject> subjects() {
		return Arrays.<Subject>asList(new SubjectTask(), new SubjectProject(), new SubjectDayOfWeek(),
				new SubjectDayAsDate(), new SubjectDaysAsDates(), new SubjectResource(), new SubjectToday());
	}

	public GanttDiagramFactory(DiagramType type) {
		super(type);
	}

	@Override
	protected List<Command> createCommands() {
		final List<Command> cmds = new ArrayList<>();
		addTitleCommands(cmds);
		addCommonCommands2(cmds);

		cmds.add(new CommandStyleMultilinesCSS());
		cmds.add(new CommandStyleImport());

		// addCommonCommands(cmds);
		cmds.add(new CommandNope());
		// cmds.add(new CommandComment());
		// cmds.add(new CommandMultilinesComment());
		cmds.addAll(getLanguageCommands());
		cmds.add(new CommandGanttArrow());
		cmds.add(new CommandGanttArrow2());
		cmds.add(new CommandColorTask());
		cmds.add(new CommandSeparator());
		cmds.add(new CommandWeekNumberStrategy());

		cmds.add(new CommandLanguage());
		cmds.add(new CommandPrintScale());
		cmds.add(new CommandPrintBetween());
		cmds.add(new CommandNoteBottom());
		cmds.add(new CommandFootbox());
		cmds.add(new CommandLabelOnColumn());

		// cmds.add(new CommandScaleWidthAndHeight());
		// cmds.add(new CommandScaleWidthOrHeight());
		// cmds.add(new CommandScaleMaxWidth());
		// cmds.add(new CommandScaleMaxHeight());
		// cmds.add(new CommandScaleMaxWidthAndHeight());

		return cmds;
	}

	static private final Collection<Command> cache = new ArrayList<>();

	public static void clearCache() {
		cache.clear();
	}

	private static Collection<Command> getLanguageCommands() {
		// Useless synchronized now
		synchronized (cache) {
			if (cache.size() == 0) {

				for (Subject subject : subjects()) {
					for (SentenceSimple sentenceA : subject.getSentences()) {
						cache.add(NaturalCommand.create(sentenceA));
						for (SentenceSimple sentenceB : subject.getSentences()) {
							if (sentenceA.getVerbPattern().equals(sentenceB.getVerbPattern()) == false) {
								cache.add(NaturalCommand.create(new SentenceAnd(sentenceA, sentenceB)));
							}
						}
					}
				}

				for (Subject subject : subjects()) {
					for (SentenceSimple sentenceA : subject.getSentences()) {
						for (SentenceSimple sentenceB : subject.getSentences()) {
							for (SentenceSimple sentenceC : subject.getSentences()) {
								if (sentenceA.getVerbPattern().equals(sentenceB.getVerbPattern()) == false
										&& sentenceA.getVerbPattern().equals(sentenceC.getVerbPattern()) == false
										&& sentenceC.getVerbPattern().equals(sentenceB.getVerbPattern()) == false) {
									cache.add(
											NaturalCommand.create(new SentenceAndAnd(sentenceA, sentenceB, sentenceC)));
								}
							}
						}
					}
				}
			}
		}
		return cache;
	}

	@Override
	public GanttDiagram createEmptyDiagram(UmlSource source, ISkinSimple skinParam) {
		return new GanttDiagram(source);
	}

}
