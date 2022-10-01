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
package net.sourceforge.plantuml.asciiart;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.ArrowComponent;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.txt.UGraphicTxt;

public class ComponentTextSelfArrow extends AbstractComponentText implements ArrowComponent {

	private final ComponentType type;
	private final Display stringsToDisplay;
	private final FileFormat fileFormat;
	private final ArrowConfiguration config;

	public ComponentTextSelfArrow(ComponentType type, ArrowConfiguration config, Display stringsToDisplay,
			FileFormat fileFormat) {
		this.type = type;
		this.stringsToDisplay = ComponentTextArrow.cleanAndManageBoldNumber(stringsToDisplay, fileFormat);
		this.fileFormat = fileFormat;
		this.config = config;
	}

	public void drawU(UGraphic ug, Area area, Context2D context) {
		if (config.isHidden()) {
			return;
		}
		final XDimension2D dimensionToUse = area.getDimensionToUse();
		final UmlCharArea charArea = ((UGraphicTxt) ug).getCharArea();
		final int width = (int) dimensionToUse.getWidth();
		final int height = (int) dimensionToUse.getHeight() - 1;

		charArea.fillRect(' ', 0, 0, width, height);

		if (fileFormat == FileFormat.UTXT) {
			if (config.isDotted()) {
				charArea.drawStringLR("\u2500 \u2500 \u2510", 0, 0);
				charArea.drawStringLR("|", 4, 1);
				charArea.drawStringLR("< \u2500 \u2518", 0, 2);
			} else {
				charArea.drawStringLR("\u2500\u2500\u2500\u2500\u2510", 0, 0);
				charArea.drawStringLR("\u2502", 4, 1);
				charArea.drawStringLR("<\u2500\u2500\u2500\u2518", 0, 2);
			}
		} else if (config.isDotted()) {
			charArea.drawStringLR("- - .", 0, 0);
			charArea.drawStringLR("|", 4, 1);
			charArea.drawStringLR("< - '", 0, 2);
		} else {
			charArea.drawStringLR("----.", 0, 0);
			charArea.drawStringLR("|", 4, 1);
			charArea.drawStringLR("<---'", 0, 2);
		}

		if (fileFormat == FileFormat.UTXT) {
			charArea.drawStringsLRUnicode(stringsToDisplay.asList(), 6, 1);
		} else {
			charArea.drawStringsLRSimple(stringsToDisplay.asList(), 6, 1);
		}
	}

	public double getPreferredHeight(StringBounder stringBounder) {
		return StringUtils.getHeight(stringsToDisplay) + 3;
	}

	public double getPreferredWidth(StringBounder stringBounder) {
		return StringUtils.getWcWidth(stringsToDisplay) + 6;
	}

	public XPoint2D getStartPoint(StringBounder stringBounder, XDimension2D dimensionToUse) {
		return new XPoint2D(0, 0);
	}

	public XPoint2D getEndPoint(StringBounder stringBounder, XDimension2D dimensionToUse) {
		return new XPoint2D(0, 0);
	}

	public double getPaddingY() {
		throw new UnsupportedOperationException();
	}

	public double getYPoint(StringBounder stringBounder) {
		throw new UnsupportedOperationException();
	}
	
	public double getPosArrow(StringBounder stringBounder) {
		throw new UnsupportedOperationException();
	}


}
