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
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColors;

public class ComponentRoseGroupingElse extends AbstractTextualComponent {

	private final HColor groupBorder;
	private final HColor backgroundColor;
	private final double roundCorner;

	public ComponentRoseGroupingElse(Style style, CharSequence comment, ISkinSimple spriteContainer) {
		super(style, LineBreakStrategy.NONE, 5, 5, 1, spriteContainer, comment == null ? null : "[" + comment + "]");

		this.roundCorner = style.value(PName.RoundCorner).asInt();
		this.groupBorder = style.value(PName.LineColor).asColor(getIHtmlColorSet());
		this.backgroundColor = style.value(PName.BackGroundColor).asColor(getIHtmlColorSet());
	}

	@Override
	protected void drawBackgroundInternalU(UGraphic ug, Area area) {
		if (backgroundColor.isTransparent())
			return;

		final XDimension2D dimensionToUse = area.getDimensionToUse();
		ug = ug.apply(HColors.none()).apply(backgroundColor.bg());
		final double width = dimensionToUse.getWidth();
		final double height = dimensionToUse.getHeight();
		final UShape rect;
		if (roundCorner == 0) {
			rect = new URectangle(width, height);
		} else {
			final UPath path = new UPath();
			path.moveTo(0, 0);
			path.lineTo(width, 0);

			path.lineTo(width, height - roundCorner / 2);
			path.arcTo(roundCorner / 2, roundCorner / 2, 0, 0, 1, width - roundCorner / 2, height);

			path.lineTo(roundCorner / 2, height);
			path.arcTo(roundCorner / 2, roundCorner / 2, 0, 0, 1, 0, height - roundCorner / 2);

			path.lineTo(0, 0);
			rect = path;
		}
		ug.draw(rect);
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		final XDimension2D dimensionToUse = area.getDimensionToUse();
		ug = ArrowConfiguration.stroke(ug, 2, 2, 1).apply(groupBorder);
		ug.apply(UTranslate.dy(1)).draw(ULine.hline(dimensionToUse.getWidth()));
		ug = ug.apply(new UStroke());
		getTextBlock().drawU(ug.apply(new UTranslate(getMarginX1(), getMarginY())));
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder);
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return getTextWidth(stringBounder);
	}

}
