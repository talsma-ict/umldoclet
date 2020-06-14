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

import java.util.Date;

public class DayAsDate implements Complement, Comparable<DayAsDate>, Subject {

	private final int year;
	private final int dayOfMonth;
	private final Month month;

	public static DayAsDate create(int year, String month, int dayOfMonth) {
		return new DayAsDate(year, Month.fromString(month), dayOfMonth);
	}

	public static DayAsDate create(int year, int month, int dayOfMonth) {
		return new DayAsDate(year, Month.values()[month - 1], dayOfMonth);
	}

	public static DayAsDate today() {
		final Date now = new Date();
		final int year = now.getYear() + 1900;
		final int month = now.getMonth() + 1;
		final int dayOfMonth = now.getDate();
		return create(year, month, dayOfMonth);
	}

	private DayAsDate(int year, Month month, int dayOfMonth) {
		this.year = year;
		this.dayOfMonth = dayOfMonth;
		this.month = month;
	}

	private int internalNumber() {
		return year * 100 * 100 + month.ordinal() * 100 + dayOfMonth;
	}

	@Override
	public String toString() {
		return "" + year + "/" + month + "/" + dayOfMonth;
	}

	@Override
	public int hashCode() {
		return year * 113 + dayOfMonth * 17 + month.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final DayAsDate other = (DayAsDate) obj;
		return other.internalNumber() == this.internalNumber();
	}

	public final int getDayOfMonth() {
		return dayOfMonth;
	}

	private int daysPerMonth() {
		return month.getDaysPerMonth(year);
	}

	public DayAsDate next() {
		int newDayOfMonth = dayOfMonth + 1;
		if (newDayOfMonth <= daysPerMonth()) {
			return new DayAsDate(year, month, newDayOfMonth);
		}
		assert newDayOfMonth > daysPerMonth();
		newDayOfMonth = 1;
		final Month newMonth = month.next();
		final int newYear = newMonth == Month.JANUARY ? year + 1 : year;
		return new DayAsDate(newYear, newMonth, newDayOfMonth);
	}

	public Month getMonth() {
		return month;
	}

	// https://en.wikipedia.org/wiki/Zeller%27s_congruence
	public DayOfWeek getDayOfWeek() {
		final int q = dayOfMonth;
		final int m = month.m();
		final int y = m >= 13 ? year - 1 : year;
		final int k = y % 100;
		final int j = y / 100;
		final int h = ((q + 13 * (m + 1) / 5) + k + k / 4 + j / 4 + 5 * j) % 7;
		return DayOfWeek.fromH(h);
	}

	public InstantDay asInstantDay(DayAsDate reference) {
		// if (this.compareTo(reference) < 0) {
		// throw new IllegalArgumentException();
		// }
		int cmp = 0;
		DayAsDate current = reference;
		while (current.compareTo(this) < 0) {
			current = current.next();
			cmp++;
		}
		return new InstantDay(cmp);
	}

	public int compareTo(DayAsDate other) {
		return this.internalNumber() - other.internalNumber();
	}

}
