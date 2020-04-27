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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FileSystem {

	private final static FileSystem singleton = new FileSystem();

	private final ThreadLocal<File> currentDir = new ThreadLocal<File>();

	private FileSystem() {
		reset();
	}

	public static FileSystem getInstance() {
		return singleton;
	}

	public void setCurrentDir(File dir) {
		// if (dir == null) {
		// throw new IllegalArgumentException();
		// }
		Log.info("Setting current dir: " + dir);
		this.currentDir.set(dir);
	}

	public File getCurrentDir() {
		return this.currentDir.get();
	}

	public File getFile(String nameOrPath) throws IOException {
		if (isAbsolute(nameOrPath)) {
			return new File(nameOrPath).getCanonicalFile();
		}
		final File dir = currentDir.get();
		File filecurrent = null;
		if (dir != null) {
			filecurrent = new File(dir.getAbsoluteFile(), nameOrPath);
			if (filecurrent.exists()) {
				return filecurrent.getCanonicalFile();

			}
		}
		for (File d : getPath("plantuml.include.path", true)) {
			if (d.isDirectory()) {
				final File file = new File(d, nameOrPath);
				if (file.exists()) {
					return file.getCanonicalFile();
				}
			}
		}
		for (File d : getPath("java.class.path", true)) {
			if (d.isDirectory()) {
				final File file = new File(d, nameOrPath);
				if (file.exists()) {
					return file.getCanonicalFile();
				}
			}
		}
		if (dir == null) {
			assert filecurrent == null;
			return new File(nameOrPath).getCanonicalFile();
		}
		assert filecurrent != null;
		return filecurrent;
	}

	public static List<File> getPath(String prop, boolean onlyDir) {
		final List<File> result = new ArrayList<File>();
		String paths = System.getProperty(prop);
		if (paths == null) {
			return result;
		}
		paths = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(paths);
		final StringTokenizer st = new StringTokenizer(paths, System.getProperty("path.separator"));
		while (st.hasMoreTokens()) {
			final File f = new File(st.nextToken());
			if (f.exists() && (onlyDir == false || f.isDirectory())) {
				result.add(f);
			}
		}
		return result;
	}

	private boolean isAbsolute(String nameOrPath) {
		final File f = new File(nameOrPath);
		return f.isAbsolute();
	}

	public void reset() {
		setCurrentDir(new File("."));
	}

}
