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
package net.sourceforge.plantuml.decoration.symbol;

import java.util.Objects;

import net.sourceforge.plantuml.klimt.Fashion;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.drawing.UGraphicStencil;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.AbstractTextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlock;

abstract class USymbolSimpleAbstract extends USymbol {

	@Override
	public TextBlock asSmall(TextBlock name, final TextBlock label, final TextBlock stereotype,
			final Fashion symbolContext, final HorizontalAlignment stereoAlignment) {
		Objects.requireNonNull(stereotype);
		final TextBlock stickman = getDrawing(symbolContext);
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final StringBounder stringBounder = ug.getStringBounder();
				final XDimension2D dimLabel = label.calculateDimension(stringBounder);
				final XDimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				final XDimension2D dimStickMan = stickman.calculateDimension(stringBounder);
				final XDimension2D dimTotal = calculateDimension(stringBounder);
				final double stickmanX = (dimTotal.getWidth() - dimStickMan.getWidth()) / 2;
				final double stickmanY = dimStereo.getHeight();
				ug = symbolContext.apply(ug);
				stickman.drawU(ug.apply(new UTranslate(stickmanX, stickmanY)));
				final double labelX = (dimTotal.getWidth() - dimLabel.getWidth()) / 2;
				final double labelY = dimStickMan.getHeight() + dimStereo.getHeight();

				// Actor bug?
				final UGraphic ug2 = UGraphicStencil.create(ug, dimLabel);
				label.drawU(ug2.apply(new UTranslate(labelX, labelY)));
				// label.drawU(ug.apply(new UTranslate(labelX, labelY)));

				final double stereoX = (dimTotal.getWidth() - dimStereo.getWidth()) / 2;
				stereotype.drawU(ug.apply(UTranslate.dx(stereoX)));
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				final XDimension2D dimLabel = label.calculateDimension(stringBounder);
				final XDimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				final XDimension2D dimActor = stickman.calculateDimension(stringBounder);
				return XDimension2D.mergeLayoutT12B3(dimStereo, dimActor, dimLabel);
			}
		};
	}

	abstract protected TextBlock getDrawing(final Fashion symbolContext);

	@Override
	public TextBlock asBig(final TextBlock title, HorizontalAlignment labelAlignment, TextBlock stereotype,
			final double width, final double height, final Fashion symbolContext,
			final HorizontalAlignment stereoAlignment) {
		throw new UnsupportedOperationException();
	}

}
