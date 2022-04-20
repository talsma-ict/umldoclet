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

import net.sourceforge.plantuml.security.SFile;

public class BaseFile {

	private final String basename;
	private final SFile basedir;

	public BaseFile(SFile file) {
		if (file == null) {
			this.basedir = null;
			this.basename = null;
		} else {
			this.basedir = file.getParentFile();
			this.basename = extractBasename(file.getName());
		}
	}

	private static String extractBasename(String name) {
		final int idx = name.lastIndexOf('.');
		if (idx == -1) {
			return name;
		}
		return name.substring(0, idx);
	}

	@Override
	public String toString() {
		if (basedir == null || basename == null) {
			return "(DEFAULT)";
		}
		return basedir + " " + basename;
	}

	public String getBasename() {
		return basename;
	}

	public SFile getBasedir() {
		return basedir;
	}

	public SFile getTraceFile(String tail) {
		if (basedir == null || basename == null) {
			return new SFile(tail);
		}
		return basedir.file(basename + "_" + tail);
	}

}
