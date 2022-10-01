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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.Hexagon;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class GtileHexagonInside extends AbstractGtile {

	protected final HColor backColor;
	protected final HColor borderColor;

	protected final TextBlock label;
	protected final XDimension2D dimLabel;

	protected final double shadowing;

	final public StyleSignatureBasic getDefaultStyleDefinition() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.activity, SName.diamond);
	}

	// FtileDiamondInside

	public GtileHexagonInsideLabelled withEastLabel(TextBlock eastLabel) {
		return new GtileHexagonInsideLabelled(this, TextBlockUtils.EMPTY_TEXT_BLOCK, eastLabel,
				TextBlockUtils.EMPTY_TEXT_BLOCK);
	}

	public GtileHexagonInsideLabelled withWestLabel(TextBlock westLabel) {
		return new GtileHexagonInsideLabelled(this, TextBlockUtils.EMPTY_TEXT_BLOCK, TextBlockUtils.EMPTY_TEXT_BLOCK,
				westLabel);
	}

	public AbstractGtileRoot withSouthLabel(TextBlock southLabel) {
		return new GtileHexagonInsideLabelled(this, southLabel, TextBlockUtils.EMPTY_TEXT_BLOCK,
				TextBlockUtils.EMPTY_TEXT_BLOCK);
	}

	public GtileHexagonInside(StringBounder stringBounder, TextBlock label, ISkinParam skinParam, HColor backColor,
			HColor borderColor, Swimlane swimlane) {
		super(stringBounder, skinParam, swimlane);

		final Style style = getDefaultStyleDefinition().getMergedStyle(skinParam.getCurrentStyleBuilder());
		this.borderColor = style.value(PName.LineColor).asColor(getIHtmlColorSet());
		this.backColor = style.value(PName.BackGroundColor).asColor(getIHtmlColorSet());
		this.shadowing = style.value(PName.Shadowing).asDouble();

		this.label = label;
		this.dimLabel = label.calculateDimension(stringBounder);

	}

	@Override
	public XDimension2D calculateDimension(StringBounder stringBounder) {
		final XDimension2D dim;
		if (dimLabel.getWidth() == 0 || dimLabel.getHeight() == 0) {
			dim = new XDimension2D(Hexagon.hexagonHalfSize * 2, Hexagon.hexagonHalfSize * 2);
		} else {
			dim = XDimension2D.delta(
					XDimension2D.atLeast(dimLabel, Hexagon.hexagonHalfSize * 2, Hexagon.hexagonHalfSize * 2),
					Hexagon.hexagonHalfSize * 2, 0);
		}
		return dim;
	}

	@Override
	protected void drawUInternal(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dimTotal = calculateDimension(stringBounder);
		ug = ug.apply(borderColor).apply(getThickness()).apply(backColor.bg());
		ug.draw(Hexagon.asPolygon(shadowing, dimTotal.getWidth(), dimTotal.getHeight()));

		final double lx = (dimTotal.getWidth() - dimLabel.getWidth()) / 2;
		final double ly = (dimTotal.getHeight() - dimLabel.getHeight()) / 2;
		label.drawU(ug.apply(new UTranslate(lx, ly)));
	}

}
