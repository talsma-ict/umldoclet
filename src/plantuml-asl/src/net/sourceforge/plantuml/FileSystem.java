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

import java.io.IOException;

import net.sourceforge.plantuml.security.SFile;
import net.sourceforge.plantuml.security.SecurityUtils;

public class FileSystem {

	private final static FileSystem singleton = new FileSystem();

	private ThreadLocal<String> currentDir = new ThreadLocal<>();

	private FileSystem() {
		reset();
	}

	public static FileSystem getInstance() {
		return singleton;
	}

	public void setCurrentDir(SFile dir) {
		if (dir == null) {
			this.currentDir.set(null);
		} else {
			Log.info("Setting current dir: " + dir.getAbsolutePath());
			this.currentDir.set(dir.getAbsolutePath());
		}
	}

	public SFile getCurrentDir() {
		final String path = this.currentDir.get();
		if (path != null) {
			return new SFile(path);
		}
		return null;
	}

	public SFile getFile(String nameOrPath) throws IOException {
		if (isAbsolute(nameOrPath)) {
			final SFile result = new SFile(nameOrPath);
			Log.info("Trying " + result.getAbsolutePath());
			return result.getCanonicalFile();
		}

		final SFile dir = getCurrentDir();
		SFile filecurrent = null;
		if (dir != null) {
			filecurrent = dir.getAbsoluteFile().file(nameOrPath);
			Log.info("Current dir is " + dir.getAbsolutePath() + " so trying " + filecurrent.getAbsolutePath());
			if (filecurrent.exists())
				return filecurrent.getCanonicalFile();

		}
		for (SFile d : SecurityUtils.getPath(SecurityUtils.PATHS_INCLUDES)) {
			assert d.isDirectory();
			final SFile file = d.file(nameOrPath);
			if (file.exists())
				return file.getCanonicalFile();
		}
		for (SFile d : SecurityUtils.getPath(SecurityUtils.PATHS_CLASSES)) {
			assert d.isDirectory();
			final SFile file = d.file(nameOrPath);
			if (file.exists())
				return file.getCanonicalFile();

		}
		if (dir == null) {
			assert filecurrent == null;
			return new SFile(nameOrPath).getCanonicalFile();
		}
		assert filecurrent != null;
		return filecurrent;
	}

	private boolean isAbsolute(String nameOrPath) {
		final SFile f = new SFile(nameOrPath);
		return f.isAbsolute();
	}

	public void reset() {
		setCurrentDir(new SFile("."));
	}

}
