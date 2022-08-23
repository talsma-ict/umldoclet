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
package net.sourceforge.plantuml.openiconic;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.PlainDiagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.style.ClockwiseTopRightBottomLeft;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class PSystemOpenIconic extends PlainDiagram {

	private final String iconName;
	private final double factor;

	public PSystemOpenIconic(UmlSource source, String iconName, double factor) {
		super(source);
		this.iconName = iconName;
		this.factor = factor;
	}

	@Override
	protected UDrawable getRootDrawable(FileFormatOption fileFormatOption) {
		final OpenIcon icon = OpenIcon.retrieve(iconName);
		// final Dimension2D dim = new Dimension2DDouble(100, 100);

		return icon.asTextBlock(HColors.BLACK, factor);

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

	@Override
	public ClockwiseTopRightBottomLeft getDefaultMargins() {
		return ClockwiseTopRightBottomLeft.same(5);
	}

}
