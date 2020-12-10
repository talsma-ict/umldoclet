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
package net.sourceforge.plantuml.stats;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.ImageParameter;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class PSystemStats extends AbstractPSystem {

	private final List<String> strings = new ArrayList<String>();

	PSystemStats() throws IOException {
		final StatsImpl stats = (StatsImpl) StatsUtils.getStats();
		strings.addAll(new CreoleConverter(stats).toCreole());
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		final TextBlockBackcolored result = getGraphicStrings();
		HColor backcolor = result.getBackcolor();
		final ImageParameter imageParameter = new ImageParameter(new ColorMapperIdentity(), false, null, 1.0,
				getMetadata(), null, ClockwiseTopRightBottomLeft.none(), backcolor);
		final ImageBuilder imageBuilder = ImageBuilder.build(imageParameter);
		imageBuilder.setUDrawable(result);
		return imageBuilder.writeImageTOBEMOVED(fileFormat, seed, os);
	}

	public static PSystemStats create() throws IOException {
		return new PSystemStats();
	}

	private TextBlockBackcolored getGraphicStrings() throws IOException {
		return GraphicStrings.createBlackOnWhite(strings);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Stats)");
	}

}
