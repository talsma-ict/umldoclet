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
import java.io.InputStream;

import net.sourceforge.plantuml.security.SFile;

public class AFileRegular implements AFile {

	private final SFile file;

	@Override
	public String toString() {
		return "AFileRegular::" + file.getAbsolutePath();
	}

	public AFileRegular(SFile file) {
		this.file = file;
	}

	public InputStream openFile() {
		return file.openFile();
	}

	public boolean isOk() {
		return file.exists() && file.isDirectory() == false;
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AFileRegular == false) {
			return false;
		}
		return this.file.equals(((AFileRegular) obj).file);
	}

	public AParentFolder getParentFile() {
		return new AParentFolderRegular(file.getParentFile());
	}

	public SFile getUnderlyingFile() {
		return file;
	}

	public SFile getSystemFolder() throws IOException {
		return file.getParentFile().getCanonicalFile();
	}

}
