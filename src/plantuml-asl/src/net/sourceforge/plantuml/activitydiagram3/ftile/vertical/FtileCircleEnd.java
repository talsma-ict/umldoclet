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
package net.sourceforge.plantuml.activitydiagram3.ftile.vertical;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class FtileCircleEnd extends AbstractFtile {

	private static final int SIZE = 20;

	private final HColor backColor;
	private final Swimlane swimlane;
	private double shadowing;

	@Override
	public Collection<Ftile> getMyChildren() {
		return Collections.emptyList();
	}

	public FtileCircleEnd(ISkinParam skinParam, HColor backColor, Swimlane swimlane, Style style) {
		super(skinParam);
		this.backColor = backColor;
		this.swimlane = swimlane;
		if (SkinParam.USE_STYLES()) {
			this.shadowing = style.value(PName.Shadowing).asDouble();
		} else {
			if (skinParam().shadowing(null)) {
				this.shadowing = 3;
			}
		}
	}

	public Set<Swimlane> getSwimlanes() {
		if (swimlane == null) {
			return Collections.emptySet();
		}
		return Collections.singleton(swimlane);
	}

	public Swimlane getSwimlaneIn() {
		return swimlane;
	}

	public Swimlane getSwimlaneOut() {
		return swimlane;
	}

	public void drawU(UGraphic ug) {
		double xTheoricalPosition = 0;
		double yTheoricalPosition = 0;
		xTheoricalPosition = Math.round(xTheoricalPosition);
		yTheoricalPosition = Math.round(yTheoricalPosition);

		final UEllipse circle = new UEllipse(SIZE, SIZE);
		circle.setDeltaShadow(shadowing);
		ug = ug.apply(backColor);
		final double thickness = 2.5;
		ug.apply(HColorUtils.WHITE.bg()).apply(new UStroke(1.5))
				.apply(new UTranslate(xTheoricalPosition, yTheoricalPosition)).draw(circle);

		final double size2 = (SIZE - thickness) / Math.sqrt(2);
		final double delta = (SIZE - size2) / 2;
		ug = ug.apply(new UStroke(thickness));
		ug.apply(new UTranslate(delta, delta)).draw(new ULine(size2, size2));
		ug.apply(new UTranslate(delta, SIZE - delta)).draw(new ULine(size2, -size2));

	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		return new FtileGeometry(SIZE, SIZE, SIZE / 2, 0);
	}

}
