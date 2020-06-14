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
package net.sourceforge.plantuml.salt.element;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.utils.MathUtils;

public class ElementBorder extends AbstractElement {

	private Element north = new ElementEmpty();
	private Element south = new ElementEmpty();
	private Element east = new ElementEmpty();
	private Element west = new ElementEmpty();
	private Element center = new ElementEmpty();

	public final void setNorth(Element north) {
		this.north = north;
	}

	public final void setSouth(Element south) {
		this.south = south;
	}

	public final void setEast(Element east) {
		this.east = east;
	}

	public final void setWest(Element west) {
		this.west = west;
	}

	public final void setCenter(Element center) {
		this.center = center;
	}

	public void drawU(UGraphic ug, int zIndex, Dimension2D dimToUse) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimNorth = north.getPreferredDimension(stringBounder, 0, 0);
		final Dimension2D dimSouth = south.getPreferredDimension(stringBounder, 0, 0);
		final Dimension2D dimEast = east.getPreferredDimension(stringBounder, 0, 0);
		final Dimension2D dimWest = west.getPreferredDimension(stringBounder, 0, 0);
		final Point2D pA = new Point2D.Double(dimWest.getWidth(), dimNorth.getHeight());
		final Point2D pB = new Point2D.Double(dimToUse.getWidth() - dimEast.getWidth(), dimNorth.getHeight());
		final Point2D pC = new Point2D.Double(dimWest.getWidth(), dimToUse.getHeight() - dimSouth.getHeight());
		// final Point2D pD = new Point2D.Double(dimToUse.getWidth() - dimEast.getWidth(), dimToUse.getHeight()
		// - dimSouth.getHeight());

		north.drawU(ug, zIndex, dimToUse);
		south.drawU(ug.apply(new UTranslate(0, pC.getY())), zIndex, dimToUse);
		west.drawU(ug.apply(new UTranslate(0, pA.getY())), zIndex, dimToUse);
		east.drawU(ug.apply(new UTranslate(pB.getX(), pB.getY())), zIndex, dimToUse);
		center.drawU(ug.apply(new UTranslate(pA.getX(), pA.getY())), zIndex, dimToUse);
	}

	public Dimension2D getPreferredDimension(StringBounder stringBounder, double x, double y) {
		final Dimension2D dimNorth = north.getPreferredDimension(stringBounder, x, y);
		final Dimension2D dimSouth = south.getPreferredDimension(stringBounder, x, y);
		final Dimension2D dimEast = east.getPreferredDimension(stringBounder, x, y);
		final Dimension2D dimWest = west.getPreferredDimension(stringBounder, x, y);
		final Dimension2D dimCenter = center.getPreferredDimension(stringBounder, x, y);
		final double width = MathUtils.max(dimNorth.getWidth(), dimSouth.getWidth(),
				dimWest.getWidth() + dimCenter.getWidth() + dimEast.getWidth());
		final double height = dimNorth.getHeight()
				+ MathUtils.max(dimWest.getHeight(), dimCenter.getHeight(), dimEast.getHeight()) + dimSouth.getHeight();
		return new Dimension2DDouble(width, height);
	}

}
