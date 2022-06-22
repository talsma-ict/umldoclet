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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileMinWidthCentered;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDiamondInside2;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

class FtileSwitch extends AbstractFtile {

	private final double xSeparation = 20;

	private final List<Ftile> tiles;

	private final Rainbow arrowColor;

	private FtileSwitch(List<Double> inlabelSizes, List<Ftile> tiles, Rainbow arrowColor) {
		super(tiles.get(0).skinParam());
		this.tiles = new ArrayList<>(tiles);
		this.arrowColor = arrowColor;

	}

	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<>();
		if (getSwimlaneIn() != null)
			result.add(getSwimlaneIn());

		return Collections.unmodifiableSet(result);
	}

	public Swimlane getSwimlaneIn() {
		return tiles.get(0).getSwimlaneIn();
	}

	public Swimlane getSwimlaneOut() {
		return getSwimlaneIn();
	}

	static Ftile create(Swimlane swimlane, HColor borderColor, HColor backColor, Rainbow arrowColor,
			FtileFactory ftileFactory, ConditionStyle conditionStyle, List<Branch> thens, FontConfiguration fcArrow,
			LinkRendering topInlinkRendering, LinkRendering afterEndwhile, FontConfiguration fcTest) {
		Objects.requireNonNull(afterEndwhile);
		final List<Ftile> tiles = new ArrayList<>();

		for (Branch branch : thens)
			tiles.add(new FtileMinWidthCentered(branch.getFtile(), 30));

		List<Double> inlabelSizes = new ArrayList<>();
		for (Branch branch : thens) {
			final TextBlock tb1 = branch.getDisplayPositive().create(fcArrow, HorizontalAlignment.LEFT,
					ftileFactory.skinParam());
			final TextBlock tbTest = branch.getLabelTest().create(fcTest,
					ftileFactory.skinParam().getDefaultTextAlignment(HorizontalAlignment.LEFT),
					ftileFactory.skinParam());
			final HColor diamondColor = branch.getColor() == null ? backColor : branch.getColor();

			FtileDiamondInside2 diamond = new FtileDiamondInside2(tbTest, branch.skinParam(), diamondColor, borderColor,
					swimlane);
			TextBlock tbInlabel = null;
			if (Display.isNull(branch.getInlabel())) {
				inlabelSizes.add(0.0);
			} else {
				tbInlabel = branch.getInlabel().create(fcArrow, HorizontalAlignment.LEFT, ftileFactory.skinParam());
				inlabelSizes.add(tbInlabel.calculateDimension(ftileFactory.getStringBounder()).getWidth());
				diamond = diamond.withWest(tbInlabel);
			}
			diamond = diamond.withNorth(tb1);
		}

		return new FtileSwitch(inlabelSizes, tiles, arrowColor);

	}

	@Override
	public Collection<Ftile> getMyChildren() {
		final List<Ftile> result = new ArrayList<>(tiles);
		return Collections.unmodifiableList(result);
	}

	@Override
	public UTranslate getTranslateFor(Ftile child, StringBounder stringBounder) {
		if (tiles.contains(child))
			return getTranslate1(child, stringBounder);

		throw new UnsupportedOperationException();
	}

	private UTranslate getTranslate1(Ftile tile, StringBounder stringBounder) {
		double x1 = 0;
		for (Ftile candidate : tiles) {
			final FtileGeometry dim1 = candidate.calculateDimension(stringBounder);
			if (candidate == tile)
				return new UTranslate(x1, 25);

			x1 += dim1.getWidth() + xSeparation;
		}
		throw new IllegalArgumentException();
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		for (Ftile tile : tiles)
			ug.apply(getTranslate1(tile, stringBounder)).draw(tile);

	}

	private FtileGeometry calculateDimensionInternal(StringBounder stringBounder) {
		Dimension2D result = new Dimension2DDouble(0, 0);
		for (Ftile couple : tiles)
			result = Dimension2DDouble.mergeLR(result, couple.calculateDimension(stringBounder));

		result = Dimension2DDouble.delta(result, xSeparation * (tiles.size() - 1), 100);

		return new FtileGeometry(result, result.getWidth() / 2, 0);
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);

		final List<Ftile> all = new ArrayList<>(tiles);
		for (Ftile tmp : all)
			if (tmp.calculateDimension(stringBounder).hasPointOut())
				return new FtileGeometry(dimTotal, dimTotal.getWidth() / 2, 0, dimTotal.getHeight());

		return new FtileGeometry(dimTotal, dimTotal.getWidth() / 2, 0);

	}

}
