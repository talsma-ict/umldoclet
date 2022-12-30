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
package net.sourceforge.plantuml.posimo;

import net.sourceforge.plantuml.awt.geom.XDimension2D;
import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.awt.geom.XRectangle2D;

public class PositionableUtils {

	static private XRectangle2D convert(Positionable positionable) {
		final XPoint2D position = positionable.getPosition();
		final XDimension2D size = positionable.getSize();
		return new XRectangle2D(position.getX(), position.getY(), size.getWidth(), size.getHeight());
	}

	static public boolean intersect(Positionable big, Positionable small) {
		final XRectangle2D bigR = convert(big);
		final XRectangle2D smallR = convert(small);
		return bigR.intersects(smallR);
	}

	static public Positionable addMargin(final Positionable pos, final double widthMargin, final double heightMargin) {
		return new Positionable() {

			public XPoint2D getPosition() {
				final XPoint2D p = pos.getPosition();
				return new XPoint2D(p.getX() - widthMargin, p.getY() - heightMargin);
			}

			public XDimension2D getSize() {
				return pos.getSize().delta(2 * widthMargin, 2 * heightMargin);
			}

			public void moveSvek(double deltaX, double deltaY) {
				pos.moveSvek(deltaX, deltaY);
			}
		};
	}

	static private XPoint2D getCenter(Positionable p) {
		final XPoint2D pt = p.getPosition();
		final XDimension2D dim = p.getSize();
		return new XPoint2D(pt.getX() + dim.getWidth() / 2, pt.getY() + dim.getHeight() / 2);
	}

	static private Positionable move(Positionable p, double deltaX, double deltaY) {
		final XPoint2D pt = p.getPosition();
		final XDimension2D dim = p.getSize();
		return new PositionableImpl(pt.getX() + deltaX, pt.getY() + deltaY, dim);

	}

	public static Positionable moveAwayFrom(Positionable fixe, Positionable toMove) {
		final XPoint2D centerFixe = getCenter(fixe);
		final XPoint2D centerToMove = getCenter(toMove);
		// final XPoint2D pt = toMove.getPosition();
		// return new PositionableImpl(pt.getX() + 20, pt.getY(),
		// toMove.getSize());

		final double deltaX = centerToMove.getX() - centerFixe.getX();
		final double deltaY = centerToMove.getY() - centerFixe.getY();

		double min = 0.0;
		if (doesIntersectWithThisCoef(fixe, toMove, deltaX, deltaY, min) == false)
			throw new IllegalArgumentException();

		double max = 0.1;
		while (doesIntersectWithThisCoef(fixe, toMove, deltaX, deltaY, max))
			max = max * 2;

		for (int i = 0; i < 5; i++) {
			assert doesIntersectWithThisCoef(fixe, toMove, deltaX, deltaY, min);
			assert doesIntersectWithThisCoef(fixe, toMove, deltaX, deltaY, max) == false;
			final double candidat = (min + max) / 2.0;
			if (doesIntersectWithThisCoef(fixe, toMove, deltaX, deltaY, candidat))
				min = candidat;
			else
				max = candidat;

			// Log.println("min=" + min + " max=" + max);
		}
		final double candidat = (min + max) / 2.0;
		return move(toMove, deltaX * candidat, deltaY * candidat);

	}

	private static boolean doesIntersectWithThisCoef(Positionable fixe, Positionable toMove, double deltaX,
			double deltaY, double c) {
		final Positionable result = move(toMove, deltaX * c, deltaY * c);
		return intersect(fixe, result);
	}

}
