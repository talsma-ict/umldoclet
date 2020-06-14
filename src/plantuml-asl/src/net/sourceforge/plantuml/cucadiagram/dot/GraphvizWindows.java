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
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.StringUtils;

class GraphvizWindows extends AbstractGraphviz {

	@Override
	protected File specificDotExe() {
		final File result = searchInDir(new File("c:/Program Files"));
		if (result != null) {
			return result;
		}
		final File result86 = searchInDir(new File("c:/Program Files (x86)"));
		if (result86 != null) {
			return result86;
		}
		final File resultEclipse = searchInDir(new File("c:/eclipse/graphviz"));
		if (resultEclipse != null) {
			return resultEclipse;
		}
		return null;
	}

	private static File searchInDir(final File programFile) {
		if (programFile.exists() == false || programFile.isDirectory() == false) {
			return null;
		}
		final List<File> dots = new ArrayList<File>();
		for (File f : programFile.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() && StringUtils.goLowerCase(pathname.getName()).startsWith("graphviz");
			}
		})) {
			final File result = new File(new File(f, "bin"), "dot.exe");
			if (result.exists() && result.canRead()) {
				dots.add(result.getAbsoluteFile());
			}
		}
		return higherVersion(dots);
	}

	static File higherVersion(List<File> dots) {
		if (dots.size() == 0) {
			return null;
		}
		Collections.sort(dots, Collections.reverseOrder());
		return dots.get(0);
	}

	GraphvizWindows(ISkinParam skinParam, String dotString, String... type) {
		super(skinParam, dotString, type);
	}
	
	@Override
	protected String getExeName() {
		return "dot.exe";
	}


}
