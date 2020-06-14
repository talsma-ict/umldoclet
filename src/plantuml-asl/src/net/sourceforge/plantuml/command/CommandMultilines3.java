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
package net.sourceforge.plantuml.command;

import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.core.Diagram;

public abstract class CommandMultilines3<S extends Diagram> implements Command<S> {

	private final IRegex starting;

	private final MultilinesStrategy strategy;
	
	public CommandMultilines3(IRegex patternStart, MultilinesStrategy strategy) {
		if (patternStart.getPattern().startsWith("^") == false || patternStart.getPattern().endsWith("$") == false) {
			throw new IllegalArgumentException("Bad pattern " + patternStart.getPattern());
		}
		this.strategy = strategy;
		this.starting = patternStart;
	}

	public abstract RegexConcat getPatternEnd2();

	public String[] getDescription() {
		return new String[] { "START: " + starting.getPattern(), "END: " + getPatternEnd2().getPattern() };
	}

	final public CommandControl isValid(BlocLines lines) {
		lines = lines.cleanList2(strategy);
		if (isCommandForbidden()) {
			return CommandControl.NOT_OK;
		}
		final StringLocated first = lines.getFirst499();
		if (first == null) {
			return CommandControl.NOT_OK;
		}
		final boolean result1 = starting.match(first.getTrimmed());
		if (result1 == false) {
			return CommandControl.NOT_OK;
		}
		if (lines.size() == 1) {
			return CommandControl.OK_PARTIAL;
		}

		final StringLocated potentialLast = lines.getLast499().getTrimmed();
		final boolean m1 = getPatternEnd2().match(potentialLast);
		if (m1 == false) {
			return CommandControl.OK_PARTIAL;
		}

		actionIfCommandValid();
		return CommandControl.OK;
	}

	public final CommandExecutionResult execute(S system, BlocLines lines) {
		lines = lines.cleanList2(strategy);
		return executeNow(system, lines);
	}

	protected abstract CommandExecutionResult executeNow(S system, BlocLines lines);

	protected boolean isCommandForbidden() {
		return false;
	}

	protected void actionIfCommandValid() {
	}

	protected final IRegex getStartingPattern() {
		return starting;
	}

}
