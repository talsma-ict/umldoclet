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
package net.sourceforge.plantuml.project.time;

import java.util.Calendar;
import java.util.TimeZone;

import net.sourceforge.plantuml.project.Value;

public class Day implements Comparable<Day>, Value {

	static final public long MILLISECONDS_PER_DAY = 1000L * 3600L * 24;
	static final private Calendar gmt = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

	private final int dayOfMonth;
	private final MonthYear monthYear;
	private final long ms1;

	public static Day create(int year, String month, int dayOfMonth) {
		return new Day(year, Month.fromString(month), dayOfMonth);
	}

	public static Day create(int year, int month, int dayOfMonth) {
		return new Day(year, Month.values()[month - 1], dayOfMonth);
	}

	public static Day create(long ms) {
		return new Day(ms);
	}

	public static Day today() {
		return create(System.currentTimeMillis());
	}

	public int getWeekOfYear(WeekNumberStrategy strategy) {
		synchronized (gmt) {
			gmt.clear();
			gmt.setTimeInMillis(ms1);
			gmt.setFirstDayOfWeek(strategy.getFirstDayOfWeekAsLegacyInt());
			gmt.setMinimalDaysInFirstWeek(strategy.getMinimalDaysInFirstWeek());
			return gmt.get(Calendar.WEEK_OF_YEAR);
		}
	}

	private Day(int year, Month month, int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
		this.monthYear = MonthYear.create(year, month);
		synchronized (gmt) {
			gmt.clear();
			gmt.set(year, month.ordinal(), dayOfMonth);
			this.ms1 = gmt.getTimeInMillis();
		}
	}

	private Day(long ms) {
		this.ms1 = ms;
		synchronized (gmt) {
			gmt.clear();
			gmt.setTimeInMillis(ms);
			final int year = gmt.get(Calendar.YEAR);
			final int month = gmt.get(Calendar.MONTH);
			final int dayOfMonth = gmt.get(Calendar.DAY_OF_MONTH);
			this.dayOfMonth = dayOfMonth;
			this.monthYear = MonthYear.create(year, Month.values()[month]);
		}

	}

	public Day increment() {
		return addDays(1);
	}

	public Day decrement() {
		return addDays(-1);
	}

	public Day addDays(int nday) {
		return create(MILLISECONDS_PER_DAY * (getAbsoluteDayNum() + nday));
	}

	public final int getAbsoluteDayNum() {
		return (int) (ms1 / MILLISECONDS_PER_DAY);
	}

	public final long getMillis() {
		return ms1;
	}

	public int year() {
		return monthYear.year();
	}

	private int internalNumber() {
		return year() * 100 * 100 + month().ordinal() * 100 + dayOfMonth;
	}

	@Override
	public String toString() {
		return monthYear.toString() + "/" + dayOfMonth;
	}

	@Override
	public int hashCode() {
		return monthYear.hashCode() + dayOfMonth * 17;
	}

	@Override
	public boolean equals(Object obj) {
		final Day other = (Day) obj;
		return other.internalNumber() == this.internalNumber();
	}

	public final int getDayOfMonth() {
		return dayOfMonth;
	}

	private int daysPerMonth() {
		return month().getDaysPerMonth(year());
	}

	public Month month() {
		return monthYear.month();
	}

	public MonthYear monthYear() {
		return monthYear;
	}

	// https://en.wikipedia.org/wiki/Zeller%27s_congruence
	public DayOfWeek getDayOfWeek() {
		final int q = dayOfMonth;
		final int m = month().m();
		final int y = m >= 13 ? year() - 1 : year();
		final int k = y % 100;
		final int j = y / 100;
		final int h = ((q + 13 * (m + 1) / 5) + k + k / 4 + j / 4 + 5 * j) % 7;
		return DayOfWeek.fromH(h);
	}

	public int compareTo(Day other) {
		return this.internalNumber() - other.internalNumber();
	}

	public static Day min(Day wink1, Day wink2) {
		if (wink2.internalNumber() < wink1.internalNumber()) {
			return wink2;
		}
		return wink1;
	}

	public static Day max(Day wink1, Day wink2) {
		if (wink2.internalNumber() > wink1.internalNumber()) {
			return wink2;
		}
		return wink1;
	}

}
