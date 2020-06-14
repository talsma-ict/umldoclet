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
package net.sourceforge.plantuml.asciiart;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.txt.UGraphicTxt;

public class ComponentTextGroupingHeader extends AbstractComponentText {

	private final ComponentType type;
	private final Display stringsToDisplay;
	private final FileFormat fileFormat;

	public ComponentTextGroupingHeader(ComponentType type, Display stringsToDisplay, FileFormat fileFormat) {
		this.type = type;
		this.stringsToDisplay = stringsToDisplay;
		this.fileFormat = fileFormat;
	}

	public void drawU(UGraphic ug, Area area, Context2D context) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		final UmlCharArea charArea = ((UGraphicTxt) ug).getCharArea();
		final int width = (int) dimensionToUse.getWidth();
		final int height = (int) dimensionToUse.getHeight();

		// charArea.fillRect('G', 0, 0, width, height);
		final String text = stringsToDisplay.get(0).toString();

		if (fileFormat == FileFormat.UTXT) {
			charArea.drawHLine('\u2550', 0, 1, width - 1, '\u2502', '\u256a');
			charArea.drawStringLR(StringUtils.goUpperCase(text) + "  /", 2, 1);
			charArea.drawHLine('\u2500', 2, 1, text.length() + 4);
			charArea.drawVLine('\u2551', 0, 1, height - 1);
			charArea.drawVLine('\u2551', width - 1, 1, height - 1);
			charArea.drawChar('\u255f', 0, 2);
			charArea.drawStringTB("\u2564\u2502\u2518", text.length() + 4, 0);
			charArea.drawChar('\u2554', 0, 0);
			charArea.drawChar('\u2557', width - 1, 0);
			charArea.drawHLine('\u2550', height - 1, 1, width - 1, '\u2502', '\u256a');
			charArea.drawChar('\u255a', 0, height - 1);
			charArea.drawChar('\u255d', width - 1, height - 1);
		} else {
			charArea.drawHLine('_', 0, 0, width - 1);
			charArea.drawStringLR(StringUtils.goUpperCase(text) + "  /", 2, 1);
			charArea.drawHLine('_', 2, 1, text.length() + 3);
			charArea.drawChar('/', text.length() + 3, 2);
			charArea.drawVLine('!', 0, 1, height);
			charArea.drawVLine('!', width - 1, 1, height);
			charArea.drawHLine('~', height - 1, 1, width - 1);
		}

		if (stringsToDisplay.size() > 1 && stringsToDisplay.get(1) != null) {
			final String comment = stringsToDisplay.get(1).toString();
			charArea.drawStringLR(comment, text.length() + 7, 1);

		}
	}

	public double getPreferredHeight(StringBounder stringBounder) {
		return StringUtils.getHeight(stringsToDisplay) + 1;
	}

	public double getPreferredWidth(StringBounder stringBounder) {
		return StringUtils.getWcWidth(stringsToDisplay) + 2;
	}

}
