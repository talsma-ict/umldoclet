/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import java.util.Objects;

import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.utils.BlocLines;
import net.sourceforge.plantuml.utils.Log;
import net.sourceforge.plantuml.version.Version;

public class ProtectedCommand<S extends Diagram> implements Command<S> {

	private final Command<S> cmd;

	public ProtectedCommand(Command<S> cmd) {
		this.cmd = Objects.requireNonNull(cmd);
	}

	public CommandExecutionResult execute(S system, BlocLines lines) {
		try {
			// WasmLog.log("...running " + cmd.getClass().getName() + " ...");
			final CommandExecutionResult result = cmd.execute(system, lines);
			// if (result.isOk()) {
			// // TRACECOMMAND
			// System.err.println("CMD = " + cmd.getClass());
			// }
			return result;
		} catch (Throwable t) {
			Log.error("Error " + t);
			Logme.error(t);
			String msg = "You should send a mail to plantuml@gmail.com or post to https://plantuml.com/qa with this log (V"
					+ Version.versionString() + ")";
			Log.error(msg);
			msg += " " + t.toString();
			return CommandExecutionResult.error(msg, t);
		}
	}

	public CommandControl isValid(BlocLines lines) {
		return cmd.isValid(lines);
	}

	public String[] getDescription() {
		return cmd.getDescription();
	}

}
