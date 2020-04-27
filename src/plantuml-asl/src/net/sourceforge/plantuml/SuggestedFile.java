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

public class SuggestedFile {

	private final FileFormat fileFormat;
	private final int initialCpt;
	private final File outputFile;

	private SuggestedFile(File outputFile, FileFormat fileFormat, int initialCpt) {
		if (outputFile.getName().endsWith(fileFormat.getFileSuffix())) {
			throw new IllegalArgumentException();
		}
		this.outputFile = outputFile;
		this.fileFormat = fileFormat;
		this.initialCpt = initialCpt;
	}

	public SuggestedFile withPreprocFormat() {
		return new SuggestedFile(outputFile, FileFormat.PREPROC, initialCpt);
	}

	@Override
	public String toString() {
		return outputFile.getAbsolutePath() + "[" + initialCpt + "]";
	}

	public static SuggestedFile fromOutputFile(File outputFile, FileFormat fileFormat) {
		return fromOutputFile(outputFile, fileFormat, 0);
	}

	public File getParentFile() {
		return outputFile.getParentFile();
	}

	public String getName() {
		return outputFile.getName();
	}

	public File getFile(int cpt) {
		final String newName = fileFormat.changeName(outputFile.getName(), initialCpt + cpt);
		return new File(outputFile.getParentFile(), newName);
	}

	public static SuggestedFile fromOutputFile(File outputFile, FileFormat fileFormat, int initialCpt) {
		return new SuggestedFile(outputFile, fileFormat, initialCpt);
	}

	public File getTmpFile() {
		return new File(getParentFile(), getName() + ".tmp");
	}

}
