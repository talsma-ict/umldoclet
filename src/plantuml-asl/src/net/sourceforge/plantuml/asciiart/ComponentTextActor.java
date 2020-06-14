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

public class ComponentTextActor extends AbstractComponentText {

	private final ComponentType type;
	private final Display stringsToDisplay;
	private final FileFormat fileFormat;
	private final AsciiShape shape;

	public ComponentTextActor(ComponentType type, Display stringsToDisplay, FileFormat fileFormat, AsciiShape shape) {
		this.type = type;
		this.stringsToDisplay = stringsToDisplay;
		this.fileFormat = fileFormat;
		this.shape = shape;
	}

	public void drawU(UGraphic ug, Area area, Context2D context) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		final UmlCharArea charArea = ((UGraphicTxt) ug).getCharArea();
		final int width = (int) dimensionToUse.getWidth();
		final int height = (int) dimensionToUse.getHeight();
		charArea.fillRect(' ', 0, 0, width, height);

		final int xman = width / 2 - 1;
		if (type == ComponentType.ACTOR_HEAD) {
			charArea.drawStringsLR(stringsToDisplay.as(), 1, getHeight());
			if (fileFormat == FileFormat.UTXT) {
				charArea.drawShape(AsciiShape.STICKMAN_UNICODE, xman, 0);
			} else {
				charArea.drawShape(AsciiShape.STICKMAN, xman, 0);
			}
		} else if (type == ComponentType.ACTOR_TAIL) {
			charArea.drawStringsLR(stringsToDisplay.as(), 1, 0);
			if (fileFormat == FileFormat.UTXT) {
				charArea.drawShape(AsciiShape.STICKMAN_UNICODE, xman, 1);
			} else {
				charArea.drawShape(AsciiShape.STICKMAN, xman, 1);
			}
		} else {
			assert false;
		}
	}

	private int getHeight() {
		if (fileFormat == FileFormat.UTXT) {
			return AsciiShape.STICKMAN_UNICODE.getHeight();
		}
		return AsciiShape.STICKMAN.getHeight();
	}

	public double getPreferredHeight(StringBounder stringBounder) {
		return StringUtils.getHeight(stringsToDisplay) + getHeight();
	}

	public double getPreferredWidth(StringBounder stringBounder) {
		return StringUtils.getWcWidth(stringsToDisplay) + 2;
	}

}
