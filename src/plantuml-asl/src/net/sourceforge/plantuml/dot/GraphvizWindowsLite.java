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

import java.io.File;

import net.sourceforge.plantuml.style.ISkinParam;
import net.sourceforge.plantuml.windowsdot.WindowsDotArchive;

class GraphvizWindowsLite extends AbstractGraphviz {
	// ::remove file when __CORE__

	static private File specificDotExe;

	@Override
	protected boolean findExecutableOnPath() {
		return false;
	}

	@Override
	protected File specificDotExe() {
		synchronized (GraphvizWindowsLite.class) {
			if (specificDotExe == null)
				specificDotExe = WindowsDotArchive.getInstance().getWindowsExeLite();

			return specificDotExe;
		}
	}

	public boolean graphviz244onWindows() {
		return false;
	}

	GraphvizWindowsLite(ISkinParam skinParam, String dotString, String... type) {
		super(skinParam, dotString, type);
	}

	@Override
	protected String getExeName() {
		return "dot.exe";
	}

}
