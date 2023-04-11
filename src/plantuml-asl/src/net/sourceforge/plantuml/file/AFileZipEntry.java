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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.security.SFile;

public class AFileZipEntry implements AFile {
    // ::remove folder when __HAXE__

	private final SFile zipFile;
	private final String entry;

	public AFileZipEntry(SFile file, String entry) {
		this.zipFile = file;
		this.entry = entry;
	}

	@Override
	public String toString() {
		return "AFileZipEntry::" + zipFile.getAbsolutePath() + " " + entry;
	}

	public InputStream openFile() {
		final InputStream tmp = zipFile.openFile();
		if (tmp != null)
			try {
				final ZipInputStream zis = new ZipInputStream(tmp);
				ZipEntry ze = zis.getNextEntry();

				while (ze != null) {
					final String fileName = ze.getName();
					if (ze.isDirectory()) {
					} else if (fileName.trim().equalsIgnoreCase(entry.trim())) {
						return zis;
					}
					ze = zis.getNextEntry();
				}
				zis.closeEntry();
				zis.close();
			} catch (IOException e) {
				Logme.error(e);
			}
		return null;
	}

	public boolean isOk() {
		if (zipFile.exists() && zipFile.isDirectory() == false) {
			final InputStream is = openFile();
			if (is != null) {
				try {
					is.close();
					return true;
				} catch (IOException e) {
					Logme.error(e);
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return zipFile.hashCode() + entry.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AFileZipEntry == false) {
			return false;
		}
		final AFileZipEntry other = (AFileZipEntry) obj;
		return this.zipFile.equals(other.zipFile) && this.entry.equals(other.entry);
	}

	public AParentFolder getParentFile() {
		return new AParentFolderZip(zipFile, entry);
	}

	public SFile getUnderlyingFile() {
		return zipFile;
	}

	public SFile getSystemFolder() throws IOException {
		return zipFile.getParentFile().getCanonicalFile();
	}

}
