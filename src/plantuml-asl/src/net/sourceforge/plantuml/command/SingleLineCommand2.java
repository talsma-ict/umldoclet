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

import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.error.PSystemError;

public abstract class SingleLineCommand2<S extends Diagram> implements Command<S> {

	private final IRegex pattern;
	private final boolean doTrim;
	
	public SingleLineCommand2(IRegex pattern) {
		this(true, pattern);
	}

	public SingleLineCommand2(boolean doTrim, IRegex pattern) {
		this.doTrim = doTrim;
		if (pattern == null) {
			throw new IllegalArgumentException();
		}
		this.pattern = pattern;
	}

	public boolean syntaxWithFinalBracket() {
		return false;
	}

	public String[] getDescription() {
		return new String[] { pattern.getClass().getName() };
	}

	private String myTrim(StringLocated s) {
		if (doTrim) {
			return s.getTrimmed().getString();
		}
		return s.getString();
	}

	private StringLocated myTrim2(StringLocated s) {
		if (doTrim) {
			return s.getTrimmed();
		}
		return s;
	}

	final public CommandControl isValid(BlocLines lines) {
		if (lines.size() == 2 && syntaxWithFinalBracket()) {
			return isValidBracket(lines);
		}
		if (lines.size() != 1) {
			return CommandControl.NOT_OK;
		}
		if (isCommandForbidden()) {
			return CommandControl.NOT_OK;
		}
		final StringLocated line2 = myTrim2(lines.getFirst());
		if (syntaxWithFinalBracket() && line2.getString().endsWith("{") == false) {
			final String vline = lines.getAt(0).getString() + " {";
			if (isValid(BlocLines.singleString(vline)) == CommandControl.OK) {
				return CommandControl.OK_PARTIAL;
			}
			return CommandControl.NOT_OK;
		}
		final boolean result = pattern.match(line2);
		if (result) {
			actionIfCommandValid();
		}
		return result ? CommandControl.OK : CommandControl.NOT_OK;
	}

	private CommandControl isValidBracket(BlocLines lines) {
		assert lines.size() == 2;
		assert syntaxWithFinalBracket();
		if (myTrim(lines.getAt(1)).equals("{") == false) {
			return CommandControl.NOT_OK;
		}
		final String vline = lines.getAt(0).getString() + " {";
		return isValid(BlocLines.singleString(vline));
	}

	protected boolean isCommandForbidden() {
		return false;
	}

	protected void actionIfCommandValid() {
	}

	public final CommandExecutionResult execute(S system, BlocLines lines) {
		if (syntaxWithFinalBracket() && lines.size() == 2) {
			assert myTrim(lines.getAt(1)).equals("{");
			lines = BlocLines.singleString(lines.getFirst().getString() + " {");
		}
		if (lines.size() != 1) {
			throw new IllegalArgumentException();
		}
		final StringLocated first = lines.getFirst();
		final String line = myTrim(first);
		if (isForbidden(line)) {
			return CommandExecutionResult.error("Syntax error: " + line);
		}

		final RegexResult arg = pattern.matcher(line);
		if (arg == null) {
			return CommandExecutionResult.error("Cannot parse line " + line);
		}
		if (system instanceof PSystemError) {
			return CommandExecutionResult.error("PSystemError cannot be cast");
		}
		// System.err.println("lines="+lines);
		// System.err.println("pattern="+pattern.getPattern());
		return executeArg(system, first.getLocation(), arg);
	}

	protected boolean isForbidden(CharSequence line) {
		return false;
	}

	protected abstract CommandExecutionResult executeArg(S system, LineLocation location, RegexResult arg);

}
