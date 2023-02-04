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
package net.sourceforge.plantuml.svek;

import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.awt.geom.XPoint2D;
import net.sourceforge.plantuml.utils.Log;

class PointListIteratorImpl implements PointListIterator {

	private final SvgResult svg;
	private int pos = 0;

	static PointListIterator create(SvgResult svg, int lineColor) {
		final PointListIteratorImpl result = new PointListIteratorImpl(svg);
		final int idx = svg.getIndexFromColor(lineColor);
		if (idx == -1) {
			result.pos = -1;
		}
		return result;
	}

	public PointListIterator cloneMe() {
		final PointListIteratorImpl result = new PointListIteratorImpl(svg);
		result.pos = this.pos;
		return result;
	}

	private PointListIteratorImpl(SvgResult svg) {
		this.svg = svg;
	}

	public boolean hasNext() {
		return true;
	}

	public List<XPoint2D> next() {
		if (pos == -1) {
			return Collections.emptyList();
		}
		try {
			final List<XPoint2D> result = svg.substring(pos).extractList(SvgResult.POINTS_EQUALS);
			pos = svg.indexOf(SvgResult.POINTS_EQUALS, pos) + SvgResult.POINTS_EQUALS.length() + 1;
			return result;
		} catch (StringIndexOutOfBoundsException e) {
			Log.error("Error " + e);
			return Collections.emptyList();
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
