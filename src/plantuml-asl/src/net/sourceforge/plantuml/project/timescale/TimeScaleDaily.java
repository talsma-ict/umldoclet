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
package net.sourceforge.plantuml.project.timescale;

import net.sourceforge.plantuml.project.core.PrintScale;
import net.sourceforge.plantuml.project.time.Day;

public final class TimeScaleDaily implements TimeScale {

	private final TimeScaleWink basic;
	private final double delta;

	public TimeScaleDaily(double scale, Day calendar, Day zeroDay) {
		this.basic = new TimeScaleWink(scale, PrintScale.DAILY);
		if (zeroDay == null) {
			this.delta = basic.getStartingPosition(calendar);
		} else {
			this.delta = basic.getStartingPosition(zeroDay);
		}

	}

	public double getStartingPosition(Day instant) {
		return basic.getStartingPosition(instant) - delta;
	}

	public double getEndingPosition(Day instant) {
		return basic.getEndingPosition(instant) - delta;
	}

	public double getWidth(Day instant) {
		return basic.getWidth(instant);
	}

	public boolean isBreaking(Day instant) {
		return true;
	}

}
