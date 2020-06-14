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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDecorate;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileMinWidth extends FtileDecorate {

	private final double minWidth;
	private FtileGeometry calculateDimensionInternal;

	public FtileMinWidth(Ftile tile, double minWidth) {
		super(tile);
		this.minWidth = minWidth;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final UTranslate change = getUTranslateInternal(stringBounder);
		super.drawU(ug.apply(change));
	}

	@Override
	public FtileGeometry calculateDimension(StringBounder stringBounder) {
		if (calculateDimensionInternal == null) {
			calculateDimensionInternal = calculateDimensionSlow(stringBounder);
		}
		return calculateDimensionInternal;
	}

	private FtileGeometry calculateDimensionSlow(StringBounder stringBounder) {
		final FtileGeometry geo = super.calculateDimension(stringBounder);
		final double left = getPoint2(geo.getLeft(), stringBounder);
		if (geo.hasPointOut() == false) {
			return new FtileGeometry(getDimensionInternal(stringBounder), left, geo.getInY());
		}
		return new FtileGeometry(getDimensionInternal(stringBounder), left, geo.getInY(), geo.getOutY());
	}

	private Dimension2D getDimensionInternal(StringBounder stringBounder) {
		final Dimension2D dim = getFtileDelegated().calculateDimension(stringBounder);
		if (dim.getWidth() < minWidth) {
			return new Dimension2DDouble(minWidth, dim.getHeight());
		}
		return dim;
	}

	private UTranslate getUTranslateInternal(final StringBounder stringBounder) {
		final Dimension2D dimTile = getFtileDelegated().calculateDimension(stringBounder);
		final Dimension2D dimTotal = getDimensionInternal(stringBounder);
		final UTranslate change = new UTranslate((dimTotal.getWidth() - dimTile.getWidth()) / 2, 0);
		return change;
	}

	public UTranslate getTranslateFor(Ftile child, StringBounder stringBounder) {
		if (child == getFtileDelegated()) {
			return getUTranslateInternal(stringBounder);
		}
		return null;
	}

	private double getPoint2(double x, StringBounder stringBounder) {
		final Dimension2D dim = getFtileDelegated().calculateDimension(stringBounder);
		if (dim.getWidth() < minWidth) {
			final double diff = minWidth - dim.getWidth();
			return x + diff / 2;
		}
		return x;
	}

}
