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
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

final public class ComponentRoseNoteBox extends AbstractTextualComponent {

	private final SymbolContext symbolContext;
	private final double roundCorner;

	public ComponentRoseNoteBox(Style style, SymbolContext symbolContext, FontConfiguration font, Display strings,
			ISkinSimple spriteContainer, double roundCorner, HorizontalAlignment alignment) {
		super(style, spriteContainer.wrapWidth(), strings, font, alignment, 4, 4, 4, spriteContainer, false, null,
				null);
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
		final double result = getTextWidth(stringBounder) + 2 * getPaddingX();
		return result;
	}

	@Override
	final public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder) + 2 * getPaddingY();
	}

	@Override
	public double getPaddingX() {
		return 5;
	}

	@Override
	public double getPaddingY() {
		return 5;
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

		ug = symbolContext.apply(ug);
		final URectangle rect = new URectangle(x2, textHeight).rounded(roundCorner);
		rect.setDeltaShadow(symbolContext.getDeltaShadow());
		ug.draw(rect);
		ug = ug.apply(new UStroke());

		getTextBlock().drawU(ug.apply(new UTranslate(getMarginX1() + diffX / 2, getMarginY())));

	}
}
