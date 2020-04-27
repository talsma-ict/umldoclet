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
package net.sourceforge.plantuml.cute;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class CutePath {

	private final List<Arc> arcs = new ArrayList<Arc>();

	public CutePath(String value) {

		MyPoint2D lastAdded = null;
		String tension = null;

		final StringTokenizer spl = new StringTokenizer(value, "~:", true);
		while (spl.hasMoreTokens()) {
			final String token = spl.nextToken();
			if (token.equals(":")) {
				continue;
			} else if (token.equals("~")) {
				tension = spl.nextToken();
				final String next = spl.nextToken();
				if (next.equals("~") == false) {
					throw new IllegalArgumentException();
				}
			} else {
				final StringTokenizer st = new StringTokenizer(token.replaceAll("[()]", ""), ",^");
				final MyPoint2D current = new MyPoint2D(st);
				if (lastAdded != null) {
					add(new Arc(lastAdded, current).withTension(tension));
				}
				lastAdded = current;
				tension = null;
			}
		}
		add(new Arc(lastAdded, arcs.get(0).getA()).withTension(tension));

	}

	public CutePath() {
	}

	public void add(Arc arc) {
		if (arcs.size() > 0) {
			final Arc last = arcs.get(arcs.size() - 1);
			if (last.getB().equals(arc.getA()) == false) {
				throw new IllegalArgumentException("last=" + last.getB() + " arc=" + arc.getA());
			}
		}
		this.arcs.add(arc);
	}

	private final MyPoint2D getMyPoint2D(int i) {
		return getArc(i).getA();
	}

	private Arc getArc(int i) {
		if (i == -1) {
			return arcs.get(arcs.size() - 1);
		}
		if (i == arcs.size()) {
			return arcs.get(0);
		}
		if (i == arcs.size() + 1) {
			return arcs.get(1);
		}
		return arcs.get(i);
	}

	private UPath toUPath() {
		final TriangleCorner corner0 = new TriangleCorner(getMyPoint2D(0), getMyPoint2D(1), getMyPoint2D(2));
		final int swepFlag = corner0.determinant() < 0 ? 0 : 1;

		final UPath path = new UPath();
		final BetweenCorners betweenCornersLast = new BetweenCorners(getCorner(arcs.size() - 1),
				getCorner(arcs.size()), arcs.get(arcs.size() - 1).getTension());
		betweenCornersLast.initPath(path);
		for (int i = 0; i < arcs.size(); i++) {

			// if (i == 0) {
			// if (getMyPoint2D(i).hasCurvation()) {
			// path.moveTo(getPointK(i));
			// } else {
			// path.moveTo(arcs.get(i).getA());
			// }
			// }

			final BetweenCorners betweenCorners = new BetweenCorners(getCorner(i), getCorner(i + 1), arcs.get(i)
					.getTension());
			betweenCorners.addToPath(path, swepFlag);

		}
		path.closePath();
		return path;
	}

	private void debugMe(UGraphic ug) {
		for (int i = 0; i < arcs.size(); i++) {
			final BetweenCorners betweenCorners = new BetweenCorners(getCorner(i), getCorner(i + 1), arcs.get(i)
					.getTension());
			betweenCorners.debugMe(ug.apply(HColorUtils.BLACK).apply(new HColorNone().bg()));
		}
	}

	private Point2D getPointK(final int j) {
		if (getMyPoint2D(j).hasCurvation()) {
			return getCorner(j).getOnSegmentB(getMyPoint2D(j).getCurvation(0));
		}
		return arcs.get(j - 1).getB();
	}

	private TriangleCorner getCorner(int i) {
		return new TriangleCorner(getMyPoint2D(i), getMyPoint2D(i - 1), getMyPoint2D(i + 1));
	}

	public void drawU(UGraphic ug) {
		final UPath path = toUPath();
		ug.draw(path);
//		debugMe(ug);
	}

	public CutePath rotateZoom(RotationZoom rotationZoom) {
		final CutePath result = new CutePath();
		for (Arc arc : arcs) {
			result.arcs.add(arc.rotateZoom(rotationZoom));
		}
		return result;
	}

	public CutePath withNoTension() {
		final CutePath result = new CutePath();
		for (Arc arc : arcs) {
			result.arcs.add(arc.withNoTension());
		}
		return result;
	}
}
