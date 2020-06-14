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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactoryDelegator;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileMinWidth;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.cond.FtileSwitchNude;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.cond.FtileSwitchWithDiamonds;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.cond.FtileSwitchWithOneLink;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.cond.FtileSwitchWithManyLinks;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDiamondInside;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorAndStyle;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;

public class FtileFactoryDelegatorSwitch extends FtileFactoryDelegator {

	public FtileFactoryDelegatorSwitch(FtileFactory factory) {
		super(factory);
	}

	@Override
	public Ftile createSwitch(Swimlane swimlane, List<Branch> branches, LinkRendering afterEndwhile,
			LinkRendering topInlinkRendering, Display labelTest) {
		// return createNude(swimlane, branches);
		// return createWithDiamonds(swimlane, branches, labelTest);
		return createWithLinks(swimlane, branches, labelTest);
	}

	private Ftile createNude(Swimlane swimlane, List<Branch> branches) {
		final List<Ftile> ftiles = new ArrayList<Ftile>();
		for (Branch branch : branches) {
			ftiles.add(new FtileMinWidth(branch.getFtile(), 30));
		}
		return new FtileSwitchNude(ftiles, swimlane);
	}

	private Ftile createWithDiamonds(Swimlane swimlane, List<Branch> branches, Display labelTest) {
		final List<Ftile> ftiles = new ArrayList<Ftile>();
		for (Branch branch : branches) {
			ftiles.add(new FtileMinWidth(branch.getFtile(), 30));
		}
		final Ftile diamond1 = getDiamond1(swimlane, branches.get(0), labelTest);
		final Ftile diamond2 = getDiamond2(swimlane, branches.get(0));

		return new FtileSwitchWithDiamonds(ftiles, branches, swimlane, diamond1, diamond2, getStringBounder());
	}

	private Ftile createWithLinks(Swimlane swimlane, List<Branch> branches, Display labelTest) {
		final List<Ftile> ftiles = new ArrayList<Ftile>();
		final Ftile diamond1 = getDiamond1(swimlane, branches.get(0), labelTest);
		final Ftile diamond2 = getDiamond2(swimlane, branches.get(0));
		for (Branch branch : branches) {
			ftiles.add(new FtileMinWidth(branch.getFtile(), 30));
		}
		final Rainbow arrowColor = HtmlColorAndStyle.build(skinParam());
		if (ftiles.size() == 1) {
			final FtileSwitchWithOneLink result = new FtileSwitchWithOneLink(ftiles, branches, swimlane, diamond1,
					diamond2, getStringBounder(), arrowColor);
			return result.addLinks();
		}
		final FtileSwitchWithManyLinks result = new FtileSwitchWithManyLinks(ftiles, branches, swimlane, diamond1,
				diamond2, getStringBounder(), arrowColor);
		return result.addLinks();

	}

	private Ftile getDiamond1(Swimlane swimlane, Branch branch0, Display test) {
		final HtmlColor borderColor = getRose().getHtmlColor(skinParam(), ColorParam.activityDiamondBorder);
		final HtmlColor backColor = branch0.getColor() == null ? getRose().getHtmlColor(skinParam(),
				ColorParam.activityDiamondBackground) : branch0.getColor();

		final FontConfiguration fcDiamond = new FontConfiguration(skinParam(), FontParam.ACTIVITY_DIAMOND, null);

		final TextBlock tbTest = (Display.isNull(test) || test.isWhite()) ? TextBlockUtils.empty(0, 0) : test.create(
				fcDiamond, branch0.skinParam().getDefaultTextAlignment(HorizontalAlignment.LEFT), branch0.skinParam());

		return new FtileDiamondInside(branch0.skinParam(), backColor, borderColor, swimlane, tbTest);
	}

	private Ftile getDiamond2(Swimlane swimlane, Branch branch0) {
		final HtmlColor borderColor = getRose().getHtmlColor(skinParam(), ColorParam.activityDiamondBorder);
		final HtmlColor backColor = branch0.getColor() == null ? getRose().getHtmlColor(skinParam(),
				ColorParam.activityDiamondBackground) : branch0.getColor();

		return new FtileDiamondInside(branch0.skinParam(), backColor, borderColor, swimlane, TextBlockUtils.empty(0, 0));
	}

	private HtmlColor fontColor(FontParam param) {
		return skinParam().getFontHtmlColor(null, param);
	}

}
