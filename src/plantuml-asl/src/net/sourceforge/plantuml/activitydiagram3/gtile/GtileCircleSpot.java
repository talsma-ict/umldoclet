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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class GtileCircleSpot extends AbstractGtile {

	private static final int SIZE = 20;

	private final String spot;
	private final FontConfiguration fc;
	private final HColor backColor;
	private double shadowing;

	private StyleSignature getDefaultStyleDefinitionCircle() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.circle);
	}

	public GtileCircleSpot(StringBounder stringBounder, ISkinParam skinParam, HColor backColor, Swimlane swimlane,
			String spot, UFont font) {
		super(stringBounder, skinParam, swimlane);
		this.spot = spot;
		this.backColor = backColor;
		this.fc = new FontConfiguration(skinParam, FontParam.ACTIVITY, null);
		if (UseStyle.useBetaStyle()) {
			final Style style = getDefaultStyleDefinitionCircle().getMergedStyle(skinParam().getCurrentStyleBuilder());
			this.shadowing = style.value(PName.Shadowing).asDouble();
		} else if (skinParam().shadowing(null))
			this.shadowing = 3;

	}

	@Override
	protected void drawUInternal(UGraphic ug) {

		final HColor borderColor = SkinParamUtils.getColor(skinParam(), null, ColorParam.activityBorder);
		final HColor backColor = this.backColor == null
				? SkinParamUtils.getColor(skinParam(), null, ColorParam.activityBackground)
				: this.backColor;

		final UEllipse circle = new UEllipse(SIZE, SIZE);
		circle.setDeltaShadow(shadowing);
		ug.apply(borderColor).apply(backColor.bg()).apply(getThickness()).draw(circle);

		ug.apply(fc.getColor()).apply(new UTranslate(SIZE / 2, SIZE / 2))
				.draw(new UCenteredCharacter(spot.charAt(0), fc.getFont()));

	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(SIZE, SIZE);
	}

}
