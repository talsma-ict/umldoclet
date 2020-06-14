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
package net.sourceforge.plantuml.project3;

import java.util.Iterator;

public class DaysAsDates implements Subject, Complement, Iterable<DayAsDate> {

	private final DayAsDate date1;
	private final DayAsDate date2;

	public DaysAsDates(DayAsDate date1, DayAsDate date2) {
		this.date1 = date1;
		this.date2 = date2;
	}

	public DaysAsDates(GanttDiagram gantt, DayAsDate date1, int count) {
		this.date1 = date1;
		DayAsDate tmp = date1;
		while (count > 0) {
			if (gantt.isOpen(tmp)) {
				count--;
			}
			tmp = tmp.next();
		}
		this.date2 = tmp;
	}

	class MyIterator implements Iterator<DayAsDate> {

		private DayAsDate current;

		public MyIterator(DayAsDate current) {
			this.current = current;
		}

		public boolean hasNext() {
			return current.compareTo(date2) <= 0;
		}

		public DayAsDate next() {
			final DayAsDate result = current;
			current = current.next();
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public Iterator<DayAsDate> iterator() {
		return new MyIterator(date1);
	}

}
