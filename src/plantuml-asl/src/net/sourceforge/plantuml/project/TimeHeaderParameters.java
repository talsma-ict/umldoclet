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

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.api.ThemeStyle;
import net.sourceforge.plantuml.project.time.Day;
import net.sourceforge.plantuml.project.time.DayOfWeek;
import net.sourceforge.plantuml.style.Style;
import net.sourceforge.plantuml.ugraphic.color.HColor;
import net.sourceforge.plantuml.ugraphic.color.HColorSet;

public class TimeHeaderParameters {

	private final Map<Day, HColor> colorDays;
	private final ThemeStyle themeStyle;
	private final double scale;
	private final Day min;
	private final Day max;
	private final HColorSet colorSet;
	private final Style timelineStyle;
	private final Style closedStyle;
	private final Locale locale;
	private final OpenClose openClose;
	private final Map<DayOfWeek, HColor> colorDaysOfWeek;
	private final Set<Day> verticalSeparatorBefore;

	public TimeHeaderParameters(Map<Day, HColor> colorDays, ThemeStyle themeStyle, double scale, Day min, Day max,
			HColorSet colorSet, Style timelineStyle, Style closedStyle, Locale locale, OpenClose openClose,
			Map<DayOfWeek, HColor> colorDaysOfWeek, Set<Day> verticalSeparatorBefore) {
		this.colorDays = colorDays;
		this.themeStyle = themeStyle;
		this.scale = scale;
		this.min = min;
		this.max = max;
		this.colorSet = colorSet;
		this.timelineStyle = timelineStyle;
		this.closedStyle = closedStyle;
		this.locale = locale;
		this.openClose = openClose;
		this.colorDaysOfWeek = colorDaysOfWeek;
		this.verticalSeparatorBefore = verticalSeparatorBefore;
	}

	public HColor getColor(Day wink) {
		return colorDays.get(wink);
	}

	public HColor getColor(DayOfWeek dayOfWeek) {
		return colorDaysOfWeek.get(dayOfWeek);
	}

	public ThemeStyle getThemeStyle() {
		return themeStyle;
	}

	public final double getScale() {
		return scale;
	}

	public final Day getMin() {
		return min;
	}

	public final Day getMax() {
		return max;
	}

	public final HColorSet getColorSet() {
		return colorSet;
	}

	public final Style getTimelineStyle() {
		return timelineStyle;
	}

	public final Style getClosedStyle() {
		return closedStyle;
	}

	public final Locale getLocale() {
		return locale;
	}

	public final LoadPlanable getLoadPlanable() {
		return openClose;
	}

	public Day getStartingDay() {
		return openClose.getStartingDay();
	}

	public final Set<Day> getVerticalSeparatorBefore() {
		return verticalSeparatorBefore;
	}

}
