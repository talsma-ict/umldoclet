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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AFileZipEntry implements AFile {

	private final File zipFile;
	private final String entry;

	public AFileZipEntry(File file, String entry) {
		this.zipFile = file;
		this.entry = entry;
	}

	@Override
	public String toString() {
		return "AFileZipEntry::" + zipFile + " " + entry;
	}

	public InputStream open() throws IOException {
		final ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
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
		throw new IOException();
	}

	public boolean isOk() {
		if (zipFile.exists() && zipFile.isDirectory() == false) {
			InputStream is = null;
			try {
				is = open();
				return true;
			} catch (IOException e) {
				// e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
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

	public File getUnderlyingFile() {
		return zipFile;
	}

	public File getSystemFolder() throws IOException {
		return zipFile.getParentFile().getCanonicalFile();
	}

}
