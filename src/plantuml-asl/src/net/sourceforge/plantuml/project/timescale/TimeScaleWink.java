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

public class TimeScaleWink implements TimeScale {

	private final double scale;
	private final PrintScale printScale;

	public TimeScaleWink(double scale, PrintScale printScale) {
		this.scale = 16.0 * scale;
		this.printScale = printScale;
	}

	public double getStartingPosition(Day instant) {
		final long wink = instant.getMillis();
		return wink * scale / Day.MILLISECONDS_PER_DAY;
	}

	public double getEndingPosition(Day instant) {
		return getStartingPosition(instant) + getWidth(instant);
	}

	public double getWidth(Day instant) {
		return scale;
	}

	public boolean isBreaking(Day instant) {
		if (printScale == PrintScale.WEEKLY) {
			final long num = instant.getMillis() / Day.MILLISECONDS_PER_DAY;
			return num % 7 == 6;
		}
		return true;
	}

}
