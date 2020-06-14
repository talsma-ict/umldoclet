/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.eggs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.graphic.GraphicPosition;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.version.PSystemVersion;

public class PSystemWelcome extends AbstractPSystem {

	private final List<String> strings = new ArrayList<String>();
	private final GraphicPosition position;

	public PSystemWelcome(GraphicPosition position) {
		this.position = position;
		strings.add("<b>Welcome to PlantUML!");
		strings.add(" ");
		strings.add("If you use this software, you accept its license.");
		strings.add("(details by typing \"\"license\"\" keyword)");
		strings.add(" ");
		strings.add("You can start with a simple UML Diagram like:");
		strings.add(" ");
		strings.add("\"\"Bob->Alice: Hello\"\"");
		strings.add(" ");
		strings.add("Or");
		strings.add(" ");
		strings.add("\"\"class Example\"\"");
		strings.add(" ");
		strings.add("You will find more information about PlantUML syntax on <u>http://plantuml.com</u>");
		if (position == GraphicPosition.BACKGROUND_CORNER_BOTTOM_RIGHT) {
			strings.add(" ");
			strings.add(" ");
			strings.add(" ");
			strings.add(" ");
		}
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		final TextBlockBackcolored result = getGraphicStrings();
		final ImageBuilder imageBuilder = new ImageBuilder(new ColorMapperIdentity(), 1.0, result.getBackcolor(),
				getMetadata(), null, 0, 0, null, false);
		imageBuilder.setUDrawable(result);
		// imageBuilder.setUDrawable(TextBlockUtils.withMargin(result, 4, 4));
		return imageBuilder.writeImageTOBEMOVED(fileFormat, seed, os);
	}

	public TextBlockBackcolored getGraphicStrings() throws IOException {
		if (position != null) {
			return GraphicStrings.createBlackOnWhite(strings, PSystemVersion.getPlantumlImage(), position);
		}
		return GraphicStrings.createBlackOnWhite(strings);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Empty)");
	}

}
