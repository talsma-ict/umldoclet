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
package net.sourceforge.plantuml;

import java.io.File;
import java.io.PrintStream;

import net.sourceforge.plantuml.command.PSystemAbstractFactory;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.eggs.PSystemWelcome;
import net.sourceforge.plantuml.error.PSystemError;

public class StdrptV2 implements Stdrpt {

	public void finalMessage() {
	}

	public void finalMessage(ErrorStatus error) {
	}

	public void errorLine(int lineError, File file) {
	}

	public void printInfo(final PrintStream output, Diagram sys) {
		if (sys instanceof PSystemWelcome) {
			sys = null;
		}
		if (sys == null || sys instanceof PSystemError) {
			out(output, (PSystemError) sys);
		}
	}

	private void out(final PrintStream output, final PSystemError err) {
		final StringBuilder line = new StringBuilder();
		if (empty(err)) {
		} else {
			line.append(err.getLineLocation().getDescription());
			line.append(":");
			line.append(err.getLineLocation().getPosition() + 1);
			line.append(":");
			line.append("error");
			line.append(":");
			for (ErrorUml er : err.getErrorsUml()) {
				line.append(er.getError());
			}
		}
		output.println(line);
		output.flush();
	}

	private boolean empty(final PSystemError err) {
		if (err == null) {
			return true;
		}
		for (ErrorUml er : err.getErrorsUml()) {
			if (PSystemAbstractFactory.EMPTY_DESCRIPTION.equals(er.getError()))
				return true;
		}
		return false;
	}

}
