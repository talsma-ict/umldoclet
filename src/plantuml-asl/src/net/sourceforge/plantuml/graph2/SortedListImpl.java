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
package net.sourceforge.plantuml.graph2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SortedListImpl<V> implements SortedList<V> {

	static class NullableAndEvenMeasurer<V> implements Measurer<V> {
		private final Measurer<V> wrapped;
		private final int valueForNull;

		NullableAndEvenMeasurer(Measurer<V> wrapped, int valueForNull, boolean plus) {
			this.wrapped = wrapped;
			if (plus) {
				this.valueForNull = valueForNull * 2 + 1;
			} else {
				this.valueForNull = valueForNull * 2 - 1;
			}
		}

		public int getMeasure(V data) {
			if (data == null) {
				return valueForNull;
			}
			return wrapped.getMeasure(data) * 2;
		}
	}

	private final Measurer<V> measurer;
	private final List<V> all = new ArrayList<V>();
	private final Comparator<V> comparator;

	public SortedListImpl(Measurer<V> m) {
		this.measurer = m;
		this.comparator = new Comparator<V>() {
			public int compare(V o1, V o2) {
				final int v1 = measurer.getMeasure(o1);
				final int v2 = measurer.getMeasure(o2);
				return v1 - v2;
			}
		};
	}

	public void add(V data) {
		final int pos = Collections.binarySearch(all, data, comparator);
		if (pos >= 0) {
			all.add(pos, data);
		} else {
			all.add(-pos - 1, data);
		}
		assert isSorted();
	}

	private int getPos(int v, boolean plus) {
		final Measurer<V> m = new NullableAndEvenMeasurer<V>(measurer, v, plus);
		final Comparator<V> myComp = new Comparator<V>() {
			public int compare(V o1, V o2) {
				final int v1 = m.getMeasure(o1);
				final int v2 = m.getMeasure(o2);
				return v1 - v2;
			}
		};
		final int pos = Collections.binarySearch(all, null, myComp);
		assert pos < 0;
		return -pos - 1;
	}

	public List<V> lesserOrEquals(int v) {
		return all.subList(0, getPos(v, true));
	}

	public List<V> biggerOrEquals(int v) {
		return all.subList(getPos(v, false), all.size());
	}

	private boolean isSorted() {
		for (int i = 0; i < all.size() - 1; i++) {
			final int v1 = measurer.getMeasure(all.get(i));
			final int v2 = measurer.getMeasure(all.get(i + 1));
			if (v1 > v2) {
				return false;
			}
		}
		return true;
	}

	public Iterator<V> iterator() {
		return all.iterator();
	}

}
