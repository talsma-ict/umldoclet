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
package net.sourceforge.plantuml.ugraphic.comp;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class TextBlockCompressedOnXorY extends AbstractTextBlock implements TextBlock {

	private final TextBlock textBlock;
	private final CompressionMode mode;

	public TextBlockCompressedOnXorY(CompressionMode mode, TextBlock textBlock) {
		this.textBlock = textBlock;
		this.mode = mode;
	}

	public void drawU(final UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final CompressionTransform compressionTransform = getCompressionTransform(stringBounder);
		textBlock.drawU(new UGraphicCompressOnXorY(mode, ug, compressionTransform));
	}

	private MinMax cachedMinMax;

	@Override
	public MinMax getMinMax(StringBounder stringBounder) {
		if (cachedMinMax == null) {
			cachedMinMax = TextBlockUtils.getMinMax(this, stringBounder);
		}
		return cachedMinMax;
	}

	private CompressionTransform cachedCompressionTransform;

	private CompressionTransform getCompressionTransform(final StringBounder stringBounder) {
		if (cachedCompressionTransform == null) {
			cachedCompressionTransform = getCompressionTransformSlow(stringBounder);
		}
		return cachedCompressionTransform;
	}

	private CompressionTransform getCompressionTransformSlow(final StringBounder stringBounder) {
		final SlotFinder slotFinder = new SlotFinder(mode, stringBounder);
		textBlock.drawU(slotFinder);
		final SlotSet ysSlotSet = slotFinder.getSlotSet().reverse().smaller(5.0);
		final CompressionTransform compressionTransform = new CompressionTransform(ysSlotSet);
		return compressionTransform;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final CompressionTransform compressionTransform = getCompressionTransform(stringBounder);
		final Dimension2D dim = textBlock.calculateDimension(stringBounder);
		if (mode == CompressionMode.ON_X) {
			return new Dimension2DDouble(compressionTransform.transform(dim.getWidth()), dim.getHeight());
		} else {
			return new Dimension2DDouble(dim.getWidth(), compressionTransform.transform(dim.getHeight()));
		}
	}
}
