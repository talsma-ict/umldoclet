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
package net.sourceforge.plantuml.project.core2;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import net.sourceforge.plantuml.project.time.DayOfWeek;

public class WorkLoadConstantButWeekDay implements WorkLoad {

	private final int value;
	private final Set<DayOfWeek> excepts = EnumSet.noneOf(DayOfWeek.class);

	public WorkLoadConstantButWeekDay(int value, DayOfWeek... butThisDays) {
		this.value = value;
		this.excepts.addAll(Arrays.asList(butThisDays));
	}

	private static final long dayDuration = 1000L * 24 * 3600;

	public IteratorSlice slices(final long timeBiggerThan) {
		final Slice first = getNext(timeBiggerThan);
		return new MyIterator(first);
	}

	class MyIterator implements IteratorSlice {

		private Slice current;

		public MyIterator(Slice first) {
			this.current = first;
		}

		public Slice next() {
			final Slice result = current;
			current = getNext(current.getEnd());
			return result;
		}
	}

	private Slice getNext(final long limit) {
		long start = limit;
		long end;
		if (isClose(start)) {
			start = round(start);
			while (isClose(start))
				start += dayDuration;
			end = start + dayDuration;
		} else {
			end = round(start) + dayDuration;
		}
		assert !isClose(start);
		while (isClose(end) == false)
			end += dayDuration;
		assert isClose(end);

		return new Slice(start, end, value);
	}

	private boolean isClose(long start) {
		return excepts.contains(DayOfWeek.fromTime(start));
	}

	private long round(long start) {
		return dayDuration * (start / dayDuration);
	}

}
