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
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.StringUtils;

class GraphvizWindows extends AbstractGraphviz {

	static private File specificDotExe;

	@Override
	protected File specificDotExe() {
		synchronized (GraphvizWindows.class) {
			if (specificDotExe == null) {
				specificDotExe = specificDotExeSlow();
			}
			return specificDotExe;
		}
	}

	public boolean graphviz244onWindows() {
		try {
			return GraphvizUtils.getDotVersion() == 244;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private File specificDotExeSlow() {
		for (File tmp : new File("c:/").listFiles(new FileFilter() {
			public boolean accept(java.io.File pathname) {
				return pathname.isDirectory() && pathname.canRead();
			}
		})) {
			final File result = searchInDir(tmp);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	private static File searchInDir(final File dir) {
		if (dir.exists() == false || dir.isDirectory() == false) {
			return null;
		}
		final List<File> dots = new ArrayList<File>();
		final File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(java.io.File pathname) {
				return pathname.isDirectory() && StringUtils.goLowerCase(pathname.getName()).startsWith("graphviz");
			}
		});
		if (files == null) {
			return null;
		}
		for (File f : files) {
			final File result = new File(new File(f, "bin"), "dot.exe");
			if (result.exists() && result.canRead()) {
				dots.add(result.getAbsoluteFile());
			}
			final File result2 = new File(new File(f, "release/bin"), "dot.exe");
			if (result2.exists() && result2.canRead()) {
				dots.add(result2.getAbsoluteFile());
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
