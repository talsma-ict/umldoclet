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
package net.sourceforge.plantuml.klimt.drawing.eps;

import java.awt.Shape;
import java.awt.geom.PathIterator;

public class PathIteratorLimited implements PathIterator {

	private final PathIterator path;
	private final int limit;
	private int current = 0;

	public static int count(Shape source) {
		int result = 0;
		final PathIterator path = source.getPathIterator(null);
		while (path.isDone() == false) {
			result++;
			path.next();
		}
		return result;
	}

	public PathIteratorLimited(Shape source, int start, int limit) {
		this.path = source.getPathIterator(null);
		this.limit = limit;
		for (int i = 0; i < start; i++) {
			this.next();
		}
	}

	public int currentSegment(float[] arg0) {
		return path.currentSegment(arg0);
	}

	public int currentSegment(double[] arg0) {
		return path.currentSegment(arg0);
	}

	public int getWindingRule() {
		return path.getWindingRule();
	}

	public boolean isDone() {
		if (current >= limit) {
			return true;
		}
		return path.isDone();
	}

	public void next() {
		path.next();
		current++;
	}

}
