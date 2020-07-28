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
package net.sourceforge.plantuml.graph2;

public class InflateData2 implements Comparable<InflateData2> {

	private final double pos;
	private final double inflation;

	public InflateData2(double pos, double inflation) {
		this.pos = pos;
		this.inflation = inflation;
	}

	public final double getPos() {
		return pos;
	}

	public final double getInflation() {
		return inflation;
	}

	public int compareTo(InflateData2 other) {
		return -Double.compare(this.pos, other.pos);
	}

	// public Point2D inflateX(Point2D pt) {
	// if (pt.getX() < pos) {
	// return pt;
	// }
	// if (pt.getX() == pos) {
	// return GeomUtils.translate(pt, inflation / 2, 0);
	// }
	// return GeomUtils.translate(pt, inflation, 0);
	// }
	//
	public double inflateAt(double v) {
		if (v == pos) {
			return inflation / 2;
		}

		if (v < pos) {
			return 0;
		}
		return inflation;
	}

	// public Line2D.Double inflateXAlpha(Line2D.Double line) {
	//
	// if (GeomUtils.isHorizontal(line)) {
	// return new Line2D.Double(inflateX(line.getP1()), inflateX(line.getP2()));
	// }
	// if (line.x1 == pos && line.x2 == pos) {
	// return new Line2D.Double(GeomUtils.translate(line.getP1(), inflation / 2,
	// 0), GeomUtils.translate(line
	// .getP2(), inflation / 2, 0));
	// }
	// if (line.x1 <= pos && line.x2 <= pos) {
	// return line;
	// }
	// if (line.x1 >= pos && line.x2 >= pos) {
	// return new Line2D.Double(GeomUtils.translate(line.getP1(), inflation, 0),
	// GeomUtils.translate(line.getP2(),
	// inflation, 0));
	// }
	// throw new UnsupportedOperationException();
	// }
	//
	// public Line2D.Double inflateYAlpha(Line2D.Double line) {
	// if (GeomUtils.isVertical(line)) {
	// return new Line2D.Double(inflateY(line.getP1()), inflateY(line.getP2()));
	// }
	// if (line.y1 == pos && line.y2 == pos) {
	// return new Line2D.Double(GeomUtils.translate(line.getP1(), 0, inflation /
	// 2), GeomUtils.translate(line
	// .getP2(), 0, inflation / 2));
	// }
	// if (line.y1 <= pos && line.y2 <= pos) {
	// return line;
	// }
	// if (line.y1 >= pos && line.y2 >= pos) {
	// return new Line2D.Double(GeomUtils.translate(line.getP1(), 0, inflation),
	// GeomUtils.translate(line.getP2(),
	// 0, inflation));
	// }
	// throw new UnsupportedOperationException();
	// }

	@Override
	public String toString() {
		return "" + pos + " (" + inflation + ")";
	}
}
