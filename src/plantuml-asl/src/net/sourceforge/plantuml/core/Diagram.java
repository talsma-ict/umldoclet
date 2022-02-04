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
package net.sourceforge.plantuml.core;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.api.ApiStable;
import net.sourceforge.plantuml.cucadiagram.Display;

/**
 * Represents a single diagram. A Diagram could be a UML (sequence diagram,
 * class diagram...) or an non-UML diagram.
 * 
 * @author Arnaud Roques
 */
@ApiStable
public interface Diagram {

	/**
	 * Export the diagram as an image to some format. Note that a diagram could be
	 * drawn as several images (think about <code>new page</code> for sequence
	 * diagram for example).
	 * 
	 * @param os         where to write the image
	 * @param num        usually 0 (index of the image to be exported for this
	 *                   diagram).
	 * @param fileFormat file format to use
	 * 
	 * @return a description of the generated image
	 * 
	 * @throws IOException
	 */
	ImageData exportDiagram(OutputStream os, int num, FileFormatOption fileFormat) throws IOException;

	/**
	 * Number of images in this diagram (usually, 1)
	 * 
	 * @return usually 1
	 */
	int getNbImages();

	int getSplitPagesHorizontal();

	int getSplitPagesVertical();

	DiagramDescription getDescription();

	String getMetadata();

	String getWarningOrError();

	/**
	 * The original source of the diagram
	 */
	UmlSource getSource();

	/**
	 * Check if the Diagram have some links.
	 */
	public boolean hasUrl();

	public Display getTitleDisplay();

}
