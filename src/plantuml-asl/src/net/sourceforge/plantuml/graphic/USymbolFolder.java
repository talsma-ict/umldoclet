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

import java.util.Objects;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class USymbolFolder extends USymbol {

	private final static int marginTitleX1 = 3;
	private final static int marginTitleX2 = 3;
	private final static int marginTitleX3 = 7;
	private final static int marginTitleY0 = 0;
	private final static int marginTitleY1 = 3;
	private final static int marginTitleY2 = 3;

	private final SName sname;
	private final boolean showTitle;

	public USymbolFolder(SName sname, boolean showTitle) {
		this.showTitle = showTitle;
		this.sname = sname;
	}

	@Override
	public String toString() {
		return super.toString() + " " + showTitle;
	}

	@Override
	public SName getSName() {
		return sname;
	}

	private void drawFolder(UGraphic ug, double width, double height, XDimension2D dimTitle, double shadowing,
			double roundCorner) {

		final double wtitle;
		if (dimTitle.getWidth() == 0) {
			wtitle = Math.max(30, width / 4);
		} else {
			wtitle = dimTitle.getWidth() + marginTitleX1 + marginTitleX2;
		}
		final double htitle = getHTitle(dimTitle);

		final Shadowable shape;
		if (roundCorner == 0) {
			final UPolygon poly = new UPolygon();
			poly.addPoint(0, 0);
			poly.addPoint(wtitle, 0);

			poly.addPoint(wtitle + marginTitleX3, htitle);
			poly.addPoint(width, htitle);
			poly.addPoint(width, height);
			poly.addPoint(0, height);
			poly.addPoint(0, 0);
			shape = poly;
		} else {
			final UPath path = new UPath();
			path.moveTo(roundCorner / 2, 0);
			path.lineTo(wtitle - roundCorner / 2, 0);
			// path.lineTo(wtitle, roundCorner / 2);
			path.arcTo(new XPoint2D(wtitle, roundCorner / 2), roundCorner / 2 * 1.5, 0, 1);
			path.lineTo(wtitle + marginTitleX3, htitle);
			path.lineTo(width - roundCorner / 2, htitle);
			path.arcTo(new XPoint2D(width, htitle + roundCorner / 2), roundCorner / 2, 0, 1);
			path.lineTo(width, height - roundCorner / 2);
			path.arcTo(new XPoint2D(width - roundCorner / 2, height), roundCorner / 2, 0, 1);
			path.lineTo(roundCorner / 2, height);
			path.arcTo(new XPoint2D(0, height - roundCorner / 2), roundCorner / 2, 0, 1);
			path.lineTo(0, roundCorner / 2);
			path.arcTo(new XPoint2D(roundCorner / 2, 0), roundCorner / 2, 0, 1);
			path.closePath();
			shape = path;
		}
		shape.setDeltaShadow(shadowing);

		ug.draw(shape);
		ug.apply(UTranslate.dy(htitle)).draw(ULine.hline(wtitle + marginTitleX3));
	}

	private double getHTitle(XDimension2D dimTitle) {
		final double htitle;
		if (dimTitle.getWidth() == 0) {
			htitle = 10;
		} else {
			htitle = dimTitle.getHeight() + marginTitleY1 + marginTitleY2;
		}
		return htitle;
	}

	private Margin getMargin() {
		return new Margin(10, 10 + 10, 10 + 3, 10);
	}

	@Override
	public TextBlock asSmall(final TextBlock name, final TextBlock label, final TextBlock stereotype,
			final SymbolContext symbolContext, final HorizontalAlignment stereoAlignment) {
		Objects.requireNonNull(name);
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final XDimension2D dim = calculateDimension(ug.getStringBounder());
				ug = UGraphicStencil.create(ug, dim);
				ug = symbolContext.apply(ug);
				final XDimension2D dimName = getDimName(ug.getStringBounder());
				drawFolder(ug, dim.getWidth(), dim.getHeight(), dimName, symbolContext.getDeltaShadow(),
						symbolContext.getRoundCorner());
				final Margin margin = getMargin();
				final TextBlock tb = TextBlockUtils.mergeTB(stereotype, label, HorizontalAlignment.CENTER);
				if (showTitle) {
					name.drawU(ug.apply(new UTranslate(4, 3)));
				}
				tb.drawU(ug.apply(new UTranslate(margin.getX1(), margin.getY1() + dimName.getHeight())));
			}

			private XDimension2D getDimName(StringBounder stringBounder) {
				return showTitle ? name.calculateDimension(stringBounder) : new XDimension2D(40, 15);
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				final XDimension2D dimName = getDimName(stringBounder);
				final XDimension2D dimLabel = label.calculateDimension(stringBounder);
				final XDimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				return getMargin().addDimension(XDimension2D.mergeTB(dimName, dimStereo, dimLabel));
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
				drawFolder(ug, dim.getWidth(), dim.getHeight(), dimTitle, symbolContext.getDeltaShadow(),
						symbolContext.getRoundCorner());
				title.drawU(ug.apply(new UTranslate(4, 2)));
				final XDimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				final double posStereo = (width - dimStereo.getWidth()) / 2;

				stereotype.drawU(ug.apply(new UTranslate(4 + posStereo, 2 + getHTitle(dimTitle))));
			}

			public XDimension2D calculateDimension(StringBounder stringBounder) {
				return new XDimension2D(width, height);
			}

		};
	}

}
