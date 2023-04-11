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
package net.sourceforge.plantuml;

import java.io.IOException;
import java.io.OutputStream;

import net.atmp.ImageBuilder;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.shape.UDrawable;

// This class doesnt feel like a wonderful idea, just a stepping stone towards something
public abstract class PlainDiagram extends AbstractPSystem {
	// ::remove file when __HAXE__

	public PlainDiagram(UmlSource source) {
		super(source);
	}

	@Override
	public ImageBuilder createImageBuilder(FileFormatOption fileFormatOption) throws IOException {
		return super.createImageBuilder(fileFormatOption).margin(getDefaultMargins())
				.metadata(fileFormatOption.isWithMetadata() ? getMetadata() : null).seed(seed());
	}

	@Override
	protected ImageData exportDiagramNow(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {

		final UDrawable rootDrawable = getRootDrawable(fileFormatOption);
		return createImageBuilder(fileFormatOption).drawable(rootDrawable).write(os);
	}

	@Override
	public void exportDiagramGraphic(UGraphic ug) {
		final FileFormatOption option = new FileFormatOption(FileFormat.PNG);
		try {
			final UDrawable rootDrawable = getRootDrawable(option);
			rootDrawable.drawU(ug);
		} catch (IOException e) {
			e.printStackTrace();
			super.exportDiagramGraphic(ug);
		}
	}

	protected abstract UDrawable getRootDrawable(FileFormatOption fileFormatOption) throws IOException;
}
