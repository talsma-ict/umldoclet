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
package net.sourceforge.plantuml.project.draw;

import java.util.Map;

import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.project.LoadPlanable;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.GCalendar;
import net.sourceforge.plantuml.project.time.MonthYear;
import net.sourceforge.plantuml.project.time.Wink;
import net.sourceforge.plantuml.project.timescale.TimeScaleDaily;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorNone;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;
import net.sourceforge.plantuml.ugraphic.color.HColorUtils;

public class TimeHeaderDaily extends TimeHeader {

	private double getTimeHeaderHeight() {
		return Y_POS_ROW28 + 13;
	}

	private final HColor veryLightGray = HColorSet.instance().getColorIfValid("#E0E8E8");
	private final HColor lightGray = HColorSet.instance().getColorIfValid("#909898");

	private final GCalendar calendar;
	private final LoadPlanable defaultPlan;
	private final Map<Day, HColor> colorDays;
	private final Map<Day, String> nameDays;

	public TimeHeaderDaily(GCalendar calendar, Wink min, Wink max, LoadPlanable defaultPlan, Map<Day, HColor> colorDays,
			Map<Day, String> nameDays, Day printStart, Day printEnd) {
		super(min, max, new TimeScaleDaily(calendar, printStart));
		this.calendar = calendar;
		this.defaultPlan = defaultPlan;
		this.colorDays = colorDays;
		this.nameDays = nameDays;
	}

	@Override
	public void drawTimeHeader(final UGraphic ug, double totalHeight) {
		drawCalendar(ug, totalHeight);
		drawHline(ug, 0);
		drawHline(ug, getFullHeaderHeight());

	}

	private void drawCalendar(final UGraphic ug, double totalHeight) {
		drawTexts(ug, totalHeight);
		drawMonths(ug);
		drawNonWorking(ug, totalHeight);
		drawVBars(ug, totalHeight);
		drawVbar(ug, getTimeScale().getStartingPosition(max.increment()), Y_POS_ROW16, totalHeight);
		printNamedDays(ug);

	}

	private void drawTexts(final UGraphic ug, double totalHeight) {
		final double height = totalHeight - getFullHeaderHeight();
		for (Wink wink = min; wink.compareTo(max) <= 0; wink = wink.increment()) {
			final double x1 = getTimeScale().getStartingPosition(wink);
			final double x2 = getTimeScale().getEndingPosition(wink);
			final Day day = calendar.toDayAsDate(wink);
			HColor textColor = HColorUtils.BLACK;
			if (defaultPlan.getLoadAt(wink) <= 0) {
				textColor = lightGray;
			}
			final HColor back = colorDays.get(day);
			if (back != null) {
				drawRectangle(ug.apply(back.bg()), height, x1, x2);
			}
			printCentered(ug.apply(UTranslate.dy(Y_POS_ROW16)),
					getTextBlock(day.getDayOfWeek().shortName(), 10, false, textColor), x1, x2);
			printCentered(ug.apply(UTranslate.dy(Y_POS_ROW28)),
					getTextBlock("" + day.getDayOfMonth(), 10, false, textColor), x1, x2);
		}
	}

	private void drawMonths(final UGraphic ug) {
		MonthYear last = null;
		double lastChangeMonth = -1;
		for (Wink wink = min; wink.compareTo(max) <= 0; wink = wink.increment()) {
			final double x1 = getTimeScale().getStartingPosition(wink);
			final Day day = calendar.toDayAsDate(wink);
			if (day.monthYear().equals(last) == false) {
				if (last != null) {
					printMonth(ug, last, lastChangeMonth, x1);
				}
				lastChangeMonth = x1;
				last = day.monthYear();
			}
		}
		final double x1 = getTimeScale().getStartingPosition(max.increment());
		if (x1 > lastChangeMonth) {
			printMonth(ug, last, lastChangeMonth, x1);
		}
	}

	private void drawNonWorking(final UGraphic ug, double totalHeight) {
		final double height = totalHeight - getFullHeaderHeight();
		for (Wink wink = min; wink.compareTo(max) <= 0; wink = wink.increment()) {
			final double x1 = getTimeScale().getStartingPosition(wink);
			final double x2 = getTimeScale().getEndingPosition(wink);
			if (defaultPlan.getLoadAt(wink) == 0) {
				drawRectangle(ug.apply(veryLightGray.bg()), height, x1, x2);
			}
		}
	}

	private void drawRectangle(UGraphic ug, double height, double x1, double x2) {
		if (height == 0) {
			return;
		}
		ug = ug.apply(new HColorNone());
		ug = ug.apply(new UTranslate(x1 + 1, getFullHeaderHeight()));
		ug.draw(new URectangle(x2 - x1 - 1, height));
	}

	private void drawVBars(final UGraphic ug, double totalHeight) {
		MonthYear last = null;
		for (Wink wink = min; wink.compareTo(max) <= 0; wink = wink.increment()) {
			double startingY = getFullHeaderHeight();
			final Day day = calendar.toDayAsDate(wink);
			if (day.monthYear().equals(last) == false) {
				startingY = 0;
				last = day.monthYear();
			}
			drawVbar(ug, getTimeScale().getStartingPosition(wink), startingY, totalHeight);
		}
	}

	private void printMonth(UGraphic ug, MonthYear monthYear, double start, double end) {
		final TextBlock tiny = getTextBlock(monthYear.shortName(), 12, true, HColorUtils.BLACK);
		final TextBlock small = getTextBlock(monthYear.longName(), 12, true, HColorUtils.BLACK);
		final TextBlock big = getTextBlock(monthYear.longNameYYYY(), 12, true, HColorUtils.BLACK);
		printCentered(ug, start, end, tiny, small, big);
	}

	private void drawVbar(UGraphic ug, double x, double y1, double y2) {
		final ULine vbar = ULine.vline(y2 - y1);
		ug.apply(HColorUtils.LIGHT_GRAY).apply(new UTranslate(x, y1)).draw(vbar);
	}

	private void printNamedDays(final UGraphic ug) {
		if (nameDays.size() > 0) {
			String last = null;
			for (Wink wink = min; wink.compareTo(max.increment()) <= 0; wink = wink.increment()) {
				final Day tmpday = calendar.toDayAsDate(wink);
				final String name = nameDays.get(tmpday);
				if (name != null && name.equals(last) == false) {
					final double x1 = getTimeScale().getStartingPosition(wink);
					final double x2 = getTimeScale().getEndingPosition(wink);
					final TextBlock label = getTextBlock(name, 12, false, HColorUtils.BLACK);
					final double h = label.calculateDimension(ug.getStringBounder()).getHeight();
					double y1 = getTimeHeaderHeight();
					double y2 = getFullHeaderHeight();
					label.drawU(ug.apply(new UTranslate(x1, Y_POS_ROW28 + 11)));
				}
				last = name;
			}
		}
	}

	@Override
	public double getFullHeaderHeight() {
		return getTimeHeaderHeight() + getHeaderNameDayHeight();
	}

	private double getHeaderNameDayHeight() {
		if (nameDays.size() > 0) {
			return 16;
		}
		return 0;
	}

}
