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
package net.sourceforge.plantuml.ugraphic.debug;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.util.Random;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.graphic.StringBounderRaw;
import net.sourceforge.plantuml.ugraphic.UFont;

public class StringBounderDebug extends StringBounderRaw {

	@Override
	protected Dimension2D calculateDimensionInternal(UFont font, String text) {
		final Random rnd = new Random(StringUtils.seed(text));
		// We want a random factor between 80% et 130%
		final double factor = 0.8 + 0.5 * rnd.nextDouble();
		final double size = font.getSize2D();
		final double height = size;
		final double width = size * text.length() * factor;
		return new Dimension2DDouble(width, height);
	}

	@Override
	public double getDescent(UFont font, String text) {
		final double descent = font.getSize2D() / 4.5;
		return descent;
	}

}
