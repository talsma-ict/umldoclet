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
package net.sourceforge.plantuml.activitydiagram3.gtile;

import net.sourceforge.plantuml.awt.geom.Dimension2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.utils.MathUtils;

public class GtileTopDown3 extends AbstractGtile {

	protected final Gtile tile1;
	protected final Gtile tile2;
	protected final Gtile tile3;

	private final Dimension2D dim1;
	private final Dimension2D dim2;
	private final Dimension2D dim3;

	private final UTranslate pos1;
	private final UTranslate pos2;
	private final UTranslate pos3;

	public GtileTopDown3(Gtile tile1, Gtile tile2, Gtile tile3) {
		super(tile1.getStringBounder(), tile1.skinParam());

		final GtileTopDown tile12 = new GtileTopDown(tile1, tile2);
		final GtileTopDown tile123 = new GtileTopDown(tile12, tile3);

		this.tile1 = tile1;
		this.tile2 = tile2;
		this.tile3 = tile3;

		this.dim1 = tile1.calculateDimension(stringBounder);
		this.dim2 = tile2.calculateDimension(stringBounder);
		this.dim3 = tile3.calculateDimension(stringBounder);

		this.pos1 = tile123.getPos1().compose(tile12.getPos1());
		this.pos2 = tile123.getPos1().compose(tile12.getPos2());
		this.pos3 = tile123.getPos2();
	}

	@Override
	protected UTranslate getCoordImpl(String name) {
		if (name.equals(GPoint.NORTH_HOOK))
			return getPos1().compose(tile1.getCoord(name));
		if (name.equals(GPoint.SOUTH_HOOK))
			return getPos3().compose(tile3.getCoord(name));
		throw new UnsupportedOperationException();
	}

	@Override
	public Swimlane getSwimlane(String name) {
		if (name.equals(GPoint.NORTH_HOOK))
			return tile1.getSwimlane(name);
		if (name.equals(GPoint.SOUTH_HOOK))
			return tile3.getSwimlane(name);
		throw new UnsupportedOperationException();
	}

	protected UTranslate getPos1() {
		return pos1;
	}

	protected UTranslate getPos2() {
		return pos2;
	}

	protected UTranslate getPos3() {
		return pos3;
	}

	@Override
	protected void drawUInternal(UGraphic ug) {
		ug.apply(getPos1()).draw(tile1);
		ug.apply(getPos2()).draw(tile2);
		ug.apply(getPos3()).draw(tile3);
	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D corner1 = getPos1().getTranslated(dim1);
		final Dimension2D corner2 = getPos2().getTranslated(dim2);
		final Dimension2D corner3 = getPos3().getTranslated(dim3);
		return MathUtils.max(corner1, corner2, corner3);
	}

	@Override
	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<>();
		result.addAll(tile1.getSwimlanes());
		result.addAll(tile2.getSwimlanes());
		result.addAll(tile3.getSwimlanes());
		return Collections.unmodifiableSet(result);
	}

//	public Collection<Gtile> getMyChildren() {
//		return Arrays.asList(tile1, tile2);
//	}

}
