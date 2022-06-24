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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.awt.geom.Dimension2D;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GtileWithMargin extends AbstractGtileRoot implements Gtile {

	protected final AbstractGtileRoot orig;
	protected final double north;
	protected final double south;
	private final double east;

	public GtileWithMargin(AbstractGtileRoot orig, double north, double south, double east) {
		super(orig.stringBounder, orig.skinParam());
		this.orig = orig;
		this.north = north;
		this.south = south;
		this.east = east;
	}

	@Override
	public Set<Swimlane> getSwimlanes() {
		return orig.getSwimlanes();
	}

	@Override
	public Swimlane getSwimlane(String point) {
		return orig.getSwimlane(point);
	}

	@Override
	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D result = orig.calculateDimension(stringBounder);
		return Dimension2DDouble.delta(result, east, north + south);
	}

	private UTranslate getTranslate() {
		return new UTranslate(east, north);
	}

	@Override
	protected void drawUInternal(UGraphic ug) {
		orig.drawU(ug.apply(getTranslate()));
	}

	@Override
	protected UTranslate getCoordImpl(String name) {
		return orig.getCoordImpl(name).compose(getTranslate());
	}

	@Override
	public Collection<GConnection> getInnerConnections() {
		return Collections.emptyList();
	}

}
