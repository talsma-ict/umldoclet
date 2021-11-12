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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.LineBreakStrategy;
import net.sourceforge.plantuml.UseStyle;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.ftile.Hexagon;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.Parser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.creole.SheetBlock2;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.PName;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.svek.ConditionEndStyle;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class GtileIfHexagon extends GtileIfSimple {

	private final List<Branch> branches;
	private final Gtile shape1;
	private final Gtile shape2;

	private final UTranslate positionShape1;
	private final UTranslate positionShape2;

	@Override
	public String toString() {
		return "GtileIfHexagon " + gtiles;
	}

	// ConditionalBuilder
	// FtileFactoryDelegatorIf

	public GtileIfHexagon(Swimlane swimlane, List<Gtile> gtiles, List<Branch> branches) {
		super(gtiles);

		final ConditionStyle conditionStyle = skinParam().getConditionStyle();
		final ConditionEndStyle conditionEndStyle = skinParam().getConditionEndStyle();

		this.branches = branches;

		final Branch branch0 = branches.get(0);

		final HColor borderColor;
		final HColor backColor;
		final FontConfiguration fcTest;

		if (UseStyle.useBetaStyle()) {
			final Style styleArrow = getDefaultStyleDefinitionArrow()
					.getMergedStyle(skinParam().getCurrentStyleBuilder());
			final Style styleDiamond = getDefaultStyleDefinitionDiamond()
					.getMergedStyle(skinParam().getCurrentStyleBuilder());
			borderColor = styleDiamond.value(PName.LineColor).asColor(skinParam().getThemeStyle(),
					skinParam().getIHtmlColorSet());
			backColor = branch0.getColor() == null ? styleDiamond.value(PName.BackGroundColor)
					.asColor(skinParam().getThemeStyle(), skinParam().getIHtmlColorSet()) : branch0.getColor();
//			arrowColor = Rainbow.build(styleArrow, skinParam().getIHtmlColorSet(), skinParam().getThemeStyle());
			fcTest = styleDiamond.getFontConfiguration(skinParam().getThemeStyle(), skinParam().getIHtmlColorSet());
//			fcArrow = styleArrow.getFontConfiguration(skinParam().getThemeStyle(), skinParam().getIHtmlColorSet());
		} else {
			final FontParam testParam = conditionStyle == ConditionStyle.INSIDE_HEXAGON ? FontParam.ACTIVITY_DIAMOND
					: FontParam.ARROW;

			borderColor = getRose().getHtmlColor(skinParam(), ColorParam.activityDiamondBorder);
			backColor = branch0.getColor() == null
					? getRose().getHtmlColor(skinParam(), ColorParam.activityDiamondBackground)
					: branch0.getColor();
//			arrowColor = Rainbow.build(skinParam());
			fcTest = new FontConfiguration(skinParam(), testParam, null)
					.changeColor(fontColor(FontParam.ACTIVITY_DIAMOND));
//			fcArrow = new FontConfiguration(skinParam(), FontParam.ARROW, null);
		}

		final Sheet sheet = Parser.build(fcTest, skinParam().getDefaultTextAlignment(HorizontalAlignment.LEFT),
				skinParam(), CreoleMode.FULL).createSheet(branch0.getLabelTest());
		final SheetBlock1 sheetBlock1 = new SheetBlock1(sheet, LineBreakStrategy.NONE, skinParam().getPadding());
		final TextBlock tbTest = new SheetBlock2(sheetBlock1, Hexagon.asStencil(sheetBlock1), new UStroke());

		this.shape1 = new GtileDiamondInside(getStringBounder(), tbTest, skinParam(), backColor, borderColor, swimlane);
		this.shape2 = new GtileDiamondInside(getStringBounder(), TextBlockUtils.EMPTY_TEXT_BLOCK, skinParam(),
				backColor, borderColor, swimlane);

		final double height1 = shape1.calculateDimension(stringBounder).getHeight() + getSuppHeightMargin();

//		public GtileDiamondInside(StringBounder stringBounder, TextBlock label, ISkinParam skinParam, HColor backColor,
//				HColor borderColor, Swimlane swimlane) {

		for (ListIterator<UTranslate> it = positions.listIterator(); it.hasNext();) {
			final UTranslate tmp = it.next();
			it.set(tmp.compose(UTranslate.dy(height1)));
		}

		if (branches.size() == 1) {
			final UTranslate tmp = positions.get(0);
			positions.set(0, tmp.compose(UTranslate.dx(missingSpace())));
		}

		this.positionShape1 = this.getCoord(GPoint.NORTH_HOOK).compose(shape1.getCoord(GPoint.NORTH_HOOK).reverse());
		this.positionShape2 = this.getCoord(GPoint.SOUTH_HOOK).compose(shape2.getCoord(GPoint.SOUTH_HOOK).reverse());

	}

	private double missingSpace() {
		if (branches.size() != 1)
			throw new IllegalStateException();
		return 25;
	}

	private double getSuppHeightMargin() {
		if (branches.size() == 1)
			return 30;
		return 10;
	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final double height2 = shape2.calculateDimension(stringBounder).getHeight() + getSuppHeightMargin();
		final Dimension2D nude = super.calculateDimension(stringBounder);
		if (branches.size() > 1)
			return Dimension2DDouble.delta(nude, 0, height2);
		return Dimension2DDouble.delta(nude, missingSpace(), height2);
	}

	@Override
	public UTranslate getCoord(String name) {
		final UTranslate result = super.getCoord(name);
		return result;
	}

	private HColor fontColor(FontParam param) {
		return skinParam().getFontHtmlColor(null, param);
	}

	final public StyleSignature getDefaultStyleDefinitionActivity() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.activity);
	}

	final public StyleSignature getDefaultStyleDefinitionDiamond() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.activity, SName.diamond);
	}

	final public StyleSignature getDefaultStyleDefinitionArrow() {
		return StyleSignature.of(SName.root, SName.element, SName.activityDiagram, SName.arrow);
	}

	@Override
	public void drawU(UGraphic ug) {
		super.drawU(ug);

		shape1.drawU(ug.apply(positionShape1));
		shape2.drawU(ug.apply(positionShape2));
	}

	@Override
	public Collection<GConnection> getInnerConnections() {
		if (branches.size() == 1) {
			final GConnection arrow1 = new GConnectionVerticalDown(positionShape1, shape1.getGPoint(GPoint.SOUTH_HOOK),
					positions.get(0), gtiles.get(0).getGPoint(GPoint.NORTH_HOOK), TextBlockUtils.EMPTY_TEXT_BLOCK);
			final GConnection arrow2 = new GConnectionVerticalDown(positions.get(0),
					gtiles.get(0).getGPoint(GPoint.SOUTH_HOOK), positionShape2, shape2.getGPoint(GPoint.NORTH_HOOK),
					TextBlockUtils.EMPTY_TEXT_BLOCK);

			final Dimension2D totalDim = calculateDimension(stringBounder);

			final GConnection arrow3 = new GConnectionLeftThenDownThenRight(positionShape1,
					shape1.getGPoint(GPoint.EAST_HOOK), positionShape2, shape2.getGPoint(GPoint.EAST_HOOK), totalDim.getWidth(),
					TextBlockUtils.EMPTY_TEXT_BLOCK);
			return Arrays.asList(arrow1, arrow2, arrow3);
			// return Arrays.asList(arrow3);
		} else if (branches.size() == 2) {
			final GConnection arrow1 = new GConnectionHorizontalThenVerticalDown(positionShape1,
					shape1.getGPoint(GPoint.WEST_HOOK), positions.get(0), gtiles.get(0).getGPoint(GPoint.NORTH_HOOK),
					TextBlockUtils.EMPTY_TEXT_BLOCK);
			final GConnection arrow2 = new GConnectionHorizontalThenVerticalDown(positionShape1,
					shape1.getGPoint(GPoint.EAST_HOOK), positions.get(1), gtiles.get(1).getGPoint(GPoint.NORTH_HOOK),
					TextBlockUtils.EMPTY_TEXT_BLOCK);

			final GConnection arrow3 = new GConnectionVerticalDownThenHorizontal(positions.get(0),
					gtiles.get(0).getGPoint(GPoint.SOUTH_HOOK), positionShape2, shape2.getGPoint(GPoint.WEST_HOOK),
					TextBlockUtils.EMPTY_TEXT_BLOCK);
			final GConnection arrow4 = new GConnectionVerticalDownThenHorizontal(positions.get(1),
					gtiles.get(1).getGPoint(GPoint.SOUTH_HOOK), positionShape2, shape2.getGPoint(GPoint.EAST_HOOK),
					TextBlockUtils.EMPTY_TEXT_BLOCK);

			return Arrays.asList(arrow1, arrow2, arrow3, arrow4);
		}
		return super.getInnerConnections();
	}

}
