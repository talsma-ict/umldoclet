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
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.SkinParam;
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
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class ComponentRoseParticipant extends AbstractTextualComponent {

	private final HColor back;
	private final HColor foregroundColor;
	private final double deltaShadow;
	private final double roundCorner;
	private final double diagonalCorner;
	private final UStroke stroke;
	private final double minWidth;
	private final boolean collections;
	private final double padding;

	public ComponentRoseParticipant(Style style, Style stereo, SymbolContext biColor, FontConfiguration font,
			Display stringsToDisplay, ISkinSimple spriteContainer, double roundCorner, double diagonalCorner,
			UFont fontForStereotype, HColor htmlColorForStereotype, double minWidth, boolean collections,
			double padding) {
		super(style, stereo, LineBreakStrategy.NONE, stringsToDisplay, font, HorizontalAlignment.CENTER, 7, 7, 7,
				spriteContainer, false, fontForStereotype, htmlColorForStereotype);
		if (SkinParam.USE_STYLES()) {
			this.roundCorner = style.value(PName.RoundCorner).asInt();
			this.diagonalCorner = style.value(PName.DiagonalCorner).asInt();
			biColor = style.getSymbolContext(getIHtmlColorSet());
			this.stroke = style.getStroke();
		} else {
			this.roundCorner = roundCorner;
			this.diagonalCorner = diagonalCorner;
			this.stroke = biColor.getStroke();
		}
		this.padding = padding;
		this.minWidth = minWidth;
		this.collections = collections;
		this.back = biColor.getBackColor();
		this.deltaShadow = biColor.getDeltaShadow();
		this.foregroundColor = biColor.getForeColor();
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		final StringBounder stringBounder = ug.getStringBounder();
		ug = ug.apply(UTranslate.dx(padding));
		ug = ug.apply(back.bg()).apply(foregroundColor);
		ug = ug.apply(stroke);
		final Shadowable rect = new URectangle(getTextWidth(stringBounder), getTextHeight(stringBounder))
				.rounded(roundCorner).diagonalCorner(diagonalCorner);
		rect.setDeltaShadow(deltaShadow);
		if (collections) {
			ug.apply(UTranslate.dx(getDeltaCollection())).draw(rect);
			ug = ug.apply(UTranslate.dy(getDeltaCollection()));
		}
		ug.draw(rect);
		ug = ug.apply(new UStroke());
		final TextBlock textBlock = getTextBlock();
		textBlock.drawU(ug.apply(new UTranslate(getMarginX1() + suppWidth(stringBounder) / 2, getMarginY())));
	}

	private double getDeltaCollection() {
		if (collections) {
			return 4;
		}
		return 0;
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder) + deltaShadow + 1 + getDeltaCollection();
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return getTextWidth(stringBounder) + deltaShadow + getDeltaCollection() + 2 * padding;
	}

	@Override
	protected double getPureTextWidth(StringBounder stringBounder) {
		return Math.max(super.getPureTextWidth(stringBounder), minWidth);
	}

	private final double suppWidth(StringBounder stringBounder) {
		return getPureTextWidth(stringBounder) - super.getPureTextWidth(stringBounder);
	}

}
