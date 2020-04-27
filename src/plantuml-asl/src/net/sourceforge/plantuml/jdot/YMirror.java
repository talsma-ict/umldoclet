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
package net.sourceforge.plantuml.jdot;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class YMirror {

	private final double max;

	public YMirror(double max) {
		this.max = max;
	}

	public double getMirrored(double v) {
		if (v < 0 || v > max) {
			throw new IllegalArgumentException();
		}
		//return v;
		return max - v;
	}

	public Point2D getMirrored(Point2D pt) {
		//return pt;
		return new Point2D.Double(pt.getX(), max - pt.getY());
	}

	public DotPath getMirrored(DotPath path) {
		DotPath result = new DotPath();
		for (CubicCurve2D.Double bez : path.getBeziers()) {
			result = result.addCurve(getMirrored(bez.getP1()), getMirrored(bez.getCtrlP1()),
					getMirrored(bez.getCtrlP2()), getMirrored(bez.getP2()));
		}
		return result;
	}

	public UTranslate getMirrored(UTranslate tr) {
		return new UTranslate(tr.getDx(), max - tr.getDy());
		//return tr;
	}

}
