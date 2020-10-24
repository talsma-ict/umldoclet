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
package net.sourceforge.plantuml.project;

import net.sourceforge.plantuml.project.time.Day;

public class PlanUtils {

	private PlanUtils() {

	}

	public static LoadPlanable minOf(final LoadPlanable p1, final LoadPlanable p2) {
		return new LoadPlanable() {
			public int getLoadAt(Day instant) {
				return Math.min(p1.getLoadAt(instant), p2.getLoadAt(instant));
			}
		};
	}

	public static LoadPlanable multiply(final LoadPlanable p1, final LoadPlanable p2) {
		return new LoadPlanable() {
			public int getLoadAt(Day instant) {
				return p1.getLoadAt(instant) * p2.getLoadAt(instant) / 100;
			}
		};
	}

}
