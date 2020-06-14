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
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Diamond;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileDiamond extends AbstractFtile {

	private final HtmlColor backColor;
	private final HtmlColor borderColor;
	private final Swimlane swimlane;
	private final TextBlock north;
	private final TextBlock south;
	private final TextBlock west1;
	private final TextBlock east1;

	public FtileDiamond(ISkinParam skinParam, HtmlColor backColor, HtmlColor borderColor, Swimlane swimlane) {
		this(skinParam, backColor, borderColor, swimlane, TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0),
				TextBlockUtils.empty(0, 0), TextBlockUtils.empty(0, 0));
	}
	
	@Override
	public Collection<Ftile> getMyChildren() {
		return Collections.emptyList();
	}

	public FtileDiamond withNorth(TextBlock north) {
		return new FtileDiamond(skinParam(), backColor, borderColor, swimlane, north, south, east1, west1);
	}

	public FtileDiamond withWest(TextBlock west1) {
		if (west1 == null) {
			return this;
		}
		return new FtileDiamond(skinParam(), backColor, borderColor, swimlane, north, south, east1, west1);
	}

	public FtileDiamond withEast(TextBlock east1) {
		if (east1 == null) {
			return this;
		}
		return new FtileDiamond(skinParam(), backColor, borderColor, swimlane, north, south, east1, west1);
	}

	public FtileDiamond withSouth(TextBlock south) {
		return new FtileDiamond(skinParam(), backColor, borderColor, swimlane, north, south, east1, west1);
	}

	private FtileDiamond(ISkinParam skinParam, HtmlColor backColor, HtmlColor borderColor, Swimlane swimlane,
			TextBlock north, TextBlock south, TextBlock east1, TextBlock west1) {
		super(skinParam);
		this.backColor = backColor;
		this.swimlane = swimlane;
		this.borderColor = borderColor;
		this.north = north;
		this.west1 = west1;
		this.east1 = east1;
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

		final double suppY1 = north.calculateDimension(ug.getStringBounder()).getHeight();
		ug = ug.apply(new UTranslate(0, suppY1));
		ug.apply(new UChangeColor(borderColor)).apply(getThickness()).apply(new UChangeBackColor(backColor))
				.draw(Diamond.asPolygon(skinParam().shadowing(null)));
		// final Dimension2D dimNorth = north.calculateDimension(ug.getStringBounder());
		north.drawU(ug.apply(new UTranslate(Diamond.diamondHalfSize * 1.5, -suppY1)));

		// final Dimension2D dimSouth = south.calculateDimension(ug.getStringBounder());
		south.drawU(ug.apply(new UTranslate(Diamond.diamondHalfSize * 1.5, 2 * Diamond.diamondHalfSize)));
		// south.drawU(ug.apply(new UTranslate(-(dimSouth.getWidth() - 2 * Diamond.diamondHalfSize) / 2,
		// 2 * Diamond.diamondHalfSize)));

		final Dimension2D dimWeat1 = west1.calculateDimension(ug.getStringBounder());
		west1.drawU(ug.apply(new UTranslate(-dimWeat1.getWidth(), -dimWeat1.getHeight() + Diamond.diamondHalfSize)));

		final Dimension2D dimEast1 = east1.calculateDimension(ug.getStringBounder());
		east1.drawU(ug.apply(new UTranslate(Diamond.diamondHalfSize * 2, -dimEast1.getHeight()
				+ Diamond.diamondHalfSize)));
	}

	@Override
	protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		final double suppY1 = north.calculateDimension(stringBounder).getHeight();
		final Dimension2D dim = new Dimension2DDouble(Diamond.diamondHalfSize * 2, Diamond.diamondHalfSize * 2 + suppY1);
		return new FtileGeometry(dim, dim.getWidth() / 2, suppY1, dim.getHeight());
	}

	public Ftile withWestAndEast(TextBlock tb1, TextBlock tb2) {
		return withWest(tb1).withEast(tb2);
	}

	public double getEastLabelWidth(StringBounder stringBounder) {
		final Dimension2D dimEast = east1.calculateDimension(stringBounder);
		return dimEast.getWidth();
	}
	
	public double getSouthLabelHeight(StringBounder stringBounder) {
		final Dimension2D dimSouth = south.calculateDimension(stringBounder);
		return dimSouth.getHeight();
	}


}
