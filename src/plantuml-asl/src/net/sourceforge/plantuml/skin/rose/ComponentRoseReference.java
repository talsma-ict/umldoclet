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
package net.sourceforge.plantuml.skin.rose;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class ComponentRoseReference extends AbstractTextualComponent {

	private final int cornersize = 10;
	private final TextBlock textHeader;
	private final double heightFooter = 5;
	private final double xMargin = 2;
	private final HorizontalAlignment position;
	private final SymbolContext symbolContextHeader;
	private final SymbolContext symbolContextBody;
	private int roundCorner;

	public ComponentRoseReference(Style style, Style styleHeader, Display stringsToDisplay, ISkinSimple spriteContainer,
			HColor background) {
		super(style, LineBreakStrategy.NONE, 4, 4, 4, spriteContainer,
				stringsToDisplay.subList(1, stringsToDisplay.size()), false);

		this.symbolContextHeader = styleHeader.getSymbolContext(getIHtmlColorSet());
		this.symbolContextBody = style.getSymbolContext(getIHtmlColorSet());
		this.roundCorner = style.value(PName.RoundCorner).asInt();
		final FontConfiguration fcHeader = styleHeader.getFontConfiguration(getIHtmlColorSet());
		this.position = style.getHorizontalAlignment();

		this.textHeader = stringsToDisplay.subList(0, 1).create(fcHeader, HorizontalAlignment.LEFT, spriteContainer);

	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		final XDimension2D dimensionToUse = area.getDimensionToUse();
		final StringBounder stringBounder = ug.getStringBounder();
		final int textHeaderWidth = (int) (getHeaderWidth(stringBounder));
		final int textHeaderHeight = (int) (getHeaderHeight(stringBounder));

		URectangle rect = new URectangle(dimensionToUse.getWidth() - xMargin * 2 - symbolContextBody.getDeltaShadow(),
				dimensionToUse.getHeight() - heightFooter);
		if (this.roundCorner != 0)
			rect = rect.rounded(this.roundCorner);

		rect.setDeltaShadow(symbolContextBody.getDeltaShadow());
		ug = symbolContextBody.apply(ug);
		ug.apply(UTranslate.dx(xMargin)).draw(rect);

		final UPath corner = new UPath();
		if (this.roundCorner == 0) {
			corner.moveTo(0, 0);
			corner.lineTo(textHeaderWidth, 0);

			corner.lineTo(textHeaderWidth, textHeaderHeight - cornersize);
			corner.lineTo(textHeaderWidth - cornersize, textHeaderHeight);

			corner.lineTo(0, textHeaderHeight);
			corner.lineTo(0, 0);
		} else {
			corner.moveTo(this.roundCorner / 2, 0);
			corner.lineTo(textHeaderWidth, 0);

			corner.lineTo(textHeaderWidth, textHeaderHeight - cornersize);
			corner.lineTo(textHeaderWidth - cornersize, textHeaderHeight);

			corner.lineTo(0, textHeaderHeight);
			corner.lineTo(0, this.roundCorner / 2);

			corner.arcTo(this.roundCorner / 2, this.roundCorner / 2, 0, 0, 1, this.roundCorner / 2, 0);
		}

		ug = symbolContextHeader.apply(ug);
		ug.apply(UTranslate.dx(xMargin)).draw(corner);

		ug = ug.apply(new UStroke());

		textHeader.drawU(ug.apply(new UTranslate(15, 2)));
		final double textPos;
		if (position == HorizontalAlignment.CENTER) {
			final double textWidth = getTextBlock().calculateDimension(stringBounder).getWidth();
			textPos = (dimensionToUse.getWidth() - textWidth) / 2;
		} else if (position == HorizontalAlignment.RIGHT) {
			final double textWidth = getTextBlock().calculateDimension(stringBounder).getWidth();
			textPos = dimensionToUse.getWidth() - textWidth - getMarginX2() - xMargin;
		} else {
			textPos = getMarginX1() + xMargin;
		}
		getTextBlock().drawU(ug.apply(new UTranslate(textPos, (getMarginY() + textHeaderHeight))));
	}

	private double getHeaderHeight(StringBounder stringBounder) {
		final XDimension2D headerDim = textHeader.calculateDimension(stringBounder);
		return headerDim.getHeight() + 2 * 1;
	}

	private double getHeaderWidth(StringBounder stringBounder) {
		final XDimension2D headerDim = textHeader.calculateDimension(stringBounder);
		return headerDim.getWidth() + 30 + 15;
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder) + getHeaderHeight(stringBounder) + heightFooter;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return Math.max(getTextWidth(stringBounder), getHeaderWidth(stringBounder)) + xMargin * 2
				+ symbolContextBody.getDeltaShadow();
	}

}
