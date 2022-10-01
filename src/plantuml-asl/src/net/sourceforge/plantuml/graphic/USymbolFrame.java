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
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColors;

class USymbolFrame extends USymbol {

	@Override
	public SName getSName() {
		return sname;
	}

	private final SName sname;

	public USymbolFrame(SName sname) {
		this.sname = sname;
	}

	private void drawFrame(UGraphic ug, double width, double height, XDimension2D dimTitle, double shadowing,
			double roundCorner) {
		final Shadowable shape = new URectangle(width, height).rounded(roundCorner).ignoreForCompressionOnX()
				.ignoreForCompressionOnY();
		shape.setDeltaShadow(shadowing);

		ug.draw(shape);

		final double textWidth;
		final int cornersize;
		if (dimTitle.getWidth() == 0) {
			textWidth = width / 3;
			cornersize = 7;
		} else {
			textWidth = dimTitle.getWidth() + 10;
			cornersize = 10;
		}
		final double textHeight = getYpos(dimTitle);

		final UPath polygon = new UPath();
		polygon.setIgnoreForCompressionOnX();
		polygon.moveTo(textWidth, 0);

		polygon.lineTo(textWidth, textHeight - cornersize);
		polygon.lineTo(textWidth - cornersize, textHeight);

		polygon.lineTo(0, textHeight);
		ug.apply(HColors.none().bg()).draw(polygon);

	}

	private double getYpos(XDimension2D dimTitle) {
		if (dimTitle.getWidth() == 0)
			return 12;

		return dimTitle.getHeight() + 3;
	}

	private Margin getMargin() {
		return new Margin(10 + 5, 20 + 5, 15 + 5, 5 + 5);
	}

	@Override
	public TextBlock asSmall(TextBlock name, final TextBlock label, final TextBlock stereotype,
			final SymbolContext symbolContext, final HorizontalAlignment stereoAlignment) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final XDimension2D dim = calculateDimension(ug.getStringBounder());
				ug = UGraphicStencil.create(ug, dim);
				ug = symbolContext.apply(ug);
				drawFrame(ug, dim.getWidth(), dim.getHeight(), new XDimension2D(0, 0), symbolContext.getDeltaShadow(),
						symbolContext.getRoundCorner());
				final Margin margin = getMargin();
				final TextBlock tb = TextBlockUtils.mergeTB(stereotype, label, HorizontalAlignment.CENTER);
				tb.drawU(ug.apply(new UTranslate(margin.getX1(), margin.getY1())));
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				final XDimension2D dimLabel = label.calculateDimension(stringBounder);
				final XDimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				return getMargin().addDimension(XDimension2D.mergeTB(dimStereo, dimLabel));
			}
		};
	}

	@Override
	public TextBlock asBig(final TextBlock title, HorizontalAlignment labelAlignment, final TextBlock stereotype,
			final double width, final double height, final SymbolContext symbolContext,
			final HorizontalAlignment stereoAlignment) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final StringBounder stringBounder = ug.getStringBounder();
				final XDimension2D dim = calculateDimension(stringBounder);
				ug = symbolContext.apply(ug);
				final XDimension2D dimTitle = title.calculateDimension(stringBounder);
				final double widthFull = dim.getWidth();
				drawFrame(ug, widthFull, dim.getHeight(), dimTitle, symbolContext.getDeltaShadow(),
						symbolContext.getRoundCorner());
				final double widthTitle = title.calculateDimension(stringBounder).getWidth();

				// Temporary hack...
				if (widthFull - widthTitle < 25)
					title.drawU(ug.apply(new UTranslate(3, 1)));
				else
					ug.apply(new UTranslate(3, 1)).draw(new SpecialText(title));

				final XDimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				final double posStereo = (width - dimStereo.getWidth()) / 2;

				stereotype.drawU(ug.apply(new UTranslate(4 + posStereo, 2 + getYpos(dimTitle))));
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				return new XDimension2D(width, height);
			}
		};
	}

//	static class Interceptor extends UGraphicDelegator {
//
//		public Interceptor(UGraphic ug) {
//			super(ug);
//		}
//
//		@Override
//		public void draw(UShape shape) {
//			if (shape instanceof SpecialText) {
//				final SpecialText specialText = (SpecialText) shape;
//				specialText.title.drawU(getUg());
//				// System.err.println("getug=" + getUg());
//				return;
//			}
//			super.draw(shape);
//		}
//
//		public UGraphic apply(UChange change) {
//			return new Interceptor(getUg().apply(change));
//		}
//
//	}

}
