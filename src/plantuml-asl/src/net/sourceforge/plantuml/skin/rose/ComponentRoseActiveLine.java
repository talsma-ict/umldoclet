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
package net.sourceforge.plantuml.skin.rose;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.graphic.IHtmlColorSet;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.skin.AbstractComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ComponentRoseActiveLine extends AbstractComponent {

	private final SymbolContext symbolContext;
	private final boolean closeUp;
	private final boolean closeDown;

	public ComponentRoseActiveLine(Style style, SymbolContext symbolContext, boolean closeUp, boolean closeDown,
			IHtmlColorSet set) {
		super(style);
		if (SkinParam.USE_STYLES()) {
			symbolContext = style.getSymbolContext(set);
		}
		this.symbolContext = symbolContext;
		this.closeUp = closeUp;
		this.closeDown = closeDown;
	}

	protected void drawInternalU(UGraphic ug, Area area) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		final StringBounder stringBounder = ug.getStringBounder();
		final int x = (int) (dimensionToUse.getWidth() - getPreferredWidth(stringBounder)) / 2;

		final URectangle rect = new URectangle(getPreferredWidth(stringBounder), dimensionToUse.getHeight());
		if (symbolContext.isShadowing()) {
			rect.setDeltaShadow(1);
		}
		ug = ug.apply(new UChangeColor(symbolContext.getForeColor()));
		if (closeUp && closeDown) {
			ug.apply(new UChangeBackColor(symbolContext.getBackColor())).apply(new UTranslate(x, 0)).draw(rect);
			return;
		}
		ug.apply(new UChangeBackColor(symbolContext.getBackColor()))
				.apply(new UChangeColor(symbolContext.getBackColor())).apply(new UTranslate(x, 0)).draw(rect);

		final ULine vline = new ULine(0, dimensionToUse.getHeight());
		ug.apply(new UTranslate(x, 0)).draw(vline);
		ug.apply(new UTranslate(x + getPreferredWidth(stringBounder), 0)).draw(vline);

		final ULine hline = new ULine(getPreferredWidth(stringBounder), 0);
		if (closeUp) {
			ug.apply(new UTranslate(x, 0)).draw(hline);
		}
		if (closeDown) {
			ug.apply(new UTranslate(x, dimensionToUse.getHeight())).draw(hline);
		}
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return 0;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return 10;
	}
}
