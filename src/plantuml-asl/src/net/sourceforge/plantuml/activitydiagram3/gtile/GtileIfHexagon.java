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
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.style.SName;
import net.sourceforge.plantuml.style.StyleSignature;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GtileIfHexagon extends GtileColumns {

	private final Gtile shape1;
	private final List<Branch> branches;
	private final Gtile shape2;

	private final UTranslate positionShape1;
	private final UTranslate positionShape2;

	@Override
	public String toString() {
		return "GtileIfHexagon " + gtiles;
	}

	public static Gtile build(Swimlane swimlane, List<Gtile> gtiles, List<Branch> branches) {
		final GtileIfHexagon result = new GtileIfHexagon(swimlane, gtiles, branches);
		return result;
		// return Gtiles.withEastMargin(result, 0);
	}

	// ConditionalBuilder
	// FtileFactoryDelegatorIf

	private GtileIfHexagon(Swimlane swimlane, List<Gtile> gtiles, List<Branch> branches) {
		super(gtiles, swimlane, 20);

		this.branches = branches;

		this.shape1 = getShape1(swimlane);
		this.shape2 = Gtiles.diamondEmpty(swimlane, getStringBounder(), skinParam(), getDefaultStyleDefinitionDiamond(),
				branches.get(0).getColor());

		final Dimension2D dimShape1 = shape1.calculateDimension(stringBounder);
		this.pushDown(dimShape1.getHeight());

		final double posShape;
		final Dimension2D totalDim;
		if (branches.size() == 2) {
			final Gtile tile0 = branches.get(0).getGtile();
			final Gtile tile1 = branches.get(1).getGtile();

			final double northHook0 = tile0.getGPoint(GPoint.NORTH_HOOK).getCoord().getDx();
			final double width0 = tile0.calculateDimension(getStringBounder()).getWidth();
			final double northHook1 = tile1.getGPoint(GPoint.NORTH_HOOK).getCoord().getDx();
			final double width1 = tile1.calculateDimension(getStringBounder()).getWidth();

			final double shlen1 = shape1.calculateDimension(getStringBounder()).getWidth();
			final double shlen = shape1.getGPoint(GPoint.EAST_HOOK).getCoord().getDx()
					- shape1.getGPoint(GPoint.WEST_HOOK).getCoord().getDx();

			System.err.println("shlen=" + shlen);

			System.err.println("northHook0=" + northHook0);
			System.err.println("width0=" + width0);

			System.err.println("northHook1=" + northHook1);
			System.err.println("width1=" + width1);

			final double alpha = northHook0;
			final double beta = width1 - northHook1;
			System.err.println("alpha=" + alpha);
			System.err.println("beta=" + beta);

			final double wantedWidth = alpha + shlen + beta;
			System.err.println("wantedWidth=" + wantedWidth);
			double diff = wantedWidth - width0 - width1;
			if (diff < 0)
				diff = 0;
			System.err.println("diff=" + diff);
			this.setMargin(diff + 10);
			totalDim = calculateDimensionRaw(stringBounder);
			final double p2 = totalDim.getWidth() - beta;
			posShape = (alpha + p2) / 2;
		} else {
			totalDim = calculateDimensionRaw(stringBounder);
			posShape = totalDim.getWidth() / 2;
		}

		this.positionShape1 = UTranslate.dx(posShape).compose(shape1.getCoord(GPoint.NORTH_BORDER).reverse());
		this.positionShape2 = new UTranslate(posShape, totalDim.getHeight())
				.compose(shape2.getCoord(GPoint.SOUTH_BORDER).reverse());

	}

	@Override
	protected UTranslate getCoordImpl(String name) {
		if (name.equals(GPoint.NORTH_HOOK))
			return shape1.getCoord(name).compose(positionShape1);
		if (name.equals(GPoint.SOUTH_HOOK))
			return shape2.getCoord(name).compose(positionShape2);
		return super.getCoordImpl(name);
	}

	private Gtile getShape1(Swimlane swimlane) {
		GtileHexagonInside tmp = Gtiles.hexagonInside(swimlane, getStringBounder(), skinParam(),
				getDefaultStyleDefinitionDiamond(), branches.get(0).getColor(), branches.get(0).getLabelTest());

		final TextBlock tmp0 = branches.get(0).getTextBlockPositive();
		if (branches.size() == 1) {
			return Gtiles.withSouthMargin(tmp.withSouthLabel(tmp0), 10);
		}

		final TextBlock tmp1 = branches.get(1).getTextBlockPositive();
		return Gtiles.withSouthMargin(tmp.withWestLabel(tmp0).withEastLabel(tmp1), 10);
	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D rawDim = calculateDimensionRaw(stringBounder);
		if (branches.size() == 2) {
			final Dimension2D shape1Dim = shape1.calculateDimension(stringBounder);
			final double shape1max = this.positionShape1.getDx() + shape1Dim.getWidth();
			return Dimension2DDouble.atLeast(rawDim, shape1max, 0);

		}
		// return MathUtils.max(rawDim, shape1Dim);
		return rawDim;
	}

	private Dimension2D calculateDimensionRaw(StringBounder stringBounder) {
		final double height2 = shape2.calculateDimension(stringBounder).getHeight();
		final Dimension2D nude = super.calculateDimension(stringBounder);
		// +30 to be done only when branches.size()==1 ?
		return Dimension2DDouble.delta(nude, 0, height2 + 30);
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
	protected void drawUInternal(UGraphic ug) {
		super.drawUInternal(ug);

		ug.apply(positionShape1).draw(shape1);
		ug.apply(positionShape2).draw(shape2);
	}

//	private TextBlock getLabelPositive(Branch branch) {
//		final LineBreakStrategy labelLineBreak = LineBreakStrategy.NONE;
//
//		final FontConfiguration fontConfiguration;
//		if (UseStyle.useBetaStyle()) {
//			final Style style = getDefaultStyleDefinitionArrow().getMergedStyle(skinParam().getCurrentStyleBuilder());
//			fontConfiguration = style.getFontConfiguration(skinParam().getThemeStyle(), skinParam().getIHtmlColorSet());
//		} else {
//			fontConfiguration = new FontConfiguration(skinParam(), FontParam.ARROW, null);
//		}
//
//		return branch.getDisplayPositive().create0(fontConfiguration, HorizontalAlignment.LEFT, skinParam(),
//				labelLineBreak, CreoleMode.SIMPLE_LINE, null, null);
//	}

	@Override
	public Collection<GConnection> getInnerConnections() {
		if (branches.size() == 1) {
			final GConnection arrow1 = new GConnectionVerticalDown(positionShape1, shape1.getGPoint(GPoint.SOUTH_HOOK),
					getPosition(0), gtiles.get(0).getGPoint(GPoint.NORTH_HOOK), TextBlockUtils.EMPTY_TEXT_BLOCK);
			final GConnection arrow2 = new GConnectionVerticalDown(getPosition(0),
					gtiles.get(0).getGPoint(GPoint.SOUTH_HOOK), positionShape2, shape2.getGPoint(GPoint.NORTH_HOOK),
					TextBlockUtils.EMPTY_TEXT_BLOCK);
			return Arrays.asList(arrow1, arrow2);
		} else if (branches.size() == 2) {
			final GConnection arrow1 = new GConnectionHorizontalThenVerticalDown(positionShape1,
					shape1.getGPoint(GPoint.WEST_HOOK), getPosition(0), gtiles.get(0).getGPoint(GPoint.NORTH_HOOK),
					TextBlockUtils.EMPTY_TEXT_BLOCK);
			final GConnection arrow2 = new GConnectionHorizontalThenVerticalDown(positionShape1,
					shape1.getGPoint(GPoint.EAST_HOOK), getPosition(1), gtiles.get(1).getGPoint(GPoint.NORTH_HOOK),
					TextBlockUtils.EMPTY_TEXT_BLOCK);

			final GConnection arrow3 = new GConnectionVerticalDownThenHorizontal(getPosition(0),
					gtiles.get(0).getGPoint(GPoint.SOUTH_HOOK), positionShape2, shape2.getGPoint(GPoint.WEST_HOOK),
					TextBlockUtils.EMPTY_TEXT_BLOCK);
			final GConnection arrow4 = new GConnectionVerticalDownThenHorizontal(getPosition(1),
					gtiles.get(1).getGPoint(GPoint.SOUTH_HOOK), positionShape2, shape2.getGPoint(GPoint.EAST_HOOK),
					TextBlockUtils.EMPTY_TEXT_BLOCK);

			return Arrays.asList(arrow1, arrow2, arrow3, arrow4);
		}
		return super.getInnerConnections();
	}

}
