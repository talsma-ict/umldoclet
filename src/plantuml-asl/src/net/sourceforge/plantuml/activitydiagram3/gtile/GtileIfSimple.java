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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.utils.MathUtils;

public class GtileIfSimple extends AbstractGtile {

	protected final List<Gtile> gtiles;
	private final List<Dimension2D> dims = new ArrayList<>();
	protected final List<UTranslate> positions = new ArrayList<>();

	@Override
	public String toString() {
		return "GtileIfSimple " + gtiles;
	}

	public GtileIfSimple(List<Gtile> gtiles) {
		super(gtiles.get(0).getStringBounder(), gtiles.get(0).skinParam());
		this.gtiles = gtiles;

		double dx = 0;
		for (Gtile tile : gtiles) {
			final Dimension2D dim = tile.calculateDimension(getStringBounder());
			final UTranslate pos = UTranslate.dx(dx);
			dx += dim.getWidth() + getMargin();
			dims.add(dim);
			positions.add(pos);
		}
	}

	private double getMargin() {
		return 20;
	}

	public void drawU(UGraphic ug) {
		for (int i = 0; i < gtiles.size(); i++) {
			final Gtile tile = gtiles.get(i);
			final UTranslate pos = positions.get(i);
			ug.apply(pos).draw(tile);
		}
	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		Point2D result = new Point2D.Double();
		for (int i = 0; i < dims.size(); i++) {
			final Dimension2D dim = dims.get(i);
			final UTranslate pos = positions.get(i);
			final Point2D corner = pos.getTranslated(dim);
			result = MathUtils.max(result, corner);
		}
		return new Dimension2DDouble(result);
	}

	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<>();
		for (Gtile tile : gtiles)
			result.addAll(tile.getSwimlanes());
		return Collections.unmodifiableSet(result);
	}

	public Collection<Gtile> getMyChildren() {
		return Collections.unmodifiableCollection(gtiles);
	}

}
