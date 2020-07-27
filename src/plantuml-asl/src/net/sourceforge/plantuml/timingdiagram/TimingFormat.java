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
package net.sourceforge.plantuml.timingdiagram;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public enum TimingFormat {
	DECIMAL, HOUR, DATE;

	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	private static final GregorianCalendar gc = new GregorianCalendar(TimingFormat.GMT);

	public String formatTime(BigDecimal time) {
		if (this == HOUR || this == DATE) {
			return formatTime(time.longValueExact());
		}
		return time.toPlainString();
	}

	public String formatTime(long time) {
		if (this == HOUR) {
			final int s = (int) time % 60;
			final int m = (int) (time / 60) % 60;
			final int h = (int) (time / 3600);
			return String.format("%d:%02d:%02d", h, m, s);
		}
		if (this == DATE) {
			final int yyyy;
			final int mm;
			final int dd;
			synchronized (gc) {
				gc.setTimeInMillis(time * 1000L);
				yyyy = gc.get(Calendar.YEAR);
				mm = gc.get(Calendar.MONTH) + 1;
				dd = gc.get(Calendar.DAY_OF_MONTH);
			}
			// return String.format("%04d/%02d/%02d", yyyy, mm, dd);
			return String.format("%02d/%02d", mm, dd);
		}
		return "" + time;
	}

	public static TimeTick createDate(final int yyyy, final int mm, final int dd) {
		final long timeInMillis;
		synchronized (gc) {
			gc.setTimeInMillis(0);
			gc.set(yyyy, mm - 1, dd);
			timeInMillis = gc.getTimeInMillis() / 1000L;
		}
		return new TimeTick(new BigDecimal(timeInMillis), TimingFormat.DATE);
	}

}
