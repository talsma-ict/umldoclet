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
package net.sourceforge.plantuml.project;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;

import net.sourceforge.plantuml.project.core3.Histogram;
import net.sourceforge.plantuml.project.core3.TimeLine;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.DayOfWeek;

public class OpenClose implements Histogram, LoadPlanable {

	private final Collection<DayOfWeek> closedDayOfWeek = EnumSet.noneOf(DayOfWeek.class);
	private final Collection<DayOfWeek> openedDayOfWeek = EnumSet.noneOf(DayOfWeek.class);
	private final Collection<Day> closedDays = new HashSet<>();
	private final Collection<Day> openedDays = new HashSet<>();
	private Day startingDay;

	public int daysInWeek() {
		return 7 - closedDayOfWeek.size();
	}

	private boolean isThereSomeChangeAfter(Day day) {
		if (closedDayOfWeek.size() > 0)
			return true;

		for (Day tmp : closedDays)
			if (tmp.compareTo(day) >= 0)
				return true;

		for (Day tmp : openedDays)
			if (tmp.compareTo(day) >= 0)
				return true;

		return false;
	}

	private boolean isThereSomeChangeBefore(Day day) {
		if (closedDayOfWeek.size() > 0)
			return true;

		for (Day tmp : closedDays)
			if (tmp.compareTo(day) <= 0)
				return true;

		for (Day tmp : openedDays)
			if (tmp.compareTo(day) <= 0)
				return true;

		return false;
	}

	public boolean isClosed(Day day) {
		if (openedDays.contains(day))
			return false;

		final DayOfWeek dayOfWeek = day.getDayOfWeek();
		return closedDayOfWeek.contains(dayOfWeek) || closedDays.contains(day);
	}

	public void close(DayOfWeek day) {
		closedDayOfWeek.add(day);
	}

	public void open(DayOfWeek day) {
		closedDayOfWeek.remove(day);
		openedDayOfWeek.add(day);
	}

	public void close(Day day) {
		closedDays.add(day);
	}

	public void open(Day day) {
		openedDays.add(day);
	}

	public final Day getStartingDay() {
		return startingDay;
	}

	public final void setStartingDay(Day startingDay) {
		this.startingDay = startingDay;
	}

	public long getNext(long moment) {
		Day day = Day.create(moment);
		if (isThereSomeChangeAfter(day) == false)
			return TimeLine.MAX_TIME;

		final long current = getLoatAtInternal(day);
		System.err.println("getNext:day=" + day + " current=" + current);
		while (true) {
			day = day.increment();
			final int tmp = getLoatAtInternal(day);
			System.err.println("..day=" + day + " " + tmp);
			if (tmp != current)
				return day.getMillis();

		}
	}

	public long getPrevious(long moment) {
		Day day = Day.create(moment);
		if (isThereSomeChangeBefore(day) == false)
			return -TimeLine.MAX_TIME;

		final long current = getLoatAtInternal(day);
		System.err.println("getPrevious=" + day + " current=" + current);
		while (true) {
			day = day.decrement();
			final int tmp = getLoatAtInternal(day);
			System.err.println("..day=" + day + " " + tmp);
			if (tmp != current)
				return day.getMillis();

		}
	}

	public long getValueAt(long moment) {
		final Day day = Day.create(moment);
		if (isClosed(day))
			return 0;

		return 100;
	}

	public int getLoadAt(Day day) {
		if (getStartingDay() == null)
			return 100;

		return getLoatAtInternal(day);
	}

	private int getLoatAtInternal(Day day) {
		if (isClosed(day))
			return 0;

		return 100;
	}

	public LoadPlanable mutateMe(final OpenClose except) {
		if (except != null)
			return new LoadPlanable() {
				@Override
				public int getLoadAt(Day instant) {
					if (except.openedDays.contains(instant))
						return 100;
					if (except.closedDays.contains(instant))
						return 0;
					if (except.openedDayOfWeek.size() > 0 && except.openedDayOfWeek.contains(instant.getDayOfWeek()))
						return 100;
					return OpenClose.this.getLoadAt(instant);
				}
			};
		return this;
	}

}
