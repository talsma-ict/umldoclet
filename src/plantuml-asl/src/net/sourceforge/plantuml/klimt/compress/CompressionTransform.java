/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.klimt.compress;

import java.util.List;

public class CompressionTransform implements PiecewiseAffineTransform {
    // ::remove file when __HAXE__

	private final List<Slot> all;

	public CompressionTransform(SlotSet slotSet) {
		this.all = slotSet.getSlots();
	}

	public double transform(double v) {
		return v - getCompressDelta(v);
	}

	private double getCompressDelta(double v) {
		double result = 0;
		for (Slot s : all) {
			if (s.getStart() > v) {
				continue;
			}
			if (v > s.getEnd()) {
				result += s.size();
			} else {
				result += v - s.getStart();
			}
		}
		return result;
	}

}
