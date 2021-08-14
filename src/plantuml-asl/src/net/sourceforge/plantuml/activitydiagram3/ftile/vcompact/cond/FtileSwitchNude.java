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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.cond;

import java.awt.geom.Dimension2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileSwitchNude extends FtileDimensionMemoize {

	protected double xSeparation = 20;

	protected final List<Ftile> tiles;
	private final Swimlane in;

	public FtileSwitchNude(List<Ftile> tiles, Swimlane in) {
		super(tiles.get(0).skinParam());
		this.tiles = tiles;
		this.in = in;
	}

	@Override
	public Collection<Ftile> getMyChildren() {
		return Collections.unmodifiableCollection(tiles);
	}

	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<>();
		if (getSwimlaneIn() != null) {
			result.add(getSwimlaneIn());
		}
		for (Ftile tile : tiles) {
			result.addAll(tile.getSwimlanes());
		}
		return Collections.unmodifiableSet(result);
	}

	public Swimlane getSwimlaneIn() {
		return in;
	}

	public Swimlane getSwimlaneOut() {
		return getSwimlaneIn();
	}

	@Override
	public UTranslate getTranslateFor(Ftile child, StringBounder stringBounder) {
		if (tiles.contains(child)) {
			return getTranslateNude(child, stringBounder);
		}
		throw new UnsupportedOperationException();
	}

	protected UTranslate getTranslateNude(Ftile tile, StringBounder stringBounder) {
		double x1 = 0;
		for (Ftile candidate : tiles) {
			final FtileGeometry dim1 = candidate.calculateDimension(stringBounder);
			if (candidate == tile) {
				return UTranslate.dx(x1);
			}
			x1 += dim1.getWidth() + xSeparation;
		}
		throw new IllegalArgumentException();
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		for (Ftile tile : tiles) {
			ug.apply(getTranslateNude(tile, stringBounder)).draw(tile);
		}
	}

	@Override
	final protected FtileGeometry calculateDimensionFtile(StringBounder stringBounder) {
		final FtileGeometry dimTotal = calculateDimensionInternal(stringBounder);
		for (Ftile tile : tiles)
			if (tile.calculateDimension(stringBounder).hasPointOut()) {
				return dimTotal;
			}
		return dimTotal.withoutPointOut();
	}

	@Override
	protected FtileGeometry calculateDimensionInternalSlow(StringBounder stringBounder) {
		Dimension2D result = new Dimension2DDouble(0, 0);
		for (Ftile couple : tiles) {
			result = Dimension2DDouble.mergeLR(result, couple.calculateDimension(stringBounder));
		}
		result = Dimension2DDouble.delta(result, xSeparation * (tiles.size() - 1), 100);

		return new FtileGeometry(result, result.getWidth() / 2, 0);
	}

}
