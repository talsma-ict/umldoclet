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

import net.sourceforge.plantuml.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class USymbolPerson extends USymbol {

	@Override
	public SkinParameter getSkinParameter() {
		return SkinParameter.PERSON;
	}

	@Override
	public SName getSName() {
		return SName.person;
	}

	private void drawHeadAndBody(UGraphic ug, double shadowing, Dimension2D dimBody, double headSize) {
		final UEllipse head = new UEllipse(headSize, headSize);
		final URectangle body = new URectangle(dimBody).rounded(headSize);

		body.setDeltaShadow(shadowing);
		head.setDeltaShadow(shadowing);

		final double posx = (dimBody.getWidth() - headSize) / 2;
		ug.apply(UTranslate.dx(posx)).draw(head);
		ug.apply(UTranslate.dy(headSize)).draw(body);
	}

	private double headSize(Dimension2D dimBody) {
		final double surface = dimBody.getWidth() * dimBody.getHeight();
		return Math.sqrt(surface) * .42;
	}

	private Margin getMargin() {
		return new Margin(10, 10, 10, 10);
	}

	@Override
	public TextBlock asSmall(TextBlock name, final TextBlock label, final TextBlock stereotype,
			final SymbolContext symbolContext, final HorizontalAlignment stereoAlignment) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final Dimension2D dimFull = calculateDimension(ug.getStringBounder());
				final Dimension2D dimBody = bodyDimension(ug.getStringBounder());
				ug = UGraphicStencil.create(ug, dimFull);
				ug = symbolContext.apply(ug);
				final double headSize = headSize(dimBody);
				drawHeadAndBody(ug, symbolContext.getDeltaShadow(), dimBody, headSize);
				final TextBlock tb = TextBlockUtils.mergeTB(stereotype, label, stereoAlignment);
				final Margin margin = getMargin();
				tb.drawU(ug.apply(new UTranslate(margin.getX1(), margin.getY1() + headSize)));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				final Dimension2D body = bodyDimension(stringBounder);
				return Dimension2DDouble.delta(body, 0, headSize(body));
			}

			private Dimension2D bodyDimension(StringBounder stringBounder) {
				final Dimension2D dimLabel = label.calculateDimension(stringBounder);
				final Dimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				return getMargin().addDimension(Dimension2DDouble.mergeTB(dimStereo, dimLabel));
			}
		};
	}

	@Override
	public TextBlock asBig(final TextBlock title, final HorizontalAlignment labelAlignment, final TextBlock stereotype,
			final double width, final double height, final SymbolContext symbolContext,
			final HorizontalAlignment stereoAlignment) {
		throw new UnsupportedOperationException();
	}

}
