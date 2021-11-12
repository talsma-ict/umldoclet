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

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Hexagon;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;

public class FtileDiamondSquare extends FtileDiamondWIP {

	public FtileDiamondSquare(TextBlock label, ISkinParam skinParam, HColor backColor, HColor borderColor,
			Swimlane swimlane) {
		this(label, skinParam, backColor, borderColor, swimlane, TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0),
				TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0));
	}

	public FtileDiamondSquare withNorth(TextBlock north) {
		return new FtileDiamondSquare(label, skinParam(), backColor, borderColor, swimlane, north, west, east, south);
	}

	public FtileDiamondSquare withWest(TextBlock west) {
		return new FtileDiamondSquare(label, skinParam(), backColor, borderColor, swimlane, north, west, east, south);
	}

	public FtileDiamondSquare withEast(TextBlock east) {
		return new FtileDiamondSquare(label, skinParam(), backColor, borderColor, swimlane, north, west, east, south);
	}

	public FtileDiamondSquare withSouth(TextBlock south) {
		return new FtileDiamondSquare(label, skinParam(), backColor, borderColor, swimlane, north, west, east, south);
	}

	public Ftile withWestAndEast(TextBlock tb1, TextBlock tb2) {
		return withWest(tb1).withEast(tb2);
	}

	private FtileDiamondSquare(TextBlock label, ISkinParam skinParam, HColor backColor, HColor borderColor,
			Swimlane swimlane, TextBlock north, TextBlock west, TextBlock east, TextBlock south) {
		super(label, skinParam, backColor, borderColor, swimlane, north, south, east, west);
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimLabel = label.calculateDimension(stringBounder);
		final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);
		ug = ug.apply(borderColor).apply(getThickness()).apply(backColor.bg());
		ug.draw(Hexagon.asPolygonSquare(skinParam().shadowing(null), dimTotal.getWidth(), dimTotal.getHeight()));

		// Fix why north and south are the same
		north.drawU(ug.apply(new UTranslate(4 + dimTotal.getWidth() / 2, dimTotal.getHeight())));
		south.drawU(ug.apply(new UTranslate(4 + dimTotal.getWidth() / 2, dimTotal.getHeight())));

		final double lx = (dimTotal.getWidth() - dimLabel.getWidth()) / 2;
		final double ly = (dimTotal.getHeight() - dimLabel.getHeight()) / 2;
		label.drawU(ug.apply(new UTranslate(lx, ly)));

		final Dimension2D dimWeat = west.calculateDimension(stringBounder);
		west.drawU(ug.apply(new UTranslate(-dimWeat.getWidth(), -dimWeat.getHeight() + Hexagon.hexagonHalfSize)));

		final Dimension2D dimEast = east.calculateDimension(stringBounder);
		east.drawU(ug.apply(new UTranslate(dimTotal.getWidth(), -dimEast.getHeight() + Hexagon.hexagonHalfSize)));

	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		final Dimension2D dim = calculateDimensionInternal(stringBounder);
		return new FtileGeometry(dim, dim.getWidth() / 2, 0, dim.getHeight());
	}

	private Dimension2D calculateDimensionInternal(StringBounder stringBounder) {
		final Dimension2D dimLabel = label.calculateDimension(stringBounder);
		if (dimLabel.getWidth() == 0 || dimLabel.getHeight() == 0) {
			return new Dimension2DDouble(Hexagon.hexagonHalfSize * 2, Hexagon.hexagonHalfSize * 2);
		}
		Dimension2D result = dimLabel;
		result = Dimension2DDouble.delta(result, Hexagon.hexagonHalfSize * 2, Hexagon.hexagonHalfSize * 2);
		return result;
	}

}
