/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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

import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Hexagon;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.klimt.UTranslate;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.drawing.UGraphic;
import net.sourceforge.plantuml.klimt.font.StringBounder;
import net.sourceforge.plantuml.klimt.geom.XDimension2D;
import net.sourceforge.plantuml.klimt.shape.TextBlock;
import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
import net.sourceforge.plantuml.style.ISkinParam;

public class FtileDiamondInside2 extends FtileDiamondWIP {

	public FtileDiamondInside2(TextBlock label, ISkinParam skinParam, HColor backColor, HColor borderColor,
			Swimlane swimlane) {
		this(label, skinParam, backColor, borderColor, swimlane, TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0),
				TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0));
	}

	public FtileDiamondInside2 withNorth(TextBlock north) {
		return new FtileDiamondInside2(label, skinParam(), backColor, borderColor, swimlane, north, south, west, east);
	}

	public FtileDiamondInside2 withWest(TextBlock west) {
		return new FtileDiamondInside2(label, skinParam(), backColor, borderColor, swimlane, north, south, west, east);
	}

	public FtileDiamondInside2 withEast(TextBlock east) {
		return new FtileDiamondInside2(label, skinParam(), backColor, borderColor, swimlane, north, south, west, east);
	}

	public FtileDiamondInside2 withSouth(TextBlock south) {
		return new FtileDiamondInside2(label, skinParam(), backColor, borderColor, swimlane, north, south, west, east);
	}

	private FtileDiamondInside2(TextBlock label, ISkinParam skinParam, HColor backColor, HColor borderColor,
			Swimlane swimlane, TextBlock north, TextBlock south, TextBlock west, TextBlock east) {
		super(label, skinParam, backColor, borderColor, swimlane, north, south, east, west);
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final XDimension2D dimLabel = label.calculateDimension(stringBounder);
		final XDimension2D dimTotal = calculateDimensionAlone(stringBounder);
		ug = ug.apply(borderColor).apply(getThickness(getStyle())).apply(backColor.bg());
		ug.draw(Hexagon.asPolygon(shadowing, dimTotal.getWidth(), dimTotal.getHeight()));

		north.drawU(ug.apply(new UTranslate(4 + dimTotal.getWidth() / 2, dimTotal.getHeight())));
		south.drawU(ug.apply(new UTranslate(4 + dimTotal.getWidth() / 2, dimTotal.getHeight())));

		final double lx = (dimTotal.getWidth() - dimLabel.getWidth()) / 2;
		final double ly = (dimTotal.getHeight() - dimLabel.getHeight()) / 2;
		label.drawU(ug.apply(new UTranslate(lx, ly)));

		final XDimension2D dimWeat = west.calculateDimension(stringBounder);
		west.drawU(ug.apply(new UTranslate(-dimWeat.getWidth(), -dimWeat.getHeight() + dimTotal.getHeight() / 2)));

		final XDimension2D dimEast = east.calculateDimension(stringBounder);
		east.drawU(ug.apply(new UTranslate(dimTotal.getWidth(), -dimEast.getHeight() + dimTotal.getHeight() / 2)));

	}

	private FtileGeometry calculateDimensionAlone(StringBounder stringBounder) {
		final XDimension2D dimLabel = label.calculateDimension(stringBounder);
		final XDimension2D dim;
		if (dimLabel.getWidth() == 0 || dimLabel.getHeight() == 0) {
			dim = new XDimension2D(Hexagon.hexagonHalfSize * 2, Hexagon.hexagonHalfSize * 2);
		} else {
			dim = dimLabel.atLeast(Hexagon.hexagonHalfSize * 2, Hexagon.hexagonHalfSize * 2)
					.delta(Hexagon.hexagonHalfSize * 2, 0);
		}
		return new FtileGeometry(dim, dim.getWidth() / 2, 0, dim.getHeight());
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		final XDimension2D diamond = calculateDimensionAlone(stringBounder);
		final XDimension2D north = this.north.calculateDimension(stringBounder);
		final double height = diamond.getHeight() + north.getHeight();
		final double left = diamond.getWidth() / 2;
		final double width = north.getWidth() > left ? left + north.getWidth() : diamond.getWidth();
		return new FtileGeometry(width, height, left, 0, diamond.getHeight());
	}

}
