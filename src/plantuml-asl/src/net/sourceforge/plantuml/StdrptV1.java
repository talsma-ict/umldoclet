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

public class StdrptV1 implements Stdrpt {

	public void printInfo(final PrintStream output, Diagram sys) {
		if (sys instanceof PSystemWelcome) {
			sys = null;
		}
		if (sys == null || sys instanceof PSystemError) {
			out(output, (PSystemError) sys);
		}
	}

	public void errorLine(int lineError, File file) {
		Log.error("Error line " + lineError + " in file: " + file.getPath());
	}

	private void out(final PrintStream output, final PSystemError err) {
		output.println("protocolVersion=1");
		if (empty(err)) {
			output.println("status=NO_DATA");
		} else {
			output.println("status=ERROR");
			output.println("lineNumber=" + err.getLineLocation().getPosition());
			for (ErrorUml er : err.getErrorsUml()) {
				output.println("label=" + er.getError());
			}
		}
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

	public void finalMessage(ErrorStatus error) {
		if (error.hasError()) {
			Log.error("Some diagram description contains errors");
		}
		if (error.isNoData()) {
			Log.error("No diagram found");
		}
	}

}
