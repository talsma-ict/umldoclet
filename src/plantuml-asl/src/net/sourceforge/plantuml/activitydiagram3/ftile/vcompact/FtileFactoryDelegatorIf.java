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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.Pragma;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactoryDelegator;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.cond.ConditionalBuilder;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.svek.ConditionEndStyle;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class FtileFactoryDelegatorIf extends FtileFactoryDelegator {

	private final Pragma pragma;

	public FtileFactoryDelegatorIf(FtileFactory factory, Pragma pragma) {
		super(factory);
		this.pragma = pragma;
	}

	@Override
	public Ftile createIf(Swimlane swimlane, List<Branch> thens, Branch elseBranch, LinkRendering afterEndwhile,
			LinkRendering topInlinkRendering, Url url) {

		final ConditionStyle conditionStyle = skinParam().getConditionStyle();
		final ConditionEndStyle conditionEndStyle = skinParam().getConditionEndStyle();
		final Branch branch0 = thens.get(0);

		final HColor borderColor;
		final HColor backColor;
		final Rainbow arrowColor;
		final FontConfiguration fcTest;
		final FontParam testParam = conditionStyle == ConditionStyle.INSIDE_HEXAGON ? FontParam.ACTIVITY_DIAMOND
				: FontParam.ARROW;
		final FontConfiguration fcArrow;
		if (UseStyle.useBetaStyle()) {
			final Style styleArrow = getDefaultStyleDefinitionArrow()
					.getMergedStyle(skinParam().getCurrentStyleBuilder());
			final Style styleDiamond = getDefaultStyleDefinitionDiamond()
					.getMergedStyle(skinParam().getCurrentStyleBuilder());
			borderColor = styleDiamond.value(PName.LineColor).asColor(skinParam().getThemeStyle(),
					skinParam().getIHtmlColorSet());
			backColor = branch0.getColor() == null ? styleDiamond.value(PName.BackGroundColor)
					.asColor(skinParam().getThemeStyle(), skinParam().getIHtmlColorSet()) : branch0.getColor();
			arrowColor = Rainbow.build(styleArrow, skinParam().getIHtmlColorSet(), skinParam().getThemeStyle());
			fcTest = styleDiamond.getFontConfiguration(skinParam().getThemeStyle(), skinParam().getIHtmlColorSet());
			fcArrow = styleArrow.getFontConfiguration(skinParam().getThemeStyle(), skinParam().getIHtmlColorSet());
		} else {
			borderColor = getRose().getHtmlColor(skinParam(), ColorParam.activityDiamondBorder);
			backColor = branch0.getColor() == null
					? getRose().getHtmlColor(skinParam(), ColorParam.activityDiamondBackground)
					: branch0.getColor();
			arrowColor = Rainbow.build(skinParam());
			fcTest = new FontConfiguration(skinParam(), testParam, null)
					.changeColor(fontColor(FontParam.ACTIVITY_DIAMOND));
			fcArrow = new FontConfiguration(skinParam(), FontParam.ARROW, null);
		}

		if (thens.size() > 1) {
			if (pragma.useVerticalIf()/* OptionFlags.USE_IF_VERTICAL */)
				return FtileIfLongVertical.create(swimlane, borderColor, backColor, arrowColor, getFactory(),
						conditionStyle, thens, elseBranch, fcArrow, topInlinkRendering, afterEndwhile);
			return FtileIfLongHorizontal.create(swimlane, borderColor, backColor, arrowColor, getFactory(),
					conditionStyle, thens, elseBranch, fcArrow, topInlinkRendering, afterEndwhile, fcTest);
		}
		return ConditionalBuilder.create(swimlane, borderColor, backColor, arrowColor, getFactory(), conditionStyle,
				conditionEndStyle, thens.get(0), elseBranch, skinParam(), getStringBounder(), fcArrow, fcTest, url);
	}

	private HColor fontColor(FontParam param) {
		return skinParam().getFontHtmlColor(null, param);
	}

}
