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
package net.sourceforge.plantuml.salt.element;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

abstract class AbstractElementText extends AbstractElement {

	private final TextBlock block;
	private final FontConfiguration config;
	private final int charLength;

	public AbstractElementText(String text, UFont font, boolean manageLength, ISkinSimple spriteContainer) {
		config = FontConfiguration.blackBlueTrue(font);
		if (manageLength) {
			this.charLength = getCharNumber(text);
			text = StringUtils.trin(text);
		} else {
			this.charLength = 0;
		}
		this.block = Display.create(text).create(config, HorizontalAlignment.LEFT, spriteContainer);
	}

	private int getCharNumber(String text) {
		text = text.replaceAll("<&[-\\w]+>", "00");
		return text.length();
	}

	protected void drawText(UGraphic ug, double x, double y) {
		block.drawU(ug.apply(new UTranslate(x, y)));
	}

	protected Dimension2D getPureTextDimension(StringBounder stringBounder) {
		return block.calculateDimension(stringBounder);
	}

	protected Dimension2D getTextDimensionAt(StringBounder stringBounder, double x) {
		final Dimension2D result = block.calculateDimension(stringBounder);
		if (charLength == 0) {
			return result;
		}
		final double dimSpace = getSingleSpace(stringBounder);
		// final double endx = x + result.getWidth();
		// final double mod = endx % CHAR_SIZE;
		// final double delta = charLength * CHAR_SIZE - mod;
		// return Dimension2DDouble.delta(result, delta, 0);
		return new Dimension2DDouble(Math.max(result.getWidth(), charLength * dimSpace), result.getHeight());
	}

	private double getSingleSpace(StringBounder stringBounder) {
		// double max = 0;
		// for (int i = 32; i < 127; i++) {
		// final char c = (char) i;
		// final double w = Display.create(Arrays.asList("" + c), config, HorizontalAlignment.LEFT)
		// .calculateDimension(stringBounder).getWidth();
		// if (w > max) {
		// Log.println("c="+c+" "+max);
		// max = w;
		// }
		// }
		// return max;
		return 8;
	}

	protected final FontConfiguration getConfig() {
		return config;
	}

}
