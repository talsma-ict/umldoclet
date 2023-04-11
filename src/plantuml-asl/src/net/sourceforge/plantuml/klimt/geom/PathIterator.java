/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.klimt.geom;

public class PathIterator {

	public static final int WIND_EVEN_ODD = java.awt.geom.PathIterator.WIND_EVEN_ODD;
	public static final int WIND_NON_ZERO = java.awt.geom.PathIterator.WIND_NON_ZERO;
	public static final int SEG_MOVETO = java.awt.geom.PathIterator.SEG_MOVETO;
	public static final int SEG_LINETO = java.awt.geom.PathIterator.SEG_LINETO;
	public static final int SEG_CUBICTO = java.awt.geom.PathIterator.SEG_CUBICTO;
	public static final int SEG_QUADTO = java.awt.geom.PathIterator.SEG_QUADTO;
	public static final int SEG_CLOSE = java.awt.geom.PathIterator.SEG_CLOSE;

	public boolean isDone() {
		throw new UnsupportedOperationException();
	}

	public void next() {
		throw new UnsupportedOperationException();
	}

	public int getWindingRule() {
		throw new UnsupportedOperationException();
	}

	public int currentSegment(double[] coord) {
		throw new UnsupportedOperationException();
	}

}
