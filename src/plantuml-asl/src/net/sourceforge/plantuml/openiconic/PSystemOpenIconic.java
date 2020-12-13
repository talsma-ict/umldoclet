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
package net.sourceforge.plantuml.openiconic;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.ImageParameter;
import net.sourceforge.plantuml.ugraphic.color.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class PSystemOpenIconic extends AbstractPSystem {

	private final String iconName;
	private final double factor;

	public PSystemOpenIconic(String iconName, double factor) {
		this.iconName = iconName;
		this.factor = factor;
	}

	@Override
	final protected ImageData exportDiagramNow(OutputStream os, int num, FileFormatOption fileFormat, long seed)
			throws IOException {
		final OpenIcon icon = OpenIcon.retrieve(iconName);
		// final Dimension2D dim = new Dimension2DDouble(100, 100);

		final int margin1;
		final int margin2;
		if (UseStyle.useBetaStyle()) {
			margin1 = SkinParam.zeroMargin(5);
			margin2 = SkinParam.zeroMargin(5);
		} else {
			margin1 = 5;
			margin2 = 5;
		}
		final ClockwiseTopRightBottomLeft margins = ClockwiseTopRightBottomLeft.margin1margin2(margin1, margin2);
		final ImageParameter imageParameter = new ImageParameter(new ColorMapperIdentity(), false, null, 1.0, null,
				null, margins, null);
		final ImageBuilder imageBuilder = ImageBuilder.build(imageParameter);
		imageBuilder.setUDrawable(icon.asTextBlock(HColorUtils.BLACK, factor));
		return imageBuilder.writeImageTOBEMOVED(fileFormat, seed, os);

		// UGraphic2 ug = fileFormat.createUGraphic(dim);
		// ug = (UGraphic2) ug.apply(new UTranslate(10, 10));
		// // ug = ug.apply(UChangeColor.nnn(HtmlColorUtils.BLACK));
		// // ug.draw(new URectangle(7, 6));
		// icon.asTextBlock(HtmlColorUtils.BLACK, factor).drawU(ug);
		// ug.writeImageTOBEMOVED(os, null, 96);
		// return new ImageDataSimple(dim);
	}

	// private GraphicStrings getGraphicStrings() throws IOException {
	// final UFont font = new UFont("SansSerif", Font.PLAIN, 12);
	// final GraphicStrings result = new GraphicStrings(strings, font,
	// HtmlColorUtils.BLACK, HtmlColorUtils.WHITE,
	// UAntiAliasing.ANTI_ALIASING_ON);
	// return result;
	// }

	public DiagramDescription getDescription() {
		return new DiagramDescription("(Open iconic)");
	}

}
