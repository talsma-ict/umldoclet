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
package net.sourceforge.plantuml.activitydiagram3.ftile.vertical;

import java.awt.geom.Dimension2D;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Diamond;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileOverpassing;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.utils.MathUtils;

public class FtileDiamondInside3 extends AbstractFtile implements FtileOverpassing {

	private final HtmlColor backColor;
	private final HtmlColor borderColor;
	private final Swimlane swimlane;
	private final TextBlock label;
	private final TextBlock west;
	private final TextBlock east;
	private final TextBlock north;
	private final TextBlock south;

	public FtileDiamondInside3(ISkinParam skinParam, HtmlColor backColor, HtmlColor borderColor, Swimlane swimlane,
			TextBlock label) {
		this(skinParam, backColor, borderColor, swimlane, label, TextBlockUtils.empty(0, 0),
				TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0));
	}

	public FtileDiamondInside3 withNorth(TextBlock north) {
		return new FtileDiamondInside3(skinParam(), backColor, borderColor, swimlane, label, north, south, west, east);
	}

	public FtileDiamondInside3 withWest(TextBlock west) {
		return new FtileDiamondInside3(skinParam(), backColor, borderColor, swimlane, label, north, south, west, east);
	}

	public FtileDiamondInside3 withEast(TextBlock east) {
		return new FtileDiamondInside3(skinParam(), backColor, borderColor, swimlane, label, north, south, west, east);
	}

	public FtileDiamondInside3 withSouth(TextBlock south) {
		return new FtileDiamondInside3(skinParam(), backColor, borderColor, swimlane, label, north, south, west, east);
	}

	private FtileDiamondInside3(ISkinParam skinParam, HtmlColor backColor, HtmlColor borderColor, Swimlane swimlane,
			TextBlock label, TextBlock north, TextBlock south, TextBlock west, TextBlock east) {
		super(skinParam);
		this.backColor = backColor;
		this.swimlane = swimlane;
		this.borderColor = borderColor;
		this.label = label;
		this.west = west;
		this.east = east;
		this.north = north;
		this.south = south;
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
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimLabel = label.calculateDimension(stringBounder);
		final Dimension2D dimTotal = calculateDimensionAlone(stringBounder);
		ug = ug.apply(new UChangeColor(borderColor)).apply(getThickness()).apply(new UChangeBackColor(backColor));
		ug.draw(Diamond.asPolygon(skinParam().shadowing(null), dimTotal.getWidth(), dimTotal.getHeight()));

		 north.drawU(ug.apply(new UTranslate(4 + dimTotal.getWidth() / 2, dimTotal.getHeight())));
		 south.drawU(ug.apply(new UTranslate(4 + dimTotal.getWidth() / 2, dimTotal.getHeight())));

		final double lx = (dimTotal.getWidth() - dimLabel.getWidth()) / 2;
		final double ly = (dimTotal.getHeight() - dimLabel.getHeight()) / 2;
		label.drawU(ug.apply(new UTranslate(lx, ly)));

		final Dimension2D dimWeat = west.calculateDimension(stringBounder);
		 west.drawU(ug.apply(new UTranslate(-dimWeat.getWidth(), -dimWeat.getHeight() + dimTotal.getHeight() / 2)));

		final Dimension2D dimEast = east.calculateDimension(stringBounder);
		east.drawU(ug.apply(new UTranslate(dimTotal.getWidth(), -dimEast.getHeight() + dimTotal.getHeight() / 2)));

	}

	private FtileGeometry calculateDimensionAlone(StringBounder stringBounder) {
		final Dimension2D dimLabel = label.calculateDimension(stringBounder);
		final Dimension2D dim;
		if (dimLabel.getWidth() == 0 || dimLabel.getHeight() == 0) {
			dim = new Dimension2DDouble(Diamond.diamondHalfSize * 2, Diamond.diamondHalfSize * 2);
		} else {
			dim = Dimension2DDouble.delta(
					Dimension2DDouble.atLeast(dimLabel, Diamond.diamondHalfSize * 2, Diamond.diamondHalfSize * 2),
					Diamond.diamondHalfSize * 2, 0);
		}
		return new FtileGeometry(dim, dim.getWidth() / 2, 0, dim.getHeight());
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		final Dimension2D diamond = calculateDimensionAlone(stringBounder);
		final Dimension2D north = this.north.calculateDimension(stringBounder);
		final double height = diamond.getHeight() + north.getHeight();
		final double left = diamond.getWidth() / 2;
		// final double width = north.getWidth() > left ? left + north.getWidth() : diamond.getWidth();
		final double width = diamond.getWidth();
		return new FtileGeometry(width, height, left, 0, diamond.getHeight());
	}

	public FtileGeometry getOverpassDimension(StringBounder stringBounder) {
		final Dimension2D total = calculateDimension(stringBounder);
		final Dimension2D north = this.north.calculateDimension(stringBounder);
		final Dimension2D east = this.east.calculateDimension(stringBounder);
		final Dimension2D west = this.west.calculateDimension(stringBounder);
		final double height = total.getHeight(); //  + north.getHeight();
		final double left = total.getWidth() / 2;
		final double supp = MathUtils.max(north.getWidth(), east.getWidth(), west.getWidth());
		// final double width = supp > left ? left + supp : diamond.getWidth();
		final double width = total.getWidth() + supp;
		return new FtileGeometry(width, height, left, 0, total.getHeight());
	}

}
