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
package net.sourceforge.plantuml.dot;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import net.sourceforge.plantuml.security.SFile;

public class DebugTrace {
	// ::remove file when __CORE__

	private static final SFile out = new SFile("debug" + System.currentTimeMillis() + ".txt");

	private static PrintWriter pw;

	private synchronized static PrintWriter getPrintWriter() {
		if (pw == null) {
			try {
				pw = out.createPrintWriter();
			} catch (FileNotFoundException e) {

			}
		}
		return pw;
	}

	public synchronized static void DEBUG(String s) {
		final PrintWriter pw = getPrintWriter();
		pw.println(s);
		pw.flush();
	}

	public synchronized static void DEBUG(String s, Throwable t) {
		DEBUG(s);
		t.printStackTrace(pw);
		pw.flush();
	}

}
