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

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sourceforge.plantuml.preproc.Defines;

public class SourceFileReaderCopyCat extends SourceFileReaderAbstract implements ISourceFileReader {

	public SourceFileReaderCopyCat(Defines defines, final File file, File outputDirectory, List<String> config,
			String charset, FileFormatOption fileFormatOption) throws IOException {
		super(file, fileFormatOption, defines, config, charset);
		final String path = file.getParentFile().getPath();
		// System.err.println("SourceFileReaderCopyCat::path=" + path);
		// System.err.println("SourceFileReaderCopyCat::outputDirectory=" +
		// outputDirectory);
		this.outputDirectory = new File(outputDirectory, path).getAbsoluteFile();
		if (outputDirectory.exists() == false) {
			outputDirectory.mkdirs();
		}
		// System.err.println("SourceFileReaderCopyCat=" +
		// this.outputDirectory.getPath() + " "
		// + this.outputDirectory.getAbsolutePath());
	}

	@Override
	protected SuggestedFile getSuggestedFile(BlockUml blockUml) {
		final String newName = blockUml.getFileOrDirname();
		SuggestedFile suggested = null;
		if (newName == null) {
			suggested = SuggestedFile.fromOutputFile(new File(outputDirectory, file.getName()),
					fileFormatOption.getFileFormat(), cpt++);
		} else {
			suggested = SuggestedFile.fromOutputFile(new File(outputDirectory, newName),
					fileFormatOption.getFileFormat(), cpt++);
		}
		// System.err.println("SourceFileReaderCopyCat::suggested=" + suggested);
		suggested.getParentFile().mkdirs();
		return suggested;
	}

}
