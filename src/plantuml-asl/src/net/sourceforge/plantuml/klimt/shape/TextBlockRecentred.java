/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.klimt.shape;

import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.MinMax;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;

public class TextBlockRecentred extends AbstractTextBlock {
    // ::remove file when __HAXE__

	private final TextBlock textBlock;

	public TextBlockRecentred(TextBlock textBlock) {
		this.textBlock = textBlock;
	}

	public void drawU(final UGraphic ug) {
		StringBounder stringBounder = ug.getStringBounder();
		final MinMax minMax = getMinMax(stringBounder);
		textBlock.drawU(ug.apply(new UTranslate(-minMax.getMinX(), -minMax.getMinY())));
	}

	// private MinMax cachedMinMax;

	public MinMax getMinMax(StringBounder stringBounder) {
		return textBlock.getMinMax(stringBounder);
		// if (cachedMinMax == null) {
		// cachedMinMax = getMinMaxSlow(stringBounder);
		// }
		// // assert
		// cachedMinMax.toString().equals(getMinMaxSlow(stringBounder).toString());
		// return cachedMinMax;
	}

	// private MinMax getMinMaxSlow(StringBounder stringBounder) {
	// final MinMax result = TextBlockUtils.getMinMax(textBlock, stringBounder);
	// return result;
	// }

	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final MinMax minMax = getMinMax(stringBounder);
		return minMax.getDimension();
	}

	public HColor getBackcolor() {
		return textBlock.getBackcolor();
	}

}
