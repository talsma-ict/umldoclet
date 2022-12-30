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
package net.sourceforge.plantuml.graphic;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class TextBlockMinWidth extends AbstractTextBlock implements TextBlock {

	private final TextBlock textBlock;
	private final double minWidth;
	private final HorizontalAlignment horizontalAlignment;

	TextBlockMinWidth(TextBlock textBlock, double minWidth, HorizontalAlignment horizontalAlignment) {
		this.textBlock = textBlock;
		this.minWidth = minWidth;
		this.horizontalAlignment = horizontalAlignment;
	}

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final XDimension2D dim = textBlock.calculateDimension(stringBounder);
		return dim.atLeast(minWidth, 0);
	}

	public void drawU(UGraphic ug) {
		if (horizontalAlignment == HorizontalAlignment.LEFT) {
			textBlock.drawU(ug);
		} else if (horizontalAlignment == HorizontalAlignment.CENTER) {
			final XDimension2D dimText = textBlock.calculateDimension(ug.getStringBounder());
			final XDimension2D dimFull = calculateDimension(ug.getStringBounder());
			final double diffx = dimFull.getWidth() - dimText.getWidth();
			textBlock.drawU(ug.apply(UTranslate.dx(diffx / 2)));
		} else if (horizontalAlignment == HorizontalAlignment.RIGHT) {
			final XDimension2D dimText = textBlock.calculateDimension(ug.getStringBounder());
			final XDimension2D dimFull = calculateDimension(ug.getStringBounder());
			final double diffx = dimFull.getWidth() - dimText.getWidth();
			textBlock.drawU(ug.apply(UTranslate.dx(diffx)));
		} else {
			throw new UnsupportedOperationException();
		}
	}
}
