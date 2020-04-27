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
package net.sourceforge.plantuml.ugraphic.comp;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class PiecewiseAffineOnXorYBuilder extends AbstractTextBlock implements TextBlock, TextBlockBackcolored {

	private final TextBlock textBlock;
	private final CompressionMode mode;
	private final PiecewiseAffineTransform piecewiseAffineTransform;

	public static TextBlock build(CompressionMode mode, TextBlock textBlock,
			PiecewiseAffineTransform piecewiseAffineTransform) {
		return new PiecewiseAffineOnXorYBuilder(mode, textBlock, piecewiseAffineTransform);
	}

	private PiecewiseAffineOnXorYBuilder(CompressionMode mode, TextBlock textBlock,
			PiecewiseAffineTransform piecewiseAffineTransform) {
		this.textBlock = textBlock;
		this.mode = mode;
		this.piecewiseAffineTransform = piecewiseAffineTransform;
	}

	public void drawU(final UGraphic ug) {
		textBlock.drawU(new UGraphicCompressOnXorY(mode, ug, piecewiseAffineTransform));
	}

	private MinMax cachedMinMax;

	@Override
	public MinMax getMinMax(StringBounder stringBounder) {
		if (cachedMinMax == null) {
			cachedMinMax = TextBlockUtils.getMinMax(this, stringBounder);
		}
		return cachedMinMax;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = textBlock.calculateDimension(stringBounder);
		if (mode == CompressionMode.ON_X) {
			return new Dimension2DDouble(piecewiseAffineTransform.transform(dim.getWidth()), dim.getHeight());
		} else {
			return new Dimension2DDouble(dim.getWidth(), piecewiseAffineTransform.transform(dim.getHeight()));
		}
	}

	public HColor getBackcolor() {
		return null;
	}

}
