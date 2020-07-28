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
package net.sourceforge.plantuml.project.timescale;

import net.sourceforge.plantuml.project.time.GCalendar;
import net.sourceforge.plantuml.project.time.Wink;

public class TimeScaleCompressed implements TimeScale {

	private final int compress;
	private final TimeScale daily;

	public TimeScaleCompressed(GCalendar calendar, int compress) {
		this.daily = new TimeScaleDaily(calendar, null);
		this.compress = compress;
	}

	public double getStartingPosition(Wink instant) {
		return daily.getStartingPosition(instant) / compress;
	}

	public double getEndingPosition(Wink instant) {
		return daily.getEndingPosition(instant) / compress;
	}

	public double getWidth(Wink instant) {
		return daily.getWidth(instant) / compress;
	}

}
