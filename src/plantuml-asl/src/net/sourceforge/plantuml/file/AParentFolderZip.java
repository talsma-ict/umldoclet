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
package net.sourceforge.plantuml.file;

import java.io.IOException;

import net.sourceforge.plantuml.security.SFile;

public class AParentFolderZip implements AParentFolder {

	private final SFile zipFile;
	private final String parent;

	@Override
	public String toString() {
		return "AParentFolderZip::" + zipFile + " " + parent;
	}

	public AParentFolderZip(SFile zipFile, String entry) {
		this.zipFile = zipFile;
		final int idx = entry.lastIndexOf('/');
		if (idx == -1) {
			parent = "";
		} else {
			parent = entry.substring(0, idx + 1);
		}
	}

	public AFile getAFile(String nameOrPath) throws IOException {
		return new AFileZipEntry(zipFile, merge(parent + nameOrPath));
	}

	String merge(String full) {
		// full = full.replaceFirst("\\.", "Z");
		while (true) {
			int len = full.length();
			full = full.replaceFirst("[^/]+/\\.\\./", "");
			if (full.length() == len) {
				return full;
			}
		}
	}

}
