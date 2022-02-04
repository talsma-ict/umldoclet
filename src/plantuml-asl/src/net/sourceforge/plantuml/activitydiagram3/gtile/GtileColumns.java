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

public class GtileColumns extends AbstractGtile {

	protected final List<Gtile> gtiles;

	private double margin;
	private double dy;

	protected final UTranslate getPosition(int pos) {
		double dx = 0;
		for (int i = 0; i < pos; i++) {
			final Dimension2D dim = gtiles.get(i).calculateDimension(getStringBounder());
			dx += dim.getWidth() + margin;
		}
		return new UTranslate(dx, dy);
	}

	protected final void setMargin(double margin) {
		if (margin < 0)
			throw new IllegalArgumentException("margin=" + margin);
		this.margin = margin;
	}

	protected final void pushDown(double height) {
		this.dy += height;
	}

	@Override
	public String toString() {
		return "GtileIfSimple " + gtiles;
	}

	public Gtile first() {
		return gtiles.get(0);
	}

	public GtileColumns(List<Gtile> gtiles, Swimlane singleSwimlane, double margin) {
		super(gtiles.get(0).getStringBounder(), gtiles.get(0).skinParam(), singleSwimlane);
		this.gtiles = gtiles;
		this.margin = margin;
	}

	@Override
	protected void drawUInternal(UGraphic ug) {
		for (int i = 0; i < gtiles.size(); i++) {
			final Gtile tile = gtiles.get(i);
			final UTranslate pos = getPosition(i);
			ug.apply(pos).draw(tile);
		}
	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		Dimension2D result = new Dimension2DDouble(0, 0);
		for (int i = 0; i < gtiles.size(); i++) {
			final Dimension2D dim = gtiles.get(i).calculateDimension(stringBounder);
			final UTranslate pos = getPosition(i);
			final Dimension2D corner = pos.getTranslated(dim);
			result = MathUtils.max(result, corner);
		}
		return result;
	}

	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<>();
		for (Gtile tile : gtiles)
			result.addAll(tile.getSwimlanes());
		return Collections.unmodifiableSet(result);
	}

//	public Collection<Gtile> getMyChildren() {
//		return Collections.unmodifiableCollection(gtiles);
//	}

}
