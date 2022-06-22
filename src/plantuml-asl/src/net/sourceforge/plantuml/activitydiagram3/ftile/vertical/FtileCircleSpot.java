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
package net.sourceforge.plantuml.activitydiagram3.ftile.vertical;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class FtileCircleSpot extends AbstractFtile {

	private static final int SIZE = 20;

	private final Swimlane swimlane;
	private final String spot;
	private final FontConfiguration fc;
	private final HColor backColor;
	private final Style style;

	public FtileCircleSpot(ISkinParam skinParam, Swimlane swimlane, String spot, UFont font, HColor backColor,
			Style style) {
		super(skinParam);
		this.style = style;
		this.spot = spot;
		this.swimlane = swimlane;
		this.backColor = backColor;
		this.fc = FontConfiguration.create(skinParam, FontParam.ACTIVITY, null);
	}

	@Override
	public Collection<Ftile> getMyChildren() {
		return Collections.emptyList();
	}

	public Set<Swimlane> getSwimlanes() {
		if (swimlane == null)
			return Collections.emptySet();

		return Collections.singleton(swimlane);
	}

	public Swimlane getSwimlaneIn() {
		return swimlane;
	}

	public Swimlane getSwimlaneOut() {
		return swimlane;
	}

	public void drawU(UGraphic ug) {
		final UEllipse circle = new UEllipse(SIZE, SIZE);

		final HColor backColor = this.backColor == null
				? style.value(PName.BackGroundColor).asColor(skinParam().getThemeStyle(), getIHtmlColorSet())
				: this.backColor;
		final HColor borderColor = style.value(PName.LineColor).asColor(skinParam().getThemeStyle(),
				getIHtmlColorSet());
		final double shadow = style.value(PName.Shadowing).asDouble();

		circle.setDeltaShadow(shadow);
		ug.apply(borderColor).apply(backColor.bg()).apply(getThickness(style)).draw(circle);

		ug.apply(fc.getColor()).apply(new UTranslate(SIZE / 2, SIZE / 2))
				.draw(new UCenteredCharacter(spot.charAt(0), fc.getFont()));

	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		return new FtileGeometry(SIZE, SIZE, SIZE / 2, 0, SIZE);
	}

}
