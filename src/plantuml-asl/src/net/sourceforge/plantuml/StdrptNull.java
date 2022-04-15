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
package net.sourceforge.plantuml;

import java.io.File;
import java.io.PrintStream;

import net.sourceforge.plantuml.core.Diagram;

public class StdrptNull implements Stdrpt {

	public void printInfo(final PrintStream output, final Diagram sys) {
	}

	public void finalMessage(ErrorStatus error) {
		if (error.hasError()) {
			Log.error("Some diagram description contains errors");
		}
		if (error.isNoData()) {
			Log.error("No diagram found");
		}
	}

	public void errorLine(int lineError, File file) {
		Log.error("Error line " + (lineError + 1) + " in file: " + file.getPath());
	}

}
