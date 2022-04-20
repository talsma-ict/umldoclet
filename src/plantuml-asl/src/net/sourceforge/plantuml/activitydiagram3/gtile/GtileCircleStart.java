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

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignatureBasic;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;

public class GtileCircleStart extends AbstractGtile {

	private static final int SIZE = 20;

	private final HColor backColor;
	private double shadowing;

	private StyleSignatureBasic getDefaultStyleDefinitionCircle() {
		return StyleSignatureBasic.of(SName.root, SName.element, SName.activityDiagram, SName.circle);
	}

	public GtileCircleStart(StringBounder stringBounder, ISkinParam skinParam, HColor backColor, Swimlane swimlane) {
		super(stringBounder, skinParam, swimlane);
		this.backColor = backColor;
		final Style style = getDefaultStyleDefinitionCircle().getMergedStyle(skinParam().getCurrentStyleBuilder());
		this.shadowing = style.value(PName.Shadowing).asDouble();

	}

	@Override
	protected void drawUInternal(UGraphic ug) {
		final UEllipse circle = new UEllipse(SIZE, SIZE);
		circle.setDeltaShadow(shadowing);
		ug.apply(new HColorNone()).apply(backColor.bg()).draw(circle);
	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(SIZE, SIZE);
	}

}
