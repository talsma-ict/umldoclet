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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.Pragma;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.utils.MathUtils;

public class SwimlanesB extends SwimlanesA {

	public SwimlanesB(ISkinParam skinParam, Pragma pragma) {
		super(skinParam, pragma);
	}

	@Override
	protected void drawWhenSwimlanes(UGraphic ug, TextBlock full) {
		super.drawWhenSwimlanes(ug, full);
		double x2 = 0;

		final StringBounder stringBounder = ug.getStringBounder();

		HColor color = skinParam.getHtmlColor(ColorParam.swimlaneTitleBackground, null, false);
		if (SkinParam.USE_STYLES()) {
			color = getStyle().value(PName.BackGroundColor).asColor(skinParam.getIHtmlColorSet());
		}
		if (color != null) {
			final double titleHeight = getTitlesHeight(stringBounder);
			final URectangle back = new URectangle(getTitlesWidth(stringBounder), titleHeight).ignoreForCompressionOnX().ignoreForCompressionOnY();
			ug.apply(color.bg()).apply(color).draw(back);
		}
		for (Swimlane swimlane : swimlanes) {
			final TextBlock swTitle = getTitle(swimlane);
			final double titleWidth = swTitle.calculateDimension(stringBounder).getWidth();
			final double posTitle = x2 + (swimlane.getActualWidth() - titleWidth) / 2;
			swTitle.drawU(ug.apply(UTranslate.dx(posTitle)));
			x2 += swimlane.getActualWidth();
		}
	}

	private double getTitlesWidth(StringBounder stringBounder) {
		double x2 = 0;
		for (Swimlane swimlane : swimlanes) {
			x2 += swimlane.getActualWidth();
		}
		return x2;
	}

	private TextBlock getTitle(Swimlane swimlane) {
		final HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
		FontConfiguration fontConfiguration = new FontConfiguration(skinParam, FontParam.SWIMLANE_TITLE, null);
		if (SkinParam.USE_STYLES()) {
			fontConfiguration = getStyle().getFontConfiguration(skinParam.getIHtmlColorSet());
		}

		LineBreakStrategy wrap = getWrap();
		if (wrap.isAuto()) {
			wrap = new LineBreakStrategy("" + ((int) swimlane.getActualWidth()));
		}

		return swimlane.getDisplay().create9(fontConfiguration, horizontalAlignment, skinParam, wrap);
	}

	private LineBreakStrategy getWrap() {
		LineBreakStrategy wrap = skinParam.swimlaneWrapTitleWidth();
		if (wrap == LineBreakStrategy.NONE) {
			wrap = skinParam.wrapWidth();
		}
		return wrap;
	}

	@Override
	protected double swimlaneActualWidth(StringBounder stringBounder, double swimlaneWidth, Swimlane swimlane) {
		final double m1 = super.swimlaneActualWidth(stringBounder, swimlaneWidth, swimlane);
		if (getWrap().isAuto()) {
			return m1;
		}

		final double titleWidth = getTitle(swimlane).calculateDimension(stringBounder).getWidth();
		return MathUtils.max(m1, titleWidth + 2 * separationMargin());

	}

	@Override
	protected UTranslate getTitleHeightTranslate(final StringBounder stringBounder) {
		double titlesHeight = getTitlesHeight(stringBounder);
		return UTranslate.dy(titlesHeight > 0 ? titlesHeight + 5 : 0);
	}

	private double getTitlesHeight(StringBounder stringBounder) {
		double titlesHeight = 0;
		for (Swimlane swimlane : swimlanes) {
			final TextBlock swTitle = getTitle(swimlane);
			titlesHeight = Math.max(titlesHeight, swTitle.calculateDimension(stringBounder).getHeight());
		}
		return titlesHeight;
	}

}
