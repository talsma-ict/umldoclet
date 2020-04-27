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
package net.sourceforge.plantuml.command;

import net.sourceforge.plantuml.core.Diagram;

public class CommandDecoratorMultine<D extends Diagram> implements Command<D> {

	private final SingleLineCommand2<D> cmd;
	private final boolean removeEmptyColumn;
	private final int nbMaxLines;
	
	public CommandDecoratorMultine(SingleLineCommand2<D> cmd, int nbMaxLines) {
		this(cmd, false, nbMaxLines);
	}

	public CommandDecoratorMultine(SingleLineCommand2<D> cmd, boolean removeEmptyColumn, int nbMaxLines) {
		this.cmd = cmd;
		this.removeEmptyColumn = removeEmptyColumn;
		this.nbMaxLines = nbMaxLines;
	}

	public CommandExecutionResult execute(D diagram, BlocLines lines) {
		if (removeEmptyColumn) {
			lines = lines.removeEmptyColumns();
		}
		lines = lines.toSingleLineWithHiddenNewLine();
		return cmd.execute(diagram, lines);
	}

	public CommandControl isValid(BlocLines lines) {
		if (cmd.isCommandForbidden()) {
			return CommandControl.NOT_OK;
		}
		lines = lines.toSingleLineWithHiddenNewLine();
		if (cmd.isForbidden(lines.getFirst499().getString())) {
			return CommandControl.NOT_OK;
		}
		final CommandControl tmp = cmd.isValid(lines);
		if (tmp == CommandControl.OK_PARTIAL) {
			throw new IllegalStateException();
		}
		if (tmp == CommandControl.OK) {
			return tmp;
		}
		return CommandControl.OK_PARTIAL;
	}

	public String[] getDescription() {
		return cmd.getDescription();
	}

	public int getNbMaxLines() {
		return nbMaxLines;
	}

}
