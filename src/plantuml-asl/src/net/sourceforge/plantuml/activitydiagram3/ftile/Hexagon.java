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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UPolygon;

public class Hexagon {

	final static public double hexagonHalfSize = 12;

	public static UPolygon asPolygon(double shadowing) {
		final UPolygon diams = new UPolygon();

		diams.addPoint(hexagonHalfSize, 0);
		diams.addPoint(hexagonHalfSize * 2, hexagonHalfSize);
		diams.addPoint(hexagonHalfSize, hexagonHalfSize * 2);
		diams.addPoint(0, hexagonHalfSize);
		diams.addPoint(hexagonHalfSize, 0);

		// if (shadowing) {
		// diams.setDeltaShadow(3);
		// }
		diams.setDeltaShadow(shadowing);

		return diams;
	}

	public static UPolygon asPolygon(double shadowing, double width, double height) {
		final UPolygon diams = new UPolygon();

		diams.addPoint(hexagonHalfSize, 0);
		diams.addPoint(width - hexagonHalfSize, 0);
		diams.addPoint(width, height / 2);
		diams.addPoint(width - hexagonHalfSize, height);
		diams.addPoint(hexagonHalfSize, height);
		diams.addPoint(0, height / 2);
		diams.addPoint(hexagonHalfSize, 0);

		// if (shadowing) {
		// diams.setDeltaShadow(3);
		// }
		diams.setDeltaShadow(shadowing);

		return diams;
	}

	public static Stencil asStencil(final TextBlock tb) {
		return new Stencil() {

			private final double getDeltaX(double height, double y) {
				final double p = y / height * 2;
				if (p <= 1) {
					return hexagonHalfSize * p;
				}
				return hexagonHalfSize * (2 - p);
			}

			public double getStartingX(StringBounder stringBounder, double y) {
				final Dimension2D dim = tb.calculateDimension(stringBounder);
				return -getDeltaX(dim.getHeight(), y);
			}

			public double getEndingX(StringBounder stringBounder, double y) {
				final Dimension2D dim = tb.calculateDimension(stringBounder);
				return dim.getWidth() + getDeltaX(dim.getHeight(), y);
			}
		};
	}

	public static UPolygon asPolygonSquare(boolean shadowing, double width, double height) {
		final UPolygon diams = new UPolygon();

		diams.addPoint(width / 2, 0);
		diams.addPoint(width, height / 2);
		diams.addPoint(width / 2, height);
		diams.addPoint(0, height / 2);

		if (shadowing) {
			diams.setDeltaShadow(3);
		}

		return diams;
	}

}
