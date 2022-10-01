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
package net.sourceforge.plantuml.command;

import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.ErrorUmlType;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.annotation.HaxeIgnored;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.error.PSystemError;
import net.sourceforge.plantuml.error.PSystemErrorUtils;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.version.IteratorCounter2;

public abstract class PSystemCommandFactory extends PSystemAbstractFactory {

	private List<Command> cmds;

	protected abstract List<Command> createCommands();

	public abstract AbstractPSystem createEmptyDiagram(UmlSource source, Map<String, String> skinParam);

	@HaxeIgnored
	protected PSystemCommandFactory() {
		this(DiagramType.UML);
	}

	protected PSystemCommandFactory(DiagramType type) {
		super(type);
	}

	@Override
	final public Diagram createSystem(UmlSource source, Map<String, String> skinParam) {
		final IteratorCounter2 it = source.iterator2();
		final StringLocated startLine = it.next();
		if (StartUtils.isArobaseStartDiagram(startLine.getString()) == false)
			throw new UnsupportedOperationException();

		if (source.isEmpty()) {
			if (it.hasNext())
				it.next();

			return buildEmptyError(source, startLine.getLocation(), it.getTrace());
		}
		AbstractPSystem sys = createEmptyDiagram(source, skinParam);

		while (it.hasNext()) {
			if (StartUtils.isArobaseEndDiagram(it.peek().getString())) {
				if (sys == null)
					return null;

				final String err = sys.checkFinalError();
				if (err != null) {
					final LineLocation location = it.next().getLocation();
					return buildExecutionError(source, err, location, it.getTrace());
				}
				if (source.getTotalLineCount() == 2) {
					final LineLocation location = it.next().getLocation();
					return buildEmptyError(source, location, it.getTrace());
				}
				sys.makeDiagramReady();
				if (sys.isOk() == false)
					return null;

				return sys;
			}
			sys = executeFewLines(sys, source, it);
			if (sys instanceof PSystemError)
				return sys;

		}
		return sys;

	}

	private AbstractPSystem executeFewLines(AbstractPSystem sys, UmlSource source, final IteratorCounter2 it) {
		final Step step = getCandidate(it);
		if (step == null) {
			final ErrorUml err = new ErrorUml(ErrorUmlType.SYNTAX_ERROR, "Syntax Error?", 0, it.peek().getLocation());
			it.next();
			return PSystemErrorUtils.buildV2(source, err, null, it.getTrace());
		}

		final CommandExecutionResult result = sys.executeCommand(step.command, step.blocLines);
		if (result.isOk() == false) {
			final LineLocation location = ((StringLocated) step.blocLines.getFirst()).getLocation();
			final ErrorUml err = new ErrorUml(ErrorUmlType.EXECUTION_ERROR, result.getError(), result.getScore(),
					location);
			sys = PSystemErrorUtils.buildV2(source, err, result.getDebugLines(), it.getTrace());
		}
		if (result.getNewDiagram() != null)
			sys = result.getNewDiagram();

		return sys;

	}

	static class Step {
		final Command command;
		final BlocLines blocLines;

		Step(Command command, BlocLines blocLines) {
			this.command = command;
			this.blocLines = blocLines;
		}

	}

	private Step getCandidate(final IteratorCounter2 it) {
		final BlocLines single = BlocLines.single(it.peek());
		if (cmds == null)
			cmds = createCommands();

		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(single);
			if (result == CommandControl.OK) {
				it.next();
				return new Step(cmd, single);
			}
			if (result == CommandControl.OK_PARTIAL) {
				final IteratorCounter2 cloned = it.cloneMe();
				final BlocLines lines = isMultilineCommandOk(cloned, cmd);
				if (lines == null)
					continue;

				it.copyStateFrom(cloned);
				return new Step(cmd, lines);
			}
		}
		return null;
	}

	private BlocLines isMultilineCommandOk(IteratorCounter2 it, Command cmd) {
		BlocLines lines = BlocLines.create();
		int nb = 0;
		while (it.hasNext()) {
			lines = addOneSingleLineManageEmbedded2(it, lines);
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.NOT_OK)
				return null;

			if (result == CommandControl.OK)
				return lines;

			nb++;
			if (cmd instanceof CommandDecoratorMultine && nb > ((CommandDecoratorMultine) cmd).getNbMaxLines())
				return null;

		}
		return null;
	}

	private BlocLines addOneSingleLineManageEmbedded2(IteratorCounter2 it, BlocLines lines) {
		final StringLocated linetoBeAdded = it.next();
		lines = lines.add(linetoBeAdded);
		if (linetoBeAdded.getTrimmed().getString().equals("{{")) {
			while (it.hasNext()) {
				final StringLocated s = it.next();
				lines = lines.add(s);
				if (s.getTrimmed().getString().equals("}}"))
					return lines;

			}
		}
		return lines;
	}

}
