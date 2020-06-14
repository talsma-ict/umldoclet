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
package net.sourceforge.plantuml.version;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class PSystemLicense extends AbstractPSystem implements UDrawable {

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		final ImageBuilder imageBuilder = new ImageBuilder(new ColorMapperIdentity(), 1.0, null, getMetadata(), null,
				0, 0, null, false);
		imageBuilder.setUDrawable(this);
		return imageBuilder.writeImageTOBEMOVED(fileFormat, seed, os);
	}

	public static PSystemLicense create() throws IOException {
		return new PSystemLicense();
	}

	private TextBlockBackcolored getGraphicStrings(List<String> strings) {
		return GraphicStrings.createBlackOnWhite(strings);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescription("(License)");
	}

	public void drawU(UGraphic ug) {

		final LicenseInfo licenseInfo = LicenseInfo.retrieveQuick();
		final BufferedImage logo = LicenseInfo.retrieveDistributorImage(licenseInfo);

		if (logo == null) {
			final List<String> strings = new ArrayList<String>();
			strings.addAll(License.getCurrent().getText1(licenseInfo));
			strings.addAll(License.getCurrent().getText2(licenseInfo));
			getGraphicStrings(strings).drawU(ug);
		} else {
			final List<String> strings1 = new ArrayList<String>();
			final List<String> strings2 = new ArrayList<String>();

			strings1.addAll(License.getCurrent().getText1(licenseInfo));
			strings2.addAll(License.getCurrent().getText2(licenseInfo));

			final TextBlockBackcolored result1 = getGraphicStrings(strings1);
			result1.drawU(ug);
			ug = ug.apply(new UTranslate(0, 4 + result1.calculateDimension(ug.getStringBounder()).getHeight()));
			UImage im = new UImage(logo);
			ug.apply(new UTranslate(20, 0)).draw(im);

			ug = ug.apply(new UTranslate(0, im.getHeight()));
			final TextBlockBackcolored result2 = getGraphicStrings(strings2);
			result2.drawU(ug);
		}
	}
}
