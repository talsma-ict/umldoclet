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
package net.sourceforge.plantuml.skin.rose;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.svek.image.Opale;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

final public class ComponentRoseNote extends AbstractTextualComponent implements Stencil {

	private final double paddingX;
	private final double paddingY;
	private final SymbolContext symbolContext;
	private final double roundCorner;
	private final HorizontalAlignment position;

	public ComponentRoseNote(Style style, SymbolContext symbolContext, FontConfiguration font, Display strings,
			double paddingX, double paddingY, ISkinSimple spriteContainer, double roundCorner,
			HorizontalAlignment textAlignment, HorizontalAlignment position) {
		super(style, spriteContainer.wrapWidth(), strings, font, textAlignment,
				textAlignment == HorizontalAlignment.CENTER ? 15 : 6, 15, 5, spriteContainer, true, null, null);
		this.paddingX = paddingX;
		this.paddingY = paddingY;
		this.position = position;
		if (UseStyle.useBetaStyle()) {
			this.symbolContext = style.getSymbolContext(spriteContainer.getThemeStyle(), getIHtmlColorSet());
			this.roundCorner = style.value(PName.RoundCorner).asInt();
		} else {
			this.symbolContext = symbolContext;
			this.roundCorner = roundCorner;
		}
	}

	@Override
	final public double getPreferredWidth(StringBounder stringBounder) {
		final double result = getTextWidth(stringBounder) + 2 * getPaddingX() + symbolContext.getDeltaShadow();
		return result;
	}

	@Override
	final public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder) + 2 * getPaddingY() + symbolContext.getDeltaShadow();
	}

	@Override
	public double getPaddingX() {
		return paddingX;
	}

	@Override
	public double getPaddingY() {
		return paddingY;
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {

		final StringBounder stringBounder = ug.getStringBounder();
		final int textHeight = (int) getTextHeight(stringBounder);

		int x2 = (int) getTextWidth(stringBounder);
		final double diffX = area.getDimensionToUse().getWidth() - getPreferredWidth(stringBounder);
		if (diffX < 0) {
			throw new IllegalArgumentException();
		}
		if (area.getDimensionToUse().getWidth() > getPreferredWidth(stringBounder)) {
			x2 = (int) (area.getDimensionToUse().getWidth() - 2 * getPaddingX());
		}

		final UPath polygon = Opale.getPolygonNormal(x2, textHeight, roundCorner);
		polygon.setDeltaShadow(symbolContext.getDeltaShadow());

		ug = symbolContext.apply(ug);
		ug.draw(polygon);

		ug.draw(Opale.getCorner(x2, roundCorner));
		UGraphic ug2 = UGraphicStencil.create(ug, this, new UStroke());

		if (position == HorizontalAlignment.LEFT) {
			ug2 = ug2.apply(new UTranslate(getMarginX1(), getMarginY()));
		} else if (position == HorizontalAlignment.RIGHT) {
			ug2 = ug2.apply(
					new UTranslate(area.getDimensionToUse().getWidth() - getTextWidth(stringBounder), getMarginY()));
		} else {
			ug2 = ug2.apply(new UTranslate(getMarginX1() + diffX / 2, getMarginY()));
		}

		getTextBlock().drawU(ug2);

	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return 0;
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return getTextWidth(stringBounder);
	}

}
